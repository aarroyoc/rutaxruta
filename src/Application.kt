package eu.adrianistan

import eu.adrianistan.config.JwtConfig
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.auth.*
import eu.adrianistan.controller.authRouting
import eu.adrianistan.controller.routeRouting
import eu.adrianistan.controller.trackRouting
import eu.adrianistan.controller.userRouting
import eu.adrianistan.repositories.user.UserRepository
import io.ktor.server.auth.jwt.*
import io.ktor.server.locations.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.*
import io.ktor.serialization.kotlinx.json.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@JvmOverloads
fun Application.module() {
    install(Locations)

    val userRepository = UserRepository()
    install(Authentication) {
        jwt {
            verifier(JwtConfig.verifier)
            realm = "rutaxruta.com"
            validate {
                val userId = it.payload.claims["user_id"]?.asString()
                userId?.let {
                    userRepository.getUser(userId)
                }
            }
        }
    }

    install(ContentNegotiation) {
        json()
    }

    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowCredentials = false
        anyHost()
    }

    routing {
        authRouting()
        routeRouting()
        trackRouting()
        userRouting()
    }
}
