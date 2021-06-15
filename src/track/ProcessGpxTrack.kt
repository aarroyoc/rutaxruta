package eu.adrianistan.track

import eu.adrianistan.model.Track
import eu.adrianistan.model.TrackLine
import io.jenetics.jpx.GPX
import io.jenetics.jpx.Length
import io.jenetics.jpx.geom.Geoid
import org.nield.kotlinstatistics.percentile
import java.io.InputStream
import java.time.Duration

class ProcessGpxTrack {
    operator fun invoke(gpxStream: InputStream, name: String = ""): Track {
        val track = GPX.read(gpxStream).tracks.first()
        val points = track.segments.flatMap { it.points }
        if(points.size < 2) {
            throw IllegalArgumentException("not enough points in GPX")
        }
        val lines = mutableListOf<TrackLine>()

        for (i in 0..points.size-2) {
            val j = i + 1
            val pointA = points[i]
            val pointB = points[j]

            val length = Geoid.WGS84.distance(pointA, pointB)
            val speed = if(pointA.time.isPresent && pointB.time.isPresent){
                val timeInterval = Duration.between(pointA.time.get(), pointB.time.get())
                length.to(Length.Unit.METER) / timeInterval.seconds
            } else {
                0.0
            }

            lines.add(TrackLine(
                latA = pointA.latitude.toDegrees(),
                latB = pointB.latitude.toDegrees(),
                lonA = pointA.longitude.toDegrees(),
                lonB = pointB.longitude.toDegrees(),
                speed = speed
            ))
        }

        return Track(
            name = name,
            segments = lines,
            maxSpeed = lines.map { it.speed }.percentile(99.0) ?: 0.0,
            minSpeed = lines.map { it.speed }.minOrNull() ?: 0.0
        )
    }
}