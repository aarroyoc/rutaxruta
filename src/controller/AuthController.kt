package eu.adrianistan.controller

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import eu.adrianistan.config.JwtConfig
import eu.adrianistan.config.Settings
import eu.adrianistan.model.User
import eu.adrianistan.repositories.user.UserRepository
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRouting() {
    val userRepository = UserRepository()

    post("/token") {
        val token = call.receiveText()
        val verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
            .setAudience(listOf(Settings.GOOGLE_CLIENT_ID))
            .build()
        val idToken = verifier.verify(token)
        val payload = idToken.payload
        val id = payload.subject

        if(userRepository.getUser("google", id) == null) {
            userRepository.saveUser(
                User(
                    id = null,
                    name = payload["name"] as String,
                    picture = payload["picture"] as String,
                    type = "google",
                    providerId = id
                )
            )
        }
        val jwtToken = userRepository.getUser("google", id)?.let {
            JwtConfig.makeToken(it)
        } ?: error("Can't make token")
        call.respondText(jwtToken)
    }
}