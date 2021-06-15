package eu.adrianistan.repositories.track

import eu.adrianistan.Factory
import eu.adrianistan.model.RawTrack
import eu.adrianistan.model.TrackInfo
import eu.adrianistan.repositories.track.entities.RawTrackEntity
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Instant
import org.litote.kmongo.and
import org.litote.kmongo.eq

class RawTrackRepository {
    private val collection = Factory.getDatabase().getCollection<RawTrackEntity>("track")

    suspend fun getTrackById(id: String): RawTrack? =
        collection.findOneById(id)?.toModel()

    suspend fun getTracksInfoByUserId(userId: String): List<TrackInfo> =
        collection.find(RawTrackEntity::userId eq userId).toFlow().map {
            TrackInfo(
                trackId = it._id ?: error("id should be defined by Mongo"),
                name = it.name
            )
        }.toList()

    suspend fun saveRawTrack(rawTrack: RawTrack) =
        collection.save(rawTrack.toEntity())

    suspend fun deleteRawTrack(id: String, userId: String): Boolean =
        collection.deleteOne(and(RawTrackEntity::_id eq id, RawTrackEntity::userId eq userId)).deletedCount == 1L

    private fun RawTrackEntity.toModel(): RawTrack =
        RawTrack(
            id = this._id,
            name = this.name,
            gpx = this.gpx,
            timestamp = Instant.fromEpochSeconds(this.timestamp),
            userId = this.userId
        )

    private fun RawTrack.toEntity(): RawTrackEntity =
        RawTrackEntity(
            _id = this.id,
            name = this.name,
            gpx = this.gpx,
            timestamp = this.timestamp.epochSeconds,
            userId = this.userId
        )
}