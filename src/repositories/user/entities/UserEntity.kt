package eu.adrianistan.repositories.user.entities

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    val _id: String?,
    val type: String,
    val providerId: String,
    val name: String,
    val picture: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
