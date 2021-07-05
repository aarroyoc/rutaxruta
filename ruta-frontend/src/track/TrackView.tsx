import { Icon } from "leaflet";
import React, { useEffect, useState } from "react";
import { Marker, Polyline } from "react-leaflet";
import { useHistory, useParams } from "react-router-dom";
import Track from "../models/Track";
import { ApiService } from "../services/ApiService";
import { Color } from "./Color";
import play_icon from "./play.svg";
import flag_icon from "./flag.svg";
import TrackLine from "../models/TrackLine";
import MapBase from "../map/MapBase";
import { Link } from "@fluentui/react";
import "./TrackView.css";
import { VegaLite } from "react-vega";
import { Handler } from "vega-tooltip";

type Props = {
    apiService: ApiService
}

function TrackView({apiService}: Props){
    const history = useHistory();
    const params = useParams();
    const { id }: any = params;
    const [track, setTrack] = useState<Track|null>(null);
    const color = new Color();

    useEffect(()=> {
        apiService
        .getTrack(id)
            .then(newTrack =>{
                setTrack(newTrack);
            })
        // eslint-disable-next-line
    }, [id]);

    
    const polylines = track?.segments.map(segment => {
        const maxSpeed = track.maxSpeed;
        const lineColor = color.getColor(segment.speed/maxSpeed);
        return <Polyline positions={[[segment.latA, segment.lonA], [segment.latB, segment.lonB]]} color={lineColor} weight={3}/>
    });

    const startMarker = (segments: TrackLine[]) => {
        const playIcon = new Icon({
            iconUrl: play_icon,
            iconAnchor: [10, 20],
            iconSize: [27, 27]
        });

        return <Marker icon={playIcon} position={[segments[0].latA, segments[0].lonA]}/>;
    };

    const endMarker = (segments: TrackLine[]) => {
        const flagIcon = new Icon({
            iconUrl: flag_icon,
            iconAnchor: [10, 20],
            iconSize: [27, 27]
        });
        const lastPos = segments.length - 1;

        return <Marker icon={flagIcon} position={[segments[lastPos].latA, segments[lastPos].lonA]}/>;
    }
    

    const routeBounds = (): any => {
        if(track){
            const minLat = track.segments.map(t => t.latA).reduce((prev, current) => Math.min(prev, current));
            const minLon = track.segments.map(t => t.lonA).reduce((prev, current) => Math.min(prev, current));
            const maxLat = track.segments.map(t => t.latA).reduce((prev, current) => Math.max(prev, current));
            const maxLon = track.segments.map(t => t.lonA).reduce((prev, current) => Math.max(prev, current));
            return [[minLat, minLon], [maxLat, maxLon]];
        }
    }

    const formatDuration = (duration: number) => {
        const seconds = duration % 60;
        let minutes = Math.floor(duration / 60);
        const hours = Math.floor(minutes / 60);
        minutes = minutes % 60;

        return `${hours}:${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;
    }

    const formatDistance = (distance: number) => {
        const km = Math.floor(distance / 1000);
        const meter = distance % 1000;
        return `${km},${String(meter).padStart(3, "0")} km`;
    }

    const spec = {
        width: 400,
        height: 200,
        mark: {
            type: "line",
            tooltip: true
        },
        encoding: {
            x: { field: "distance", title: "Distancia", type: "quantitative"},
            y: { field: "elevation", title: "Elevación", type: "quantitative", scale: {zero: false}}
        },
        data: {name: "table"}
    }

    return (
        <div>
            {track && <h2>Track "{track.name}" por <Link onClick={() => history.push(`/user/${track.userId}`)}>{track.userName}</Link></h2> }
            {track && <MapBase bounds={routeBounds()}>
                {polylines}
                {startMarker(track.segments)}
                {endMarker(track.segments)}
            </MapBase>}
            {track && <div className="trackDataWrapper">
            <div className="trackData">
                <label>Duración</label>
                <span>{formatDuration(track.duration)}</span>
                <label>Distancia</label>
                <span>{formatDistance(track.distance)}</span>
                <label>Velocidad media</label>
                <span>{track.meanSpeed} km/h</span>
                <label>Velocidad máxima</label>
                <span>{track.maxSpeed*3.6} km/h</span>
            </div>
            <VegaLite spec={spec as any} data={{table: track.points}} tooltip={new Handler().call}/>
            </div>}
        </div>
    );
}

export default TrackView;