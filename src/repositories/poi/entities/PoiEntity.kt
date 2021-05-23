package eu.adrianistan.repositories.poi.entities

import eu.adrianistan.poi.entities.GeoJsonPoint
import kotlinx.serialization.Serializable

@Serializable
data class PoiEntity(
    val _id: String? = null,
    val name: String,
    val description: String,
    val type: String,
    val location: GeoJsonPoint
)
