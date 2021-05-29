package controller

import io.ktor.http.*
import eu.adrianistan.Factory
import eu.adrianistan.config.JwtConfig
import eu.adrianistan.module
import eu.adrianistan.repositories.poi.entities.PoiEntity
import eu.adrianistan.repositories.route.entities.RouteEntity
import eu.adrianistan.repositories.user.entities.UserEntity
import kotlin.test.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import mother.PoiMother
import mother.RouteMother
import mother.UserMother

class RouteControllerTest {
    @BeforeTest
    fun cleanDatabase() {
        runBlocking {
            Factory.getDatabase().dropCollection("route")
            Factory.getDatabase().dropCollection("user")
            Factory.getDatabase().dropCollection("poi")
        }
    }
    @Test
    fun `get non-existing route`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/route/12345").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun `get existing route`() {
        val routeId = runBlocking {
            saveRoute()
        }
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/route/$routeId").apply {
                assert(response.content?.contains("Valladolid - Cigales por Canal de Castilla") ?: false)
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    @Test
    fun `list existing routes`() {
        runBlocking {
            saveRoute()
        }
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/route").apply {
                assert(response.content?.contains("Valladolid - Cigales por Canal de Castilla") ?: false)
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    @Test
    fun `should successfully post a new route`() {
        val token = runBlocking {
            generateToken()
        }
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/route") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Authorization, "Bearer $token")
                setBody(VALID_ROUTE)
            }.apply {
                assertEquals(HttpStatusCode.Created, response.status())
            }
        }
    }

    @Test
    fun `should reject the new route as invalid`() {
        val token = runBlocking {
            generateToken()
        }
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/route") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Authorization, "Bearer $token")
                setBody(INVALID_ROUTE)
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun `should successfully delete a route`() {
        val routeId = runBlocking {
            saveRoute()
        }
        val token = runBlocking {
            generateToken()
        }
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Delete, "/route/$routeId") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }.apply {
                assertEquals(HttpStatusCode.NoContent, response.status())
            }
        }
    }

    @Test
    fun `should give an error when deleting a non-existing route`() {
        val token = runBlocking {
            generateToken()
        }
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Delete, "/route/123456") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun `should return a list of near pois`() {
        val routeId = runBlocking {
            savePoi()
            saveRoute()
        }
        val token = runBlocking {
            generateToken()
        }
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/route/$routeId/near") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assert(response.content?.contains("Monumento 1") ?: false)
            }
        }
    }

    @Test
    fun `should return an empty list of near pois`() {
        val routeId = runBlocking {
            saveRoute()
        }
        val token = runBlocking {
            generateToken()
        }
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/route/$routeId/near") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(response.content, "[]")
            }
        }
    }

    @Test
    fun `should return 404 if route doesn't exist while looking for pois`() {
        val token = runBlocking {
            generateToken()
        }
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/route/123456/near") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    private suspend fun saveRoute(): String? {
        val collection = Factory.getDatabase().getCollection<RouteEntity>("route")
        collection.insertOne(RouteMother.buildEntity().copy(_id = "608f1cdd2305357e6a875934"))
        return collection.find().first()?._id
    }

    private suspend fun savePoi(): String? {
        val collection = Factory.getDatabase().getCollection<PoiEntity>("poi")
        collection.insertOne(PoiMother.buildEntity())
        return collection.find().first()?._id
    }

    private suspend fun generateToken(): String {
        val collection = Factory.getDatabase().getCollection<UserEntity>("user")
        collection.insertOne(UserMother.buildEntity())
        return JwtConfig.makeToken(UserMother.build())
    }

    companion object {
        private const val VALID_ROUTE = """
        {
            "name": "Valladolid - Cigales por Canal de Castilla",
            "description": "Vamos de Valladolid a Cigales siguiendo el Canal de Castilla<br>Pasamos al lado del ITaCyl",
            "geojson": {
                "type": "Feature",
                "properties": {},
                "geometry": {
                    "type": "LineString",
                    "coordinates": [
                    [
                    -4.733734130859375,
                    41.66008825124748
                    ],
                    [
                    -4.725494384765625,
                    41.671116673793016
                    ],
                    [
                    -4.722576141357422,
                    41.67893801438407
                    ],
                    [
                    -4.7303009033203125,
                    41.68406624635085
                    ],
                    [
                    -4.739913940429687,
                    41.67855338051137
                    ],
                    [
                    -4.734592437744141,
                    41.67445047616417
                    ],
                    [
                    -4.7371673583984375,
                    41.67265537326585
                    ],
                    [
                    -4.736137390136719,
                    41.66957793753977
                    ],
                    [
                    -4.742145538330078,
                    41.66162721430806
                    ]
                    ]
                }
            }
        }
        """
        private const val INVALID_ROUTE = """
        {
            "name": "Valladolid - Cigales por Canal de Castilla",
            "description": "Vamos de Valladolid a Cigales siguiendo el Canal de Castilla<br>Pasamos al lado del ITaCyl"
        }    
        """
    }
}
