package eu.adrianistan.controller.dto

import eu.adrianistan.model.Track
import kotlinx.serialization.Serializable

@Serializable
data class TrackDto(
    val segments: List<TrackLineDto>,
    val maxSpeed: Double,
    val minSpeed: Double,
)

fun Track.toDto(): TrackDto = TrackDto(
    maxSpeed = maxSpeed,
    minSpeed = minSpeed,
    segments = segments.map { it.toDto() }
)



