package eu.adrianistan.features

import eu.adrianistan.model.RawTrack
import eu.adrianistan.repositories.track.RawTrackRepository
import io.jenetics.jpx.GPX
import kotlinx.datetime.Clock

class CreateRawTrack(
    private val rawTrackRepository: RawTrackRepository
) {
    suspend operator fun invoke(name: String, gpxText: String) {
        val gpx = GPX.read(gpxText.byteInputStream())
        if(gpx.tracks.isEmpty() || gpx.tracks.map { it.segments }.isEmpty()){
            throw IllegalArgumentException()
        }
        val rawTrack = RawTrack(
            id = null,
            name = name,
            gpx = gpxText,
            timestamp = Clock.System.now()
        )
        rawTrackRepository.saveRawTrack(rawTrack)
    }
}