package eu.adrianistan.controller.dto

import eu.adrianistan.model.Route
import kotlinx.serialization.Serializable

@Serializable
data class RouteDto(
    val id: String,
    val name: String,
    val description: String,
    val comments: List<String>,
    val geojson: GeoJsonDto,
    val tracks: List<TrackInfoDto>
)

private fun RouteDto.toModel(): Route =
    error("TODO")

fun Route.toDto(): RouteDto =
    RouteDto(
        id = this.id ?: error("Can't convert to RouteDto"),
        name = this.name,
        description = this.description,
        comments = emptyList(),
        geojson = GeoJsonDto(
            geometry = GeometryDto(
                coordinates = this.points.map { point ->
                    arrayOf(point.lon, point.lat)
                }
            ),
            properties = emptyMap<String, String>()
        ),
        tracks = this.tracks.map { it.toDto() }
    )