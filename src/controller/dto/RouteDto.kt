package eu.adrianistan.controller.dto

import eu.adrianistan.model.Route

data class RouteDto(
    val id: String,
    val name: String,
    val description: String,
    val comments: List<String>,
    val geojson: GeoJsonDto,
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
            properties = emptyMap<Any, Any>()
        )
    )