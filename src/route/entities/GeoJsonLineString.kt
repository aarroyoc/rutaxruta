package eu.adrianistan.route.entities

data class GeoJsonLineString(
    val type: String = "LineString",
    val coordinates: List<Array<Double>>
)
