package controller

import eu.adrianistan.Factory
import eu.adrianistan.config.JwtConfig
import eu.adrianistan.module
import eu.adrianistan.repositories.user.entities.UserEntity
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
    fun `get me when authenticated`() {
        val token = runBlocking {
            generateToken()
        }
        withTestApplication({module(testing = true)}) {
            handleRequest(HttpMethod.Get, "/user/me") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }.apply {
                assertEquals(HttpStatusCode.OK, this.response.status())
                assertEquals("""{"id":"1234567","name":"Fulanito","picture":"https://fulanito.com/img.png"}""", this.response.content)
            }
        }
    }

    @Test
    fun `get me when not authenticated`() {
        withTestApplication({module(testing = true)}) {
            handleRequest(HttpMethod.Get, "/user/me").apply {
                assertEquals(HttpStatusCode.Unauthorized, this.response.status())
            }
        }
    }

    @Test
    fun `get another user, not authenticated`() {
        runBlocking {
            generateToken()
        }
        val userId = "1234567"
        withTestApplication({module(testing = true)}) {
            handleRequest(HttpMethod.Get, "/user/$userId").apply {
                assertEquals(HttpStatusCode.OK, this.response.status())
                assertEquals("""{"id":"1234567","name":"Fulanito","picture":"https://fulanito.com/img.png"}""", this.response.content)
            }
        }
    }

    private suspend fun generateToken(): String {
        val collection = Factory.getDatabase().getCollection<UserEntity>("user")
        collection.insertOne(UserMother.buildEntity())
        return JwtConfig.makeToken(UserMother.build())
    }
}