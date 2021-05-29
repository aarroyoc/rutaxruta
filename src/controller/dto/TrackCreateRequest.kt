package eu.adrianistan.controller.dto

import eu.adrianistan.model.RawTrack
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class TrackCreateRequest(
    val name: String,
    val gpx: String,
) {
    fun toRawTrack(timestamp: Instant): RawTrack =
        RawTrack(
            id = null,
            name = this.name,
            gpx = this.gpx,
            timestamp = timestamp
        )
}
