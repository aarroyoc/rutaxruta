package eu.adrianistan.controller.dto

import eu.adrianistan.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String?,
    val name: String,
    val picture: String,
)

fun User.toDto(): UserDto =
    UserDto(
        id = this.id,
        name = this.name,
        picture = this.picture
    )