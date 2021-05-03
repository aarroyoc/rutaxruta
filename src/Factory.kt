package eu.adrianistan

import org.litote.kmongo.reactivestreams.*
import org.litote.kmongo.coroutine.*

object Factory{
    private val mongoClient by lazy { KMongo.createClient().coroutine }

    fun getDatabase() =
        mongoClient.getDatabase("rutaxruta")
}