package eu.adrianistan.repositories.route

import eu.adrianistan.Factory
import eu.adrianistan.model.Route
import eu.adrianistan.model.RoutePoint
import eu.adrianistan.route.entities.GeoJsonLineString
import eu.adrianistan.repositories.route.entities.RouteEntity
import org.litote.kmongo.eq

class RouteRepository {
    private val collection = Factory.getDatabase().getCollection<RouteEntity>("route")

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
            media = emptyList(),
            name = this.name,
            description = this.description,
            comments = emptyList(),
            status = Route.RouteState.valueOf(this.status.uppercase())
        )

    suspend fun saveRoute(route: Route): String? {
        var routeEntity = RouteEntity(
            name = route.name,
            description = route.description,
            comments = emptyList(),
            points = GeoJsonLineString(
                coordinates = route.points.map { point ->
                    arrayOf(point.lon, point.lat)
                }
            ),
            userId = route.userId,
            status = route.status.value
        )
        collection.insertOne(routeEntity)
        return routeEntity._id
    }

    suspend fun deleteRoute(routeId: String): Boolean =
       collection.deleteOneById(routeId).deletedCount == 1L
}