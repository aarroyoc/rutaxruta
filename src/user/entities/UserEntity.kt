package eu.adrianistan.user.entities

data class UserEntity(
    val _id: String?,
    val type: String,
    val providerId: String,
    val name: String,
    val picture: String,
)
