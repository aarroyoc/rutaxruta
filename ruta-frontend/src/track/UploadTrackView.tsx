import { ITag, Label, PrimaryButton, TagPicker, TextField } from "@fluentui/react";
import { Polyline } from "react-leaflet";
import { useEffect, useState } from "react";
import Track from "../models/Track";
import User from "../models/User";
import { ApiService } from "../services/ApiService";
import { Color } from "./Color";
import "./UploadTrackView.css";
import { useHistory } from "react-router-dom";
import MapBase from "../map/MapBase";

type Props = {
    apiService: ApiService,
    user: User | null,
}

function UploadTrackView({apiService, user}: Props){
    const [routes, setRoutes] = useState<ITag[]>([]);
    const [name, setName] = useState<string>("");
    const [gpx, setGpx] = useState<string | null>(null);
    const [track, setTrack] = useState<Track|null>(null);
    const [routeId, setRouteId] = useState<string|undefined>(undefined);
    const history = useHistory();
    const color = new Color();

    const getTextFromItem = (route: ITag) => route.name;
    

    useEffect(() => {
        apiService
            .listRoutes()
            .then(routes => {
                const tags = routes.map(route => {
                    return {
                    name: route.name,
                    key: route.id
                }})
                setRoutes(tags)
            })
            // eslint-disable-next-line
    }, []);

    useEffect(() => {
        if(gpx !== null){
            const request = {
                name: "preview",
                gpx: gpx
            };
            apiService.previewTrack(request).then(t => setTrack(t));
        }
    })

    const filterSuggestions = (filterText: string, tagList?: ITag[]): ITag[] => {
        return filterText ? routes.filter(tag => tag.name.toLowerCase().indexOf(filterText.toLowerCase()) > -1) : [];
    }

    const setFile = (evt: any) => {
        const reader = new FileReader();
        reader.onload = () => {
            if(typeof reader.result === "string"){
                setGpx(reader.result)
            }
        }
        reader.readAsText(evt.target.files[0]);
    };

    const enabledButton = () => name.length > 0 && gpx !== null;

    const polylines = track?.segments.map(segment => {
        const maxSpeed = track.maxSpeed;
        const lineColor = color.getColor(segment.speed/maxSpeed);
        return <Polyline positions={[[segment.latA, segment.lonA], [segment.latB, segment.lonB]]} color={lineColor} weight={3}/>
    });

    const routeBounds = (): any => {
        if(track){
            const minLat = track.segments.map(t => t.latA).reduce((prev, current) => Math.min(prev, current));
            const minLon = track.segments.map(t => t.lonA).reduce((prev, current) => Math.min(prev, current));
            const maxLat = track.segments.map(t => t.latA).reduce((prev, current) => Math.max(prev, current));
            const maxLon = track.segments.map(t => t.lonA).reduce((prev, current) => Math.max(prev, current));
            return [[minLat, minLon], [maxLat, maxLon]];
        }
    }

    const createTrack = () => {
        if(gpx !== null){
            const request = {
                name: name,
                gpx: gpx,
                routeId: routeId
            };
            apiService.createTrack(request).then(() => history.push("/"));
        }
    }

    return (
        <div className="upload">
            <div className="uploadText">
                <h2>Subir track</h2>
                <p>Sube tu track en formato GPX para que otros usuarios puedan ver tus logros.</p>
                {user === null && (
                    <h3>Debes iniciar sesión para subir tracks</h3>
                )}
                {user !== null && (
                    <div className="uploadGrid">
                        <Label>Nombre:</Label>
                        <TextField value={name} onChange={(evt) => setName(evt.currentTarget.value)}/>
                        <Label>Fichero GPX:</Label>
                        <input onChange={setFile} type="file" id="gpx"/>
                        <Label>Ruta asociada (opcional):</Label>
                        <TagPicker 
                            itemLimit={1}
                            onResolveSuggestions={filterSuggestions}
                            onChange={(item) => setRouteId(item?.[0].key as string)}
                            getTextFromItem={getTextFromItem}
                        />
                        <PrimaryButton onClick={() => createTrack()} disabled={!enabledButton()}>Subir track</PrimaryButton>
                    </div>
                )}
                <br/>
                {track && <MapBase bounds={routeBounds()}>
                    {polylines}
                </MapBase>}
            </div>
        </div>
    );
}

export default UploadTrackView;