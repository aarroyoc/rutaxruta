package eu.adrianistan

import eu.adrianistan.config.Settings
import eu.adrianistan.time.TimeProvider
import eu.adrianistan.time.TimeProviderClock
import org.litote.kmongo.reactivestreams.*
import org.litote.kmongo.coroutine.*

object Factory{
    private val mongoClient by lazy { KMongo.createClient(Settings.MONGODB_URI).coroutine }
    private val timeProvider by lazy { TimeProviderClock() }

    fun getDatabase() =
        mongoClient.getDatabase("rutaxruta")

    fun getTimeProvider(): TimeProvider =
        timeProvider
}