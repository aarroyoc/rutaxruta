package eu.adrianistan.time

import kotlinx.datetime.LocalDateTime

interface TimeProvider {
    fun getLocalDateTime(): LocalDateTime
}