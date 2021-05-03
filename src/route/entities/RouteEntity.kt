package eu.adrianistan.route.entities

data class RouteEntity (
    val _id: String? = null,
    val name: String,
    val description: String,
    val comments: List<String>,
    val points: GeoJsonLineString,
)
