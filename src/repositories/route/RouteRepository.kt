package eu.adrianistan.repositories.route

import eu.adrianistan.Factory
import eu.adrianistan.model.Route
import eu.adrianistan.model.RoutePoint
import eu.adrianistan.model.TrackInfo
import eu.adrianistan.route.entities.GeoJsonLineString
import eu.adrianistan.repositories.route.entities.RouteEntity
import eu.adrianistan.repositories.route.entities.TrackInfoEntity
import eu.adrianistan.repositories.route.entities.toEntity
import org.litote.kmongo.*

class RouteRepository {
    private val collection = Factory.getDatabase().getCollection<RouteEntity>("route")
    private val timeProvider = Factory.getTimeProvider()

    suspend fun listPublishedRoutes(): List<Route> =
        collection.find(RouteEntity::status eq "published").toList().map {
            it.toModel()
        }

    suspend fun getRouteById(id: String): Route? {
        return collection.findOneById(id)?.toModel()
    }

    private fun RouteEntity.toModel() =
        Route(
            id = this._id,
            userId = this.userId,
            points = this.points.coordinates.map { coordinates ->
                RoutePoint(coordinates[1], coordinates[0])
            },
            name = this.name,
            description = this.description,
            status = Route.RouteState.valueOf(this.status.uppercase()),
            tracks = this.tracks.map { it.toModel() }
        )

    suspend fun saveRoute(route: Route): String? {
        val routeEntity = RouteEntity(
            name = route.name,
            description = route.description,
            comments = emptyList(),
            points = GeoJsonLineString(
                coordinates = route.points.map { point ->
                    arrayOf(point.lon, point.lat)
                }
            ),
            userId = route.userId,
            status = route.status.value,
            tracks = emptyList(),
            createdAt = timeProvider.getLocalDateTime().toString(),
            updatedAt = timeProvider.getLocalDateTime().toString()
        )
        collection.insertOne(routeEntity)
        return routeEntity._id
    }

    suspend fun addTrack(routeId: String, trackInfo: TrackInfo) {
        collection.updateOne(RouteEntity::_id eq routeId,
            combine(
                push(RouteEntity::tracks, trackInfo.toEntity()),
                set(RouteEntity::updatedAt setTo timeProvider.getLocalDateTime().toString())
        ))
    }

    suspend fun removeTrack(trackId: String) {
        collection.updateMany(RouteEntity::tracks / TrackInfoEntity::trackId eq trackId,
            combine(
                pullByFilter(RouteEntity::tracks, TrackInfoEntity::trackId eq trackId),
                set(RouteEntity::updatedAt setTo timeProvider.getLocalDateTime().toString())
            )
        )
    }

    suspend fun deleteRoute(routeId: String): Boolean =
       collection.deleteOneById(routeId).deletedCount == 1L
}