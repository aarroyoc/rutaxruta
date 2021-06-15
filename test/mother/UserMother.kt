package mother

import eu.adrianistan.model.User
import eu.adrianistan.repositories.user.entities.UserEntity

object UserMother {
    const val SOME_USER_ID = "1234567"

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
                picture = it.picture
            )
        }
}