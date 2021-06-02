package eu.adrianistan.features

import eu.adrianistan.model.User
import eu.adrianistan.repositories.track.RawTrackRepository

class DeleteRawTrack(
    private val rawTrackRepository: RawTrackRepository
) {
    suspend operator fun invoke(id: String, user: User) =
        user.id?.let {
            rawTrackRepository.deleteRawTrack(id, user.id)
        } ?: error("invalid user (no userId)")
}