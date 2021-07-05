package eu.adrianistan.track

import eu.adrianistan.model.Track
import eu.adrianistan.model.TrackLine
import eu.adrianistan.model.TrackPoint
import eu.adrianistan.model.User
import io.jenetics.jpx.GPX
import io.jenetics.jpx.Length
import io.jenetics.jpx.geom.Geoid
import org.nield.kotlinstatistics.percentile
import java.io.InputStream
import java.time.Duration

class ProcessGpxTrack {
    operator fun invoke(gpxStream: InputStream, user: User, name: String = ""): Track {
        val track = GPX.read(gpxStream).tracks.first()
        var distance = 0.0
        val points = track.segments.flatMap { it.points }
        if(points.size < 2) {
            throw IllegalArgumentException("not enough points in GPX")
        }
        val lines = mutableListOf<TrackLine>()
        val trackPoints = mutableListOf<TrackPoint>()
        val elevationSupplier = {
            Length.of(0.0, Length.Unit.METER)
        }
        trackPoints.add(
            TrackPoint(
                distance = 0.0,
                elevation = points[0].elevation.orElseGet(elevationSupplier).to(Length.Unit.METER)
            )
        )

        for (i in 0..points.size-2) {
            val j = i + 1
            val pointA = points[i]
            val pointB = points[j]

            val length = Geoid.WGS84.distance(pointA, pointB)
            distance += length.to(Length.Unit.METER)

            trackPoints.add(
                TrackPoint(
                    distance = distance,
                    elevation = pointB.elevation.orElseGet(elevationSupplier).to(Length.Unit.METER)
                )
            )

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


        val duration = if(points.first().time.isPresent && points.last().time.isPresent) {
            Duration.between(points.first().time.get(), points.last().time.get()).toSeconds()
        } else {
            null
        }

        val meanSpeed = duration?.let { (distance*3600) / (duration * 1000)}

        return Track(
            name = name,
            user = user,
            segments = lines,
            maxSpeed = lines.map { it.speed }.percentile(99.0),
            minSpeed = lines.map { it.speed }.minOrNull() ?: 0.0,
            duration = duration,
            distance = distance.toLong(),
            meanSpeed = meanSpeed,
            points = trackPoints
        )
    }
}