package eu.adrianistan.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import eu.adrianistan.model.User

object JwtConfig {
    private val algorithm = Algorithm.HMAC256("secreto")

    val verifier = JWT.require(algorithm)
        .withAudience("rutaxruta.com")
        .withIssuer("rutaxruta.com")
        .build()

    fun makeToken(user: User): String = JWT.create()
        .withSubject("Authentication")
        .withAudience("rutaxruta.com")
        .withIssuer("rutaxruta.com")
        .withClaim("user_id", user.id)
        .sign(algorithm)
}