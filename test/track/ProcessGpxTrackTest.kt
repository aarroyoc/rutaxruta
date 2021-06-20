package track

import eu.adrianistan.model.User
import eu.adrianistan.track.ProcessGpxTrack
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class ProcessGpxTrackTest {

    val processGpxTrack = ProcessGpxTrack()
    val fakeUser = User(
        id = "",
        type = "google",
        providerId = "",
        name = "",
        picture = "",
    )

    @Test
    fun `should process a correct and complete GPX track`() {
        val gpx = File("test/track/castrodeza.gpx").inputStream()
        val gpxInfo = processGpxTrack(gpx, fakeUser)
        assertEquals(5107, gpxInfo.segments.size)
        assertEquals(0.0, gpxInfo.minSpeed)
        assert(gpxInfo.maxSpeed > 8.0)
    }

    @Test
    fun `should process a no time GPX track`() {
        val gpx = File("test/track/no-time.gpx").inputStream()
        val gpxInfo = processGpxTrack(gpx, fakeUser, "No time GPX")
        assertEquals(2, gpxInfo.segments.size)
        assertEquals(41.6666000, gpxInfo.segments[0].latA)
        assertEquals(41.6666555, gpxInfo.segments[0].latB)
        assertEquals(-4.7211110, gpxInfo.segments[0].lonA)
        assertEquals(-4.7211155, gpxInfo.segments[0].lonB)
        assertEquals(0.0, gpxInfo.segments[0].speed)
        assertEquals(0.0, gpxInfo.minSpeed)
        assertEquals(0.0, gpxInfo.maxSpeed)
        assertEquals("No time GPX", gpxInfo.name)
    }

    @Test
    fun `should return error if GPX is invalid`() {
        assertFails {
            processGpxTrack("random string".byteInputStream(), fakeUser)
        }
    }

    @Test
    fun `should return error if GPX has 0 tracks`() {
        val gpx = File("test/track/no-tracks.gpx").inputStream()
        assertFails {
            processGpxTrack(gpx, fakeUser)
        }
    }

    @Test
    fun `should return error if GPX has 0 segments in the first track`() {
        val gpx = File("test/track/no-segments.gpx").inputStream()
        assertFails {
            processGpxTrack(gpx, fakeUser)
        }
    }

    @Test
    fun `should join a multisegment track`() {
        val gpx = File("test/track/multisegment.gpx").inputStream()
        val gpxInfo = processGpxTrack(gpx, fakeUser)
        assertEquals(2, gpxInfo.segments.size)
        assertEquals(41.6666000, gpxInfo.segments[0].latA)
        assertEquals(41.6666555, gpxInfo.segments[0].latB)
        assertEquals(-4.7211110, gpxInfo.segments[0].lonA)
        assertEquals(-4.7211155, gpxInfo.segments[0].lonB)
        assertEquals(0.0, gpxInfo.segments[0].speed)
        assertEquals(0.0, gpxInfo.minSpeed)
        assertEquals(0.0, gpxInfo.maxSpeed)
    }
}