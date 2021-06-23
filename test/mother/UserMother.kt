package mother

import eu.adrianistan.model.User
import eu.adrianistan.repositories.user.entities.UserEntity
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

object UserMother {
    const val SOME_USER_ID = "1234567"
    val SOME_TIMESTAMP = LocalDateTime(2021,6,21,23,53, 0).toString()

    fun build() =
        User(
            id = SOME_USER_ID,
            name = "Fulanito",
            picture = "https://fulanito.com/img.png",
            type = "google",
            providerId = "1234567890"
        )

    fun buildEntity() =
        build().let {
            UserEntity(
                _id = it.id,
                type = it.type,
                providerId = it.providerId,
                name = it.name,
                picture = it.picture,
                createdAt = SOME_TIMESTAMP,
                updatedAt = SOME_TIMESTAMP
            )
        }
}