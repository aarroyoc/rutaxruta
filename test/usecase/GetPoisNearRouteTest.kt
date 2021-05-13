package usecase

import eu.adrianistan.repositories.poi.PoiRepository
import eu.adrianistan.route.RouteRepository
import eu.adrianistan.usecase.GetPoisNearRoute
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import mother.PoiMother
import mother.RouteMother
import mother.RouteMother.SOME_ROUTE_ID
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPoisNearRouteTest {
    private val routeRepository: RouteRepository = mockk()
    private val poiRepository: PoiRepository = mockk()

    val getPoisNearRoute = GetPoisNearRoute(routeRepository, poiRepository)

    @Test
    fun `should return a list of pois near route`() {
        coEvery { routeRepository.getRouteById(SOME_ROUTE_ID) } returns RouteMother.build()
        coEvery { poiRepository.listNearPois(any()) } returns listOf(PoiMother.build())

        val pois = runBlocking {
            getPoisNearRoute(SOME_ROUTE_ID)
        }
        assertEquals(1, pois?.size)
    }

    @Test
    fun `should return null if the route doesn't exist`() {
        coEvery { routeRepository.getRouteById(SOME_ROUTE_ID) } returns null

        val pois = runBlocking {
            getPoisNearRoute(SOME_ROUTE_ID)
        }
        assertEquals(null, pois)
    }
}