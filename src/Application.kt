package eu.adrianistan

import eu.adrianistan.config.JwtConfig
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import eu.adrianistan.controller.authRouting
import eu.adrianistan.controller.routeRouting
import eu.adrianistan.controller.userRouting
import eu.adrianistan.repositories.user.UserRepository
import io.ktor.auth.jwt.*
import io.ktor.client.features.json.*
import io.ktor.features.*
import io.ktor.locations.*
import io.ktor.serialization.*
import io.ktor.client.features.json.serializer.KotlinxSerializer


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val httpClient = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

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
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    routing {
        routeRouting()
        authRouting(httpClient)
        userRouting()
    }
}
