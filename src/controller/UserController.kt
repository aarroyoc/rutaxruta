package eu.adrianistan.controller

import eu.adrianistan.controller.dto.toDto
import eu.adrianistan.model.User
import eu.adrianistan.repositories.user.UserRepository
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Routing.userRouting() {
    val userRepository = UserRepository()

    get("/user/{id}") {
        val userId = call.parameters.getOrFail("id")
        val user = userRepository.getUser(userId)
        if(user != null){
            call.respond(user.toDto())
        } else {
            throw NotFoundException()
        }
    }

    authenticate {
        get("/user/me") {
            val user = call.authentication.principal<User>()
            user?.let {
                call.respond(user.toDto())
            } ?: call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
    }

}