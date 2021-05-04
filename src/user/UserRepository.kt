package eu.adrianistan.user

import eu.adrianistan.Factory
import eu.adrianistan.model.User
import eu.adrianistan.user.entities.UserEntity
import org.litote.kmongo.eq
import org.litote.kmongo.upsert

class UserRepository {
    private val collection = Factory.getDatabase().getCollection<UserEntity>("user")

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
            picture = this.picture
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