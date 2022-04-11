package controller

import io.ktor.http.*
import eu.adrianistan.Factory
import eu.adrianistan.config.JwtConfig
import eu.adrianistan.module
import eu.adrianistan.repositories.poi.entities.PoiEntity
import eu.adrianistan.repositories.route.entities.RouteEntity
import eu.adrianistan.repositories.user.entities.UserEntity
import io.ktor.client.request.*
import io.ktor.client.statement.*
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
    fun `get non-existing route`() = testApplication {
        val response = client.get("/route/12345")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `get existing route`() = testApplication {
        val routeId = saveRoute()
        val response = client.get("/route/$routeId")
        assert(response.bodyAsText().contains("Valladolid - Cigales por Canal de Castilla"))
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `list existing routes`() = testApplication {
        saveRoute()
        val response = client.get("/route")
        assert(response.bodyAsText().contains("Valladolid - Cigales por Canal de Castilla"))
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `should successfully post a new route`() = testApplication {
        val token = generateToken()
        val response = client.post("/route") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(VALID_ROUTE)
        }
        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun `should reject the new route as invalid`() = testApplication {
        val token = generateToken()
        val response = client.post("/route") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(INVALID_ROUTE)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `should successfully delete a route`() = testApplication {
        val routeId = saveRoute()
        val token = generateToken()
        val response = client.delete("/route/$routeId") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.NoContent, response.status)
    }

    @Test
    fun `should give an error when deleting a non-existing route`() = testApplication {
        val token = generateToken()
        val response = client.delete("/route/123456") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `should return a list of near pois`() = testApplication {
        savePoi()
        val routeId = saveRoute()
        val token = generateToken()
        val response = client.get("/route/$routeId/near") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assert(response.bodyAsText().contains("Monumento 1"))
    }

    @Test
    fun `should return an empty list of near pois`() = testApplication {
        val routeId = saveRoute()
        val token = generateToken()
        val response = client.get("/route/$routeId/near") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("[]", response.bodyAsText())
    }

    @Test
    fun `should return 404 if route doesn't exist while looking for pois`() = testApplication {
        val token = generateToken()
        val response = client.get("/route/123456/near") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
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
