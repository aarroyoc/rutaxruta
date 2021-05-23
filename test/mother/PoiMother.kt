package mother

import eu.adrianistan.model.Poi
import eu.adrianistan.poi.entities.GeoJsonPoint
import eu.adrianistan.repositories.poi.entities.PoiEntity

object PoiMother {
    fun build() = Poi(
        type = "monument",
        name = "Monumento 1",
        description = "El monumento 1",
        lat = 41.66008825124748,
        lon = -4.733734130859375
    )

    fun buildEntity() = build().let {
        PoiEntity(
            _id = null,
            name = it.name,
            type = it.type,
            description = it.description,
            location = GeoJsonPoint(
                coordinates = listOf(it.lon, it.lat)
            )
        )
    }
}