package eu.adrianistan.model

data class InterestPoint(
    val type: InterestPointType,
    val name: String,
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
