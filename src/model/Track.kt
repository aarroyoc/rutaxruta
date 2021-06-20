package eu.adrianistan.model

data class Track(
    val name: String,
    val user: User,
    val segments: List<TrackLine>,
    val maxSpeed: Double,
    val minSpeed: Double,
)
