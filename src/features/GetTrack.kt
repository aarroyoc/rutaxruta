package eu.adrianistan.features

import eu.adrianistan.model.Track
import eu.adrianistan.repositories.track.RawTrackRepository
import eu.adrianistan.track.ProcessGpxTrack

class GetTrack(
    private val rawTrackRepository: RawTrackRepository,
) {
    private val processGpxTrack = ProcessGpxTrack()

    suspend operator fun invoke(id: String): Track? =
        rawTrackRepository.getTrackById(id)?.let {
            processGpxTrack(it.gpx.byteInputStream(), it.name)
        }
}