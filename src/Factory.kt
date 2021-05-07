package eu.adrianistan

import eu.adrianistan.config.Settings
import org.litote.kmongo.reactivestreams.*
import org.litote.kmongo.coroutine.*

object Factory{
    private val mongoClient by lazy { KMongo.createClient(Settings.MONGODB_URI).coroutine }

    fun getDatabase() =
        mongoClient.getDatabase("rutaxruta")
}