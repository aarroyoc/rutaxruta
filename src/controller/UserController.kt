package eu.adrianistan.controller

import eu.adrianistan.controller.dto.toDto
import eu.adrianistan.model.User
import eu.adrianistan.repositories.user.UserRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Routing.userRouting() {
    val userRepository = UserRepository()

    authenticate {
        get("/user/me") {
            val user = call.authentication.principal<User>()
            user?.let {
                call.respond(user.toDto())
            } ?: call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
    }

}