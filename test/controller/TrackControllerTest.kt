package controller

import eu.adrianistan.Factory
import eu.adrianistan.config.JwtConfig
import eu.adrianistan.module
import eu.adrianistan.repositories.route.entities.RouteEntity
import eu.adrianistan.repositories.track.entities.RawTrackEntity
import eu.adrianistan.repositories.user.entities.UserEntity
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import mother.RawTrackMother
import mother.RawTrackMother.SOME_OTHER_TRACK_ID
import mother.RawTrackMother.SOME_TRACK_ID
import mother.RouteMother
import mother.RouteMother.SOME_ROUTE_ID
import mother.UserMother
import mother.UserMother.SOME_USER_ID
import java.io.File
import kotlin.test.*

class TrackControllerTest {

    @BeforeTest
    fun cleanDatabase() {
        runBlocking {
            Factory.getDatabase().dropCollection("track")
            Factory.getDatabase().dropCollection("user")
            Factory.getDatabase().dropCollection("route")
        }
    }

    @Test
    fun `get a single track`() {
        runBlocking {
            generateToken()
            saveRawTrack()
        }
        withTestApplication({ module(testing = true)}) {
            handleRequest(HttpMethod.Get, "/track/$SOME_TRACK_ID").apply {
                assertEquals(HttpStatusCode.OK, this.response.status())
            }
        }
    }

    @Test
    fun `get a non-existant single track`() {
        withTestApplication({ module(testing = true)}) {
            handleRequest(HttpMethod.Get, "/track/$SOME_OTHER_TRACK_ID").apply {
                assertEquals(HttpStatusCode.NotFound, this.response.status())
            }
        }
    }

    @Test
    fun `submit a valid track`() {
        val token = runBlocking {
            generateToken()
        }

        val gpxText = File("test/track/castrodeza.gpx").readText().replace("\"", "\\\"")

        withTestApplication({ module(testing = true)}) {
            handleRequest(HttpMethod.Post, "/track") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Authorization, "Bearer $token")
                setBody("""{"name": "Castrodeza Ida", "gpx": "$gpxText"}""")
            }.apply {
                assertEquals(HttpStatusCode.Created, this.response.status())
            }
        }
    }

    @Test
    fun `submit a valid track and associate with a route`() {
        val token = runBlocking {
            saveRoute()
            generateToken()
        }

        val gpxText = File("test/track/castrodeza.gpx").readText().replace("\"", "\\\"")

        withTestApplication({ module(testing = true)}) {
            handleRequest(HttpMethod.Post, "/track") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Authorization, "Bearer $token")
                setBody("""{"name": "Castrodeza Ida", "gpx": "$gpxText", "routeId": "$SOME_ROUTE_ID"}""")
            }.apply {
                assertEquals(HttpStatusCode.Created, this.response.status())
            }

            handleRequest(HttpMethod.Get, "/route/$SOME_ROUTE_ID").apply {
                assertEquals(HttpStatusCode.OK, this.response.status())
                assert(this.response.content?.contains("Castrodeza Ida") ?: false)
            }
        }
    }

    @Test
    fun `submit a valid track, associate with a route and delete it`() {
        val token = runBlocking {
            saveRoute()
            generateToken()
        }

        val gpxText = File("test/track/castrodeza.gpx").readText().replace("\"", "\\\"")

        withTestApplication({ module(testing = true)}) {
            handleRequest(HttpMethod.Post, "/track") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Authorization, "Bearer $token")
                setBody("""{"name": "Castrodeza Ida", "gpx": "$gpxText", "routeId": "$SOME_ROUTE_ID"}""")
            }.apply {
                assertEquals(HttpStatusCode.Created, this.response.status())
            }

            handleRequest(HttpMethod.Get, "/route/$SOME_ROUTE_ID").apply {
                assertEquals(HttpStatusCode.OK, this.response.status())
                assert(this.response.content?.contains("Castrodeza Ida") ?: false)
            }

            val trackId = runBlocking {
                getTrackId()
            }

            handleRequest(HttpMethod.Delete, "/track/$trackId") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }.apply {
                assertEquals(HttpStatusCode.NoContent, this.response.status())
            }

            handleRequest(HttpMethod.Get, "/route/$SOME_ROUTE_ID").apply {
                assertEquals(HttpStatusCode.OK, this.response.status())
                assertFalse(this.response.content?.contains("Castrodeza Ida") ?: true)
            }
        }
    }

    @Test
    fun `submit an invalid track`() {
        val token = runBlocking {
            generateToken()
        }
        val gpxText = File("test/track/no-tracks.gpx").readText().replace("\"", "\\\"")

        withTestApplication({ module(testing = true)}) {
            handleRequest(HttpMethod.Post, "/track"){
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Authorization, "Bearer $token")
                setBody("""{"name": "Castrodeza Ida", "gpx": "$gpxText"}""")
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, this.response.status())
            }
        }
    }

    @Test
    fun `delete an existing track`() {
        val token = runBlocking {
            generateToken()
        }
        runBlocking {
            saveRawTrack()
        }

        withTestApplication({module(testing = true)}) {
            handleRequest(HttpMethod.Delete, "/track/$SOME_TRACK_ID") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }.apply {
                assertEquals(HttpStatusCode.NoContent, this.response.status())
            }
            handleRequest(HttpMethod.Get, "/track/$SOME_TRACK_ID").apply {
                assertEquals(HttpStatusCode.NotFound, this.response.status())
            }
        }
    }

    @Test
    fun `delete a track from other user`() {
        val token = runBlocking {
            generateToken()
            saveRawTrack()
            generateTokenOtherUser()
        }
        withTestApplication({module(testing = true)}) {
            handleRequest(HttpMethod.Delete, "/track/$SOME_TRACK_ID") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }.apply {
                assertEquals(HttpStatusCode.NotFound, this.response.status())
            }
        }
    }

    @Test
    fun `get tracks of a non-existing user`() {
        withTestApplication({module(testing = true)}) {
            handleRequest(HttpMethod.Get, "/track?user=pepito").apply {
                assertEquals(HttpStatusCode.OK, this.response.status())
                assertEquals("[]", this.response.content)
            }
        }
    }

    @Test
    fun `get tracks of a user`() {
        val token = runBlocking {
            generateToken()
        }
        runBlocking {
            saveRawTrack()
        }

        withTestApplication({module(testing = true)}) {
            handleRequest(HttpMethod.Get, "/track?user=$SOME_USER_ID") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }.apply {
                assertEquals(HttpStatusCode.OK, this.response.status())
                assertEquals("[{\"trackId\":\"123456\",\"name\":\"Castrodeza Ida\"}]", this.response.content)
            }
        }
    }

    private suspend fun getTrackId(): String? {
        val collection = Factory.getDatabase().getCollection<RawTrackEntity>("track")
        return collection.findOne()?._id
    }

    private suspend fun saveRawTrack() {
        val collection = Factory.getDatabase().getCollection<RawTrackEntity>("track")
        collection.insertOne(RawTrackMother.buildEntity())
    }

    private suspend fun saveRoute(): String? {
        val collection = Factory.getDatabase().getCollection<RouteEntity>("route")
        collection.insertOne(RouteMother.buildEntity().copy(_id = SOME_ROUTE_ID))
        return collection.find().first()?._id
    }

    private suspend fun generateToken(): String {
        val collection = Factory.getDatabase().getCollection<UserEntity>("user")
        collection.insertOne(UserMother.buildEntity())
        return JwtConfig.makeToken(UserMother.build())
    }

    private suspend fun generateTokenOtherUser(): String {
        val collection = Factory.getDatabase().getCollection<UserEntity>("user")
        collection.insertOne(UserMother.buildEntity().copy(_id = "34567"))
        return JwtConfig.makeToken(UserMother.build().copy(id = "34567"))
    }
}