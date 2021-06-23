package eu.adrianistan.repositories.route.entities

import eu.adrianistan.route.entities.GeoJsonLineString
import kotlinx.serialization.Serializable

@Serializable
data class RouteEntity (
    val _id: String? = null,
    val name: String,
    val description: String,
    val comments: List<String>,
    val points: GeoJsonLineString,
    val userId: String,
    val status: String,
    val tracks: List<TrackInfoEntity>,
    val createdAt: String,
    val updatedAt: String
)
