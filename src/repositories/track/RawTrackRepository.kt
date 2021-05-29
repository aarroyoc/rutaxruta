package eu.adrianistan.repositories.track

import eu.adrianistan.Factory
import eu.adrianistan.model.RawTrack
import eu.adrianistan.repositories.track.entities.RawTrackEntity

class RawTrackRepository {
    private val collection = Factory.getDatabase().getCollection<RawTrackEntity>("track")

    suspend fun getTrackById(id: String): RawTrack? =
        collection.findOneById(id)?.toModel()

    suspend fun saveRawTrack(rawTrack: RawTrack) =
        collection.save(rawTrack.toEntity())

    private fun RawTrackEntity.toModel(): RawTrack =
        RawTrack(
            id = this._id,
            name = this.name,
            gpx = this.gpx,
            timestamp = this.timestamp
        )

    private fun RawTrack.toEntity(): RawTrackEntity =
        RawTrackEntity(
            _id = this.id,
            name = this.name,
            gpx = this.gpx,
            timestamp = this.timestamp
        )
}