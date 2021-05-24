package eu.adrianistan.controller

import eu.adrianistan.controller.dto.GeoJsonDto
import eu.adrianistan.controller.dto.PoiDto
import eu.adrianistan.controller.dto.RouteCreateRequest
import eu.adrianistan.controller.dto.toDto
import eu.adrianistan.model.Route as RouteModel
import eu.adrianistan.model.RoutePoint
import eu.adrianistan.model.User
import eu.adrianistan.repositories.poi.PoiRepository
import eu.adrianistan.repositories.route.RouteRepository
import eu.adrianistan.usecase.GetPoisNearRoute
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.routeRouting() {
    val routeRepository = RouteRepository()
    val poiRepository = PoiRepository()

    val getPoisNearRoute = GetPoisNearRoute(routeRepository, poiRepository)

    route("/route") {
        get {
            val routes = routeRepository.listRoutes().map { it.toDto() }
            call.respond(routes)
        }

        get("{id}") {
            val routeId = call.parameters["id"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
            val route = routeRepository.getRouteById(routeId)
            if(route != null){
                call.respond(route.toDto())
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }

        get("{id}/near") {
            val routeId = call.parameters["id"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
            getPoisNearRoute(routeId)?.let { pois ->
                val poisDto = pois.map {
                    PoiDto.fromModel(it)
                }
                call.respond(poisDto)
            } ?: run {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }

        authenticate {

            post {
                try {
                    val request = call.receive<RouteCreateRequest>()
                    val user = call.authentication.principal<User>()
                    if (user != null) {
                        routeRepository.saveRoute(
                            RouteModel(
                                id = null,
                                name = request.name,
                                description = request.description,
                                comments = emptyList(),
                                media = emptyList(),
                                points = convertGeoJsonToPoints(request.geojson),
                                userId = user.id ?: error("Invalid user"),
                                status = eu.adrianistan.model.Route.RouteState.IN_REVIEW
                            )
                        )?.let {
                            call.respondText(it, status = HttpStatusCode.Created)
                        } ?: call.respondText("Server Error", status = HttpStatusCode.InternalServerError)
                    }
                } catch (e: Exception) {
                    call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
                }
            }

            delete("{id}") {
                val routeId = call.parameters["id"] ?: return@delete call.respondText(
                    "Bad Request",
                    status = HttpStatusCode.BadRequest
                )
                if (routeRepository.deleteRoute(routeId)) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respondText("Nout Found", status = HttpStatusCode.NotFound)
                }
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