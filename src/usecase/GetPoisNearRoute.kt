package eu.adrianistan.usecase

import eu.adrianistan.model.Poi
import eu.adrianistan.repositories.poi.PoiRepository
import eu.adrianistan.route.RouteRepository
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.operation.buffer.BufferOp

class GetPoisNearRoute(
    private val routeRepository: RouteRepository,
    private val poiRepository: PoiRepository,
){
    suspend operator fun invoke(routeId: String): List<Poi>? =
        routeRepository
            .getRouteById(routeId)
            ?.let { route ->
                val coordinates = route.points.map {
                    Coordinate(it.lon, it.lat)
                }.toTypedArray()
                val geometry = GeometryFactory()
                    .createLineString(coordinates)
                val polygon = BufferOp.bufferOp(geometry, 0.1)
                poiRepository.listNearPois(polygon)
            }
}