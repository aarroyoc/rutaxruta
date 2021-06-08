package eu.adrianistan.controller

import eu.adrianistan.controller.dto.TrackCreateRequest
import eu.adrianistan.controller.dto.toDto
import eu.adrianistan.features.CreateRawTrack
import eu.adrianistan.features.DeleteRawTrack
import eu.adrianistan.model.User
import eu.adrianistan.repositories.route.RouteRepository
import eu.adrianistan.repositories.track.RawTrackRepository
import eu.adrianistan.features.GetTrack
import eu.adrianistan.features.PreviewTrack
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*


fun Route.trackRouting() {
    val rawTrackRepository = RawTrackRepository()
    val routeRepository = RouteRepository()

    val getTrack = GetTrack(rawTrackRepository)
    val previewTrack = PreviewTrack()
    val createRawTrack = CreateRawTrack(rawTrackRepository, routeRepository)
    val deleteRawTrack = DeleteRawTrack(rawTrackRepository, routeRepository)

    route("/track") {
        get("{id}") {
            val trackId = call.parameters.getOrFail("id")
            val track = getTrack(trackId)
            track?.let {
                call.respond(it.toDto())
            } ?: throw NotFoundException()
        }

        authenticate {
            post("/preview") {
                val request = call.receive<TrackCreateRequest>()
                val preview = previewTrack(request.gpx)
                call.respond(preview.toDto())
            }
            
            post {
                val request = call.receive<TrackCreateRequest>()
                val user = call.authentication.principal<User>()
                if (user != null) {
                    try {
                        createRawTrack(request.name, request.gpx, request.routeId, user)
                        call.respond(HttpStatusCode.Created)
                    } catch (e: Exception) {
                        throw BadRequestException("invalid gpx file")
                    }
                } else {
                    throw BadRequestException("invalid username")
                }

            }

            delete("{id}") {
                val trackId = call.parameters.getOrFail("id")
                val user = call.authentication.principal<User>()
                if(user != null){
                    if(deleteRawTrack(trackId, user)){
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        throw NotFoundException("non existent id or not allowed")
                    }
                } else {
                    throw BadRequestException("invalid username")
                }
            }
        }
    }
}