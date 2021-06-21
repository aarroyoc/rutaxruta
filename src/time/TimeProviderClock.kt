package eu.adrianistan.time

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class TimeProviderClock : TimeProvider {
    override fun getLocalDateTime(): LocalDateTime =
        Clock.System.now().toLocalDateTime(TimeZone.UTC)
}