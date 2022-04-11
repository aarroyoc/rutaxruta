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
import eu.adrianistan.repositories.user.UserRepository
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*


fun Route.trackRouting() {
    val rawTrackRepository = RawTrackRepository()
    val routeRepository = RouteRepository()
    val userRepository = UserRepository()

    val getTrack = GetTrack(rawTrackRepository, userRepository)
    val previewTrack = PreviewTrack()
    val createRawTrack = CreateRawTrack(rawTrackRepository, routeRepository)
    val deleteRawTrack = DeleteRawTrack(rawTrackRepository, routeRepository)

    route("/track") {
        get {
            val userId = call.request.queryParameters.getOrFail("user")
            val tracksInfo = rawTrackRepository.getTracksInfoByUserId(userId)
            val tracksInfoDto = tracksInfo.map { it.toDto() }
            call.respond(tracksInfoDto)
        }

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