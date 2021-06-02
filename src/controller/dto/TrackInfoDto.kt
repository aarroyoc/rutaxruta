package eu.adrianistan.controller.dto

import eu.adrianistan.model.TrackInfo
import kotlinx.serialization.Serializable

@Serializable
data class TrackInfoDto(
    val trackId: String,
    val name: String,
)

fun TrackInfo.toDto(): TrackInfoDto =
    TrackInfoDto(
        trackId,
        name
    )
