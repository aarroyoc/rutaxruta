package eu.adrianistan.controller.dto

import kotlinx.serialization.Serializable

@Serializable
data class RouteCreateRequest (
    val name: String,
    val description: String,
    val geojson: GeoJsonDto,
)
