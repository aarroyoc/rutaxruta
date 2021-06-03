package eu.adrianistan.features

import eu.adrianistan.model.User
import eu.adrianistan.repositories.route.RouteRepository
import eu.adrianistan.repositories.track.RawTrackRepository

class DeleteRawTrack(
    private val rawTrackRepository: RawTrackRepository,
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(id: String, user: User) =
        user.id?.let {
            routeRepository.removeTrack(id)
            rawTrackRepository.deleteRawTrack(id, user.id)
        } ?: error("invalid user (no userId)")
}