package eu.adrianistan.model

import kotlinx.datetime.Instant

data class RawTrack(
    val id: String?,
    val name: String,
    val gpx: String,
    val timestamp: Instant,
    val userId: String,
)
