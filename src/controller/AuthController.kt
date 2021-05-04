package eu.adrianistan.controller

import eu.adrianistan.Login
import eu.adrianistan.config.JwtConfig
import eu.adrianistan.controller.dto.google.UserInfo
import eu.adrianistan.model.User
import eu.adrianistan.user.UserRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.authRouting(httpClient: HttpClient) {
    val userRepository = UserRepository()

    authenticate("google") {
        location<Login>() {
            param("error") {
                handle {
                    call.respond(HttpStatusCode.BadRequest, call.parameters.getAll("error").orEmpty())
                }
            }

            handle {
                val principal = call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()
                if (principal != null) {
                    val userInfo: UserInfo = httpClient.get("https://www.googleapis.com/oauth2/v2/userinfo") {
                        headers {
                            append(HttpHeaders.Authorization, "Bearer ${principal.accessToken}")
                        }
                    }
                    if(userRepository.getUser("google", userInfo.id) == null) {
                        userRepository.saveUser(
                            User(
                                id = null,
                                name = userInfo.name,
                                picture = userInfo.picture,
                                type = "google",
                                providerId = userInfo.id
                            )
                        )
                    }
                    val jwtToken = userRepository.getUser("google", userInfo.id)?.let {
                        JwtConfig.makeToken(it)
                    }
                    call.response.cookies.append("jwt-token", jwtToken!!, httpOnly = false, path = null)
                    call.respondRedirect("/")

                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }
    }
}