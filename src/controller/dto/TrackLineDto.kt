package eu.adrianistan.controller.dto

import eu.adrianistan.model.TrackLine
import kotlinx.serialization.Serializable

@Serializable
data class TrackLineDto(
    val latA: Double,
    val lonA: Double,
    val latB: Double,
    val lonB: Double,
    val speed: Double,
)

fun TrackLine.toDto(): TrackLineDto = TrackLineDto(
    latA,
    lonA,
    latB,
    lonB,
    speed
)
