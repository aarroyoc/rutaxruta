package eu.adrianistan.controller.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeoJsonDto (
    val type: String = "Feature",
    val geometry: GeometryDto,
    val properties: Map<String, String>
)
