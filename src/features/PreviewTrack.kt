package eu.adrianistan.features

import eu.adrianistan.track.ProcessGpxTrack

class PreviewTrack {
    private val processGpxTrack = ProcessGpxTrack()

    operator fun invoke(gpxText: String) =
        processGpxTrack(gpxText.byteInputStream())
}