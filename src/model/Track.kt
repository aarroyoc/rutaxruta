package eu.adrianistan.model

data class Track(
    val segments: List<TrackLine>,
    val maxSpeed: Double,
    val minSpeed: Double,
)
