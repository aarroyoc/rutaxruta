package eu.adrianistan.model

import java.util.*

data class Comment(
    val date: Date,
    val content: String,
    val author: User,
)
