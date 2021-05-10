package eu.adrianistan.poi.entities

import kotlinx.serialization.Serializable

@Serializable
data class GeoJsonPoint(
    val type: String = "Point",
    val coordinates: List<Double>
)
