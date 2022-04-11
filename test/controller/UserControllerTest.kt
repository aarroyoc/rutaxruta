package controller

import eu.adrianistan.Factory
import eu.adrianistan.config.JwtConfig
import eu.adrianistan.module
import eu.adrianistan.repositories.user.entities.UserEntity
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import mother.UserMother
import kotlin.test.*

class UserControllerTest {

    @BeforeTest
    fun cleanDatabase() {
        runBlocking {
            Factory.getDatabase().dropCollection("user")
        }
    }

    @Test
    fun `get me when authenticated`() = testApplication {
        val token = generateToken()
        val response = client.get("/user/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("""{"id":"1234567","name":"Fulanito","picture":"https://fulanito.com/img.png"}""", response.bodyAsText())
    }

    @Test
    fun `get me when not authenticated`() = testApplication {
        val response = client.get("/user/me")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `get another user, not authenticated`() = testApplication {
        generateToken()
        val userId = "1234567"
        val response = client.get("/user/$userId")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("""{"id":"1234567","name":"Fulanito","picture":"https://fulanito.com/img.png"}""", response.bodyAsText())
    }

    private suspend fun generateToken(): String {
        val collection = Factory.getDatabase().getCollection<UserEntity>("user")
        collection.insertOne(UserMother.buildEntity())
        return JwtConfig.makeToken(UserMother.build())
    }
}