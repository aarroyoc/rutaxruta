package eu.adrianistan.route.entities

import kotlinx.serialization.Serializable

@Serializable
data class GeoJsonLineString(
    val type: String = "LineString",
    val coordinates: List<Array<Double>>
)
