package eu.adrianistan.controller

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import eu.adrianistan.config.JwtConfig
import eu.adrianistan.config.Settings
import eu.adrianistan.controller.dto.google.UserInfo
import eu.adrianistan.model.User
import eu.adrianistan.user.UserRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.authRouting(httpClient: HttpClient) {
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