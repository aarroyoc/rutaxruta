package controller

import io.ktor.http.*
import eu.adrianistan.Factory
import eu.adrianistan.module
import eu.adrianistan.route.entities.RouteEntity
import kotlin.test.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import mother.RouteMother
import org.litote.kmongo.*

class RouteControllerTest {
    @AfterTest
    fun cleanDatabase() {
        runBlocking {
            Factory.getDatabase().dropCollection("route")
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
                assertEquals(ROUTE_JSON, response.content)
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
                assertEquals("[ $ROUTE_JSON ]", response.content)
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    @Test
    fun `should successfully post a new route`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/route") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(VALID_ROUTE)
            }.apply {
                assertEquals(HttpStatusCode.Created, response.status())
            }
        }
    }

    @Test
    fun `should reject the new route as invalid`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/route") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
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
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Delete, "/route/$routeId").apply {
                assertEquals(HttpStatusCode.NoContent, response.status())
            }
        }
    }

    @Test
    fun `should give an error when deleting a non-existing route`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Delete, "/route/123456").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    private suspend fun saveRoute(): String? {
        val collection = Factory.getDatabase().getCollection<RouteEntity>("route")
        collection.insertOne(RouteMother.buildEntity().copy(_id = "608f1cdd2305357e6a875934"))
        return collection.find().first()?._id
    }

    companion object {
        private const val ROUTE_JSON = """{
  "id" : "608f1cdd2305357e6a875934",
  "name" : "Valladolid - Cigales por Canal de Castilla",
  "description" : "Vamos de Valladolid a Cigales siguiendo el Canal de Castilla<br>Pasamos al lado del ITaCyl",
  "comments" : [ ],
  "media" : [ ],
  "points" : [ {
    "lat" : 41.66008825124748,
    "lon" : -4.733734130859375
  }, {
    "lat" : 41.671116673793016,
    "lon" : -4.725494384765625
  }, {
    "lat" : 41.67893801438407,
    "lon" : -4.722576141357422
  }, {
    "lat" : 41.68406624635085,
    "lon" : -4.7303009033203125
  }, {
    "lat" : 41.67855338051137,
    "lon" : -4.739913940429687
  }, {
    "lat" : 41.67445047616417,
    "lon" : -4.734592437744141
  }, {
    "lat" : 41.67265537326585,
    "lon" : -4.7371673583984375
  }, {
    "lat" : 41.66957793753977,
    "lon" : -4.736137390136719
  }, {
    "lat" : 41.66162721430806,
    "lon" : -4.742145538330078
  } ],
  "author" : {
    "nickname" : "TODO",
    "email" : "TODO"
  }
}"""
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
