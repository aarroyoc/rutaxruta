package eu.adrianistan.route

import eu.adrianistan.Factory
import eu.adrianistan.model.Route
import eu.adrianistan.model.RoutePoint
import eu.adrianistan.model.User
import eu.adrianistan.route.entities.GeoJsonLineString
import eu.adrianistan.repositories.route.entities.RouteEntity

class RouteRepository (){
    private val collection = Factory.getDatabase().getCollection<RouteEntity>("route")

    suspend fun listRoutes(): List<Route> =
        collection.find().toList().map {
            it.toModel()
        }

    suspend fun getRouteById(id: String): Route? {
        return collection.findOneById(id)?.toModel()
    }

    private fun RouteEntity.toModel() =
        Route(
            id = this._id,
            author = User(
                id = null,
                type = "google",
                providerId = "TODO",
                name = "TODO",
                picture = "TODO"
            ),
            points = this.points.coordinates.map { coordinates ->
                RoutePoint(coordinates[1], coordinates[0])
            },
            media = emptyList(),
            name = this.name,
            description = this.description,
            comments = emptyList()
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
            )
        )
        collection.insertOne(routeEntity)
        return routeEntity._id
    }

    suspend fun deleteRoute(routeId: String): Boolean =
       collection.deleteOneById(routeId).deletedCount == 1L
}