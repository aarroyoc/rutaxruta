package eu.adrianistan.controller.dto

data class GeometryDto(
    val type: String = "LineString",
    val coordinates: List<Array<Double>>
)
