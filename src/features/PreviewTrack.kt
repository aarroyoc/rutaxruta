package eu.adrianistan.features

import eu.adrianistan.model.User
import eu.adrianistan.track.ProcessGpxTrack

class PreviewTrack {
    private val processGpxTrack = ProcessGpxTrack()

    private val fakeUser = User(
        id = "",
        type = "",
        name = "",
        providerId = "",
        picture = ""
    )

    operator fun invoke(gpxText: String) =
        processGpxTrack(gpxText.byteInputStream(), fakeUser)
}