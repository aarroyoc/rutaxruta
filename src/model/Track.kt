package eu.adrianistan.model

import java.util.*

data class Track(
    val user: User,
    val date: Date,
    val points: List<TrackPoint>
)
