package eu.adrianistan.features

import eu.adrianistan.model.Track
import eu.adrianistan.repositories.track.RawTrackRepository
import eu.adrianistan.repositories.user.UserRepository
import eu.adrianistan.track.ProcessGpxTrack

class GetTrack(
    private val rawTrackRepository: RawTrackRepository,
    private val userRepository: UserRepository,
) {
    private val processGpxTrack = ProcessGpxTrack()

    suspend operator fun invoke(id: String): Track? =
        rawTrackRepository.getTrackById(id)?.let { rawTrack ->
            userRepository.getUser(rawTrack.userId)?.let { user ->
                processGpxTrack(rawTrack.gpx.byteInputStream(), user, rawTrack.name)
            }
        }
}