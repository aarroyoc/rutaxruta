package eu.adrianistan.controller

import eu.adrianistan.controller.dto.TrackCreateRequest
import eu.adrianistan.controller.dto.toDto
import eu.adrianistan.features.CreateRawTrack
import eu.adrianistan.model.User
import eu.adrianistan.repositories.track.RawTrackRepository
import eu.adrianistan.usecase.GetTrack
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import kotlinx.datetime.Clock

fun Route.trackRouting() {
    val rawTrackRepository = RawTrackRepository()

    val getTrack = GetTrack(rawTrackRepository)
    val createRawTrack = CreateRawTrack(rawTrackRepository)

    route("/track") {
        get("{id}") {
            val trackId = call.parameters.getOrFail("id")
            val track = getTrack(trackId)
            track?.let {
                call.respond(it.toDto())
            } ?: throw NotFoundException()
        }

        authenticate {
            post {
                val request = call.receive<TrackCreateRequest>()
                val user = call.authentication.principal<User>()
                if (user != null) {
                    try {
                        createRawTrack(request.name, request.gpx)
                        call.respond(HttpStatusCode.Created)
                    } catch (e: Exception) {
                        throw BadRequestException("invalid gpx file")
                    }
                } else {
                    throw BadRequestException("invalid username")
                }

            }
        }
    }
}