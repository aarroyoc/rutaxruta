package eu.adrianistan.controller

import eu.adrianistan.controller.dto.GeoJsonDto
import eu.adrianistan.controller.dto.RouteCreateRequest
import eu.adrianistan.model.Route as RouteModel
import eu.adrianistan.model.RoutePoint
import eu.adrianistan.model.User
import eu.adrianistan.route.RouteRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.routeRouting() {
    val routeRepository = RouteRepository()

    route("/route") {
        get {
            val routes = routeRepository.listRoutes()
            call.respond(routes)
        }

        get("{id}") {
            val routeId = call.parameters["id"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
            val route = routeRepository.getRouteById(routeId)
            if(route != null){
                call.respond(route)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }

        post {
            try {
                val request = call.receive<RouteCreateRequest>()
                routeRepository.saveRoute(
                    RouteModel(
                        id = null,
                        name = request.name,
                        description = request.description,
                        comments = emptyList(),
                        media = emptyList(),
                        points = convertGeoJsonToPoints(request.geojson),
                        author = User(
                            nickname = "aarroyoc",
                            email = "adrian.arroyocalle@gmail.com"
                        )
                    )
                )?.let {
                    call.respondText(it, status = HttpStatusCode.Created)
                } ?: call.respondText("Server Error", status = HttpStatusCode.InternalServerError)
            } catch (e: Exception) {
                call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
            }
        }

        delete("{id}") {
            val routeId = call.parameters["id"] ?: return@delete call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
            if(routeRepository.deleteRoute(routeId)){
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respondText("Nout Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}

private fun convertGeoJsonToPoints(geojson: GeoJsonDto): List<RoutePoint> =
    geojson.geometry.coordinates.map {
        RoutePoint(
            lat = it[1],
            lon = it[0],
        )
    }