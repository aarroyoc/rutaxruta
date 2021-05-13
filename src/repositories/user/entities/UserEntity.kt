package eu.adrianistan.repositories.user.entities

import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    val _id: String?,
    val type: String,
    val providerId: String,
    val name: String,
    val picture: String,
)
