package eu.adrianistan.controller.dto

import eu.adrianistan.model.Poi
import kotlinx.serialization.Serializable

@Serializable
data class PoiDto(
    val type: String,
    val name: String,
    val description: String,
    val lat: Double,
    val lon: Double,
) {
    companion object {
        fun fromModel(poi: Poi) =
            PoiDto(
                type = poi.type,
                name = poi.name,
                description = poi.description,
                lat = poi.lat,
                lon = poi.lon
            )
    }
}
