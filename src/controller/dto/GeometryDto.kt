package eu.adrianistan.controller.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeometryDto(
    val type: String = "LineString",
    val coordinates: List<Array<Double>>
)
