package controller

import eu.adrianistan.Factory
import eu.adrianistan.config.JwtConfig
import eu.adrianistan.module
import eu.adrianistan.repositories.route.entities.RouteEntity
import eu.adrianistan.repositories.track.entities.RawTrackEntity
import eu.adrianistan.repositories.user.entities.UserEntity
import io.ktor.client.request.*
import io.ktor.client.statement.*
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
    fun `get a single track`() = testApplication {
        generateToken()
        saveRawTrack()
        val response = client.get("/track/$SOME_TRACK_ID")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `get a non-existant single track`() = testApplication {
        val response = client.get("/track/$SOME_OTHER_TRACK_ID")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `submit a valid track`() = testApplication {
        val token = generateToken()
        val gpxText = File("test/track/castrodeza.gpx").readText().replace("\"", "\\\"")
        val response = client.post("/track") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody("""{"name": "Castrodeza Ida", "gpx": "$gpxText"}""")
        }
        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun `submit a valid track and associate with a route`() = testApplication {
        saveRoute()
        val token = generateToken()
        val gpxText = File("test/track/castrodeza.gpx").readText().replace("\"", "\\\"")
        val response = client.post("/track") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody("""{"name": "Castrodeza Ida", "gpx": "$gpxText", "routeId": "$SOME_ROUTE_ID"}""")
        }
        assertEquals(HttpStatusCode.Created, response.status)

        val response2 = client.get("/route/$SOME_ROUTE_ID")
        assertEquals(HttpStatusCode.OK, response2.status)
        assert(response2.bodyAsText().contains("Castrodeza Ida"))
    }

    @Test
    fun `submit a valid track, associate with a route and delete it`() = testApplication {
        saveRoute()
        val token = generateToken()

        val gpxText = File("test/track/castrodeza.gpx").readText().replace("\"", "\\\"")

        val response = client.post("/track") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody("""{"name": "Castrodeza Ida", "gpx": "$gpxText", "routeId": "$SOME_ROUTE_ID"}""")
        }
        assertEquals(HttpStatusCode.Created, response.status)

        val response2 = client.get("/route/$SOME_ROUTE_ID")
        assertEquals(HttpStatusCode.OK, response2.status)
        assert(response2.bodyAsText().contains("Castrodeza Ida"))

        val trackId = getTrackId()
        val response3 = client.delete("/track/$trackId") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.NoContent, response3.status)

        val response4 = client.get("/route/$SOME_ROUTE_ID")
        assertEquals(HttpStatusCode.OK, response4.status)
        assertFalse(response4.bodyAsText().contains("Castrodeza Ida"))
    }

    @Test
    fun `submit an invalid track`() = testApplication {
        val token = generateToken()
        val gpxText = File("test/track/no-tracks.gpx").readText().replace("\"", "\\\"")

        val response = client.post("/track") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody("""{"name": "Castrodeza Ida", "gpx": "$gpxText"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `delete an existing track`() = testApplication {
        val token = generateToken()
        saveRawTrack()

        val response = client.delete("/track/$SOME_TRACK_ID") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.NoContent, response.status)

        val response2 = client.get("/track/$SOME_TRACK_ID")
        assertEquals(HttpStatusCode.NotFound, response2.status)
    }

    @Test
    fun `delete a track from other user`() = testApplication {
        generateToken()
        saveRawTrack()
        val token = generateTokenOtherUser()

        val response = client.delete("/track/$SOME_TRACK_ID") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `get tracks of a non-existing user`() = testApplication {
        val response = client.get("/track?user=pepito")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("[]", response.bodyAsText())
    }

    @Test
    fun `get tracks of a user`() = testApplication {
        val token = generateToken()
        saveRawTrack()

        val response = client.get("/track?user=$SOME_USER_ID") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("[{\"trackId\":\"123456\",\"name\":\"Castrodeza Ida\"}]", response.bodyAsText())
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