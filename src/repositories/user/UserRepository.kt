package eu.adrianistan.repositories.user

import eu.adrianistan.Factory
import eu.adrianistan.model.User
import eu.adrianistan.repositories.user.entities.UserEntity
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.litote.kmongo.eq

class UserRepository {
    private val collection = Factory.getDatabase().getCollection<UserEntity>("user")
    private val timeProvider = Factory.getTimeProvider()

    suspend fun getUser(id: String) =
        collection.findOneById(id)?.toModel()

    suspend fun getUser(type: String, providerId: String) =
        collection
            .findOne(UserEntity::type eq type, UserEntity::providerId eq providerId)
            ?.toModel()

    suspend fun saveUser(user: User) {
        val userEntity = user.toEntity()
        collection.insertOne(userEntity)
    }

    private fun User.toEntity(): UserEntity =
        UserEntity(
            _id = this.id,
            type = this.type,
            providerId = this.providerId,
            name = this.name,
            picture = this.picture,
            createdAt = timeProvider.getLocalDateTime().toString(),
            updatedAt = timeProvider.getLocalDateTime().toString()
        )

    private fun UserEntity.toModel(): User =
        User(
            id = this._id,
            type = this.type,
            providerId = this.providerId,
            name = this.name,
            picture = this.picture
        )
}