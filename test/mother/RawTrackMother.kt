package mother

import eu.adrianistan.repositories.track.entities.RawTrackEntity
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import java.io.File

object RawTrackMother {
    const val SOME_TRACK_ID = "123456"
    const val SOME_OTHER_TRACK_ID = "4567"
    val SOME_TIMESTAMP = LocalDateTime(2021,6,21,23,53, 0)

    fun buildEntity(): RawTrackEntity =
        RawTrackEntity(
            _id = SOME_TRACK_ID,
            name = "Castrodeza Ida",
            gpx = File("test/track/castrodeza.gpx").readText(),
            timestamp = 100000,
            userId = UserMother.buildEntity()._id ?: error(""),
            createdAt = SOME_TIMESTAMP,
            updatedAt = SOME_TIMESTAMP
        )
}