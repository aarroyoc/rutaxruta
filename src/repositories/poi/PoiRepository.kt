package eu.adrianistan.repositories.poi

import com.mongodb.client.model.Filters.*
import eu.adrianistan.Factory
import eu.adrianistan.model.Poi
import eu.adrianistan.repositories.poi.entities.PoiEntity
import org.locationtech.jts.geom.Geometry

class PoiRepository {
    private val collection = Factory.getDatabase().getCollection<PoiEntity>("poi")

    suspend fun listNearPois(geometry: Geometry): List<Poi> =
        geometry.coordinates.map {
            listOf(it.x, it.y)
        }.let {
            collection.find(geoWithinPolygon("location", it)).toList().map { poiEntity ->
                poiEntity.toModel()
            }
        }

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