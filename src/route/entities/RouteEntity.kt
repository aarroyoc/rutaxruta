package eu.adrianistan.route.entities

import kotlinx.serialization.Serializable

@Serializable
data class RouteEntity (
    val _id: String? = null,
    val name: String,
    val description: String,
    val comments: List<String>,
    val points: GeoJsonLineString,
)
