package eu.adrianistan.repositories.track.entities

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class RawTrackEntity(
    val _id: String? = null,
    val name: String,
    val gpx: String,
    val timestamp: Instant
)