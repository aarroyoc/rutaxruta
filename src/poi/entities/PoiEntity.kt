package eu.adrianistan.poi.entities

import kotlinx.serialization.Serializable

@Serializable
data class PoiEntity(
    val _id: String? = null,
    val name: String,
    val description: String,
    val location: GeoJsonPoint
)
