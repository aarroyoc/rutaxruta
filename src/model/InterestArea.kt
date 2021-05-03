package eu.adrianistan.model

data class InterestArea(
    val type: InterestAreaType,
) {
    enum class InterestAreaType(val value: String) {
        NATURAL_PARK("natural_park"),
    }
}
