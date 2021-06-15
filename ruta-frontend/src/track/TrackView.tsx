import { CRS, Icon } from "leaflet";
import React, { useEffect, useState } from "react";
import { MapContainer, Marker, Polyline, WMSTileLayer } from "react-leaflet";
import { useParams } from "react-router-dom";
import Track from "../models/Track";
import { ApiService } from "../services/ApiService";
import { Color } from "./Color";
import play_icon from "./play.svg";
import flag_icon from "./flag.svg";
import TrackLine from "../models/TrackLine";

type Props = {
    apiService: ApiService
}

function TrackView({apiService}: Props){
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

    return (
        <div>
            <h2>Track</h2>
            {track && <MapContainer bounds={routeBounds()} scrollWheelZoom={true} style={{width: "500px", height: "500px"}}>
                <WMSTileLayer url="http://orto.wms.itacyl.es/WMS?" format="image/jpeg" crs={CRS.EPSG4326} tileSize={256} layers="Ortofoto_2017" attribution="© ITaCyL. Junta de Castilla y León"/>
                {polylines}
                {startMarker(track.segments)}
                {endMarker(track.segments)}
            </MapContainer>}
        </div>
    );
}

export default TrackView;