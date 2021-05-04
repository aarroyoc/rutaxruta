package mother

import eu.adrianistan.model.Route
import eu.adrianistan.model.RoutePoint
import eu.adrianistan.model.User
import eu.adrianistan.route.entities.GeoJsonLineString
import eu.adrianistan.route.entities.RouteEntity

object RouteMother {
    fun build(): Route =
        Route(
            id = "123456789",
            name = "Valladolid - Cigales por Canal de Castilla",
            description = "Vamos de Valladolid a Cigales siguiendo el Canal de Castilla<br>Pasamos al lado del ITaCyl",
            comments = emptyList(),
            author = User(
                id = null,
                type = "google",
                providerId = "111111111122222234444070",
                name = "Cubero",
                picture = ""
            ),
            media = emptyList(),
            points = listOf(
                RoutePoint(41.66008825124748, -4.733734130859375),
                RoutePoint(41.671116673793016, -4.725494384765625),
                RoutePoint(41.67893801438407, -4.722576141357422),
                RoutePoint(41.68406624635085, -4.7303009033203125),
                RoutePoint(41.67855338051137, -4.739913940429687),
                RoutePoint(41.67445047616417, -4.734592437744141),
                RoutePoint(41.67265537326585, -4.7371673583984375),
                RoutePoint(41.66957793753977, -4.736137390136719),
                RoutePoint(41.66162721430806, -4.742145538330078)
            )
        )

    fun buildEntity(): RouteEntity {
        val route = build()
        return RouteEntity(
            name = route.name,
            description = route.description,
            comments = emptyList(),
            points = GeoJsonLineString(
                coordinates = route.points.map { point ->
                    arrayOf(point.lon, point.lat)
                }
            )
        )
    }
}