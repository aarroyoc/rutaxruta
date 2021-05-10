package eu.adrianistan.poi

import com.mongodb.client.model.Filters.geoWithinCenter
import eu.adrianistan.Factory
import eu.adrianistan.model.Poi
import eu.adrianistan.model.RoutePoint
import eu.adrianistan.poi.entities.PoiEntity

class PoiRepository {
    private val collection = Factory.getDatabase().getCollection<PoiEntity>("poi")

    suspend fun listNearPois(coordinates: RoutePoint) =
        collection.findOne(geoWithinCenter("location", coordinates.lon, coordinates.lat, 50.0))

    suspend fun getPoi(id: String) =
        collection.findOneById(id)?.toModel()

    private fun PoiEntity.toModel() =
        Poi(
            type = Poi.InterestPointType.MONUMENT,
            name = this.name,
            description = this.description,
            lat = this.location.coordinates[1],
            lon = this.location.coordinates[0]
        )
}