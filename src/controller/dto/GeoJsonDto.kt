package eu.adrianistan.controller.dto

data class GeoJsonDto (
    val type: String = "Feature",
    val geometry: GeometryDto,
    val properties: Any,
)
