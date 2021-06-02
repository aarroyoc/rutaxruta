package eu.adrianistan.controller.dto

import kotlinx.serialization.Serializable

@Serializable
data class TrackCreateRequest(
    val name: String,
    val gpx: String,
    val routeId: String? = null
)
