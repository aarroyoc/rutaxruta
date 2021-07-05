package eu.adrianistan.controller.dto

import eu.adrianistan.model.TrackPoint
import kotlinx.serialization.Serializable

@Serializable
data class TrackPointDto(
    val distance: Double,
    val elevation: Double
)

fun TrackPoint.toDto() = TrackPointDto(
    distance = distance,
    elevation = elevation
)

