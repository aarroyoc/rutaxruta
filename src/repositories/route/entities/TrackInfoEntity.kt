package eu.adrianistan.repositories.route.entities

import eu.adrianistan.model.TrackInfo
import kotlinx.serialization.Serializable

@Serializable
data class TrackInfoEntity(
    val trackId: String,
    val name: String,
) {
    fun toModel(): TrackInfo =
        TrackInfo(
            trackId = trackId,
            name = name
        )
}

fun TrackInfo.toEntity(): TrackInfoEntity =
    TrackInfoEntity(
        trackId = trackId,
        name = name
    )