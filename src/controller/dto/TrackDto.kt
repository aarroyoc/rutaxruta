package eu.adrianistan.controller.dto

import eu.adrianistan.model.Track
import kotlinx.serialization.Serializable

@Serializable
data class TrackDto(
    val name: String,
    val userId: String,
    val userName: String,
    val segments: List<TrackLineDto>,
    val maxSpeed: Double,
    val minSpeed: Double,
    val duration: Long?,
    val distance: Long,
    val meanSpeed: Double?,
)

fun Track.toDto(): TrackDto = TrackDto(
    name = name,
    userId = user.id ?: "",
    userName = user.name,
    maxSpeed = maxSpeed,
    minSpeed = minSpeed,
    segments = segments.map { it.toDto() },
    duration = duration,
    distance = distance,
    meanSpeed = meanSpeed
)



