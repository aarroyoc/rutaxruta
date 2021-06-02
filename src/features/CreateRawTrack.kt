package eu.adrianistan.features

import eu.adrianistan.model.RawTrack
import eu.adrianistan.model.TrackInfo
import eu.adrianistan.model.User
import eu.adrianistan.repositories.route.RouteRepository
import eu.adrianistan.repositories.track.RawTrackRepository
import io.jenetics.jpx.GPX
import kotlinx.datetime.Clock
import java.util.*

class CreateRawTrack(
    private val rawTrackRepository: RawTrackRepository,
    private val routeRepository: RouteRepository,
) {
    suspend operator fun invoke(name: String, gpxText: String, routeId: String?, user: User) {
        val gpx = GPX.read(gpxText.byteInputStream())
        if(gpx.tracks.isEmpty() || gpx.tracks.map { it.segments }.isEmpty()){
            throw IllegalArgumentException()
        }
        val trackId = UUID.randomUUID().toString()
        val rawTrack = RawTrack(
            id = trackId,
            name = name,
            gpx = gpxText,
            timestamp = Clock.System.now(),
            userId = user.id ?: error("can't add userId to track (no id in user)")
        )
        rawTrackRepository.saveRawTrack(rawTrack)
        routeId?.let {
            routeRepository.addTrack(routeId, TrackInfo(trackId, name))
        }
    }
}