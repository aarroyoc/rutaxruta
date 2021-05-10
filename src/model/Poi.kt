package eu.adrianistan.model

data class Poi(
    val type: InterestPointType,
    val name: String,
    val description: String,
    val lat: Double,
    val lon: Double,
) {
    enum class InterestPointType(val value: String) {
        BAR("bar"),
        RESTAURANT("restaurant"),
        MONUMENT("monument"),
        EVENT("event")
    }
}
