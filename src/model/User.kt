package eu.adrianistan.model

data class User(
    val id: String?,
    val type: String,
    val providerId: String,
    val name: String,
    val picture: String,
) : io.ktor.server.auth.Principal
