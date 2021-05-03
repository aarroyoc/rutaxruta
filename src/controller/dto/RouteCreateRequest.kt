package eu.adrianistan.controller.dto

data class RouteCreateRequest (
    val name: String,
    val description: String,
    val geojson: GeoJsonDto,
)
