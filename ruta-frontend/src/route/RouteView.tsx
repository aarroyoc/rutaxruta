import { List, Spinner, SpinnerSize, Link, Panel } from "@fluentui/react";
import { GeoJSON, Marker } from "react-leaflet";
import React, { useEffect, useState } from "react";
import Route from "../models/Route";
import { ApiService } from "../services/ApiService";
import { Icon } from "leaflet";
import "./RouteView.css";
import Poi from "../models/Poi";
import TrackInfo from "../models/TrackInfo";
import { useHistory } from "react-router-dom";
import MapBase from "../map/MapBase";
import flag_icon from "./flag.svg";
import play_icon from "./play.svg";

type Props = {
    id: string,
    apiService: ApiService,
};

export function RouteView({id, apiService}: Props){
    const history = useHistory();
    const [route, setRoute] = useState<Route | null>(null);
    const [openPanel, setOpenPanel] = useState<number | undefined>(undefined);
    const [pois, setPois] = useState<Poi[]|null>(null);

    useEffect(() => {
        apiService.getRoute(id).then(newRoute => {
            setRoute(newRoute);
        });
        // eslint-disable-next-line
    }, [id]);
    useEffect(() => {
        apiService.getRouteNearPois(id).then(newPois => {
            setPois(newPois);
        });
        // eslint-disable-next-line
    }, [id]);
    const routeBounds = () => {
        if(route){
            return route.geojson.geometry.coordinates.map((t: number[]) => [t[1], t[0]]);
        }
    }

    const onRenderPoi = (poi?: Poi, index?: number) => {
        const poiIndex = pois?.findIndex(t => t.name === poi?.name);
        return (
            <div>
                <Link onClick={() => setOpenPanel(poiIndex)}>{poi?.name}</Link>
                <Panel 
                    headerText={poi?.name}
                    isBlocking={false}
                    isOpen={openPanel === poiIndex}
                    onDismiss={() => setOpenPanel(undefined)}
                    closeButtonAriaLabel="Close">
                    <div dangerouslySetInnerHTML={{__html: poi?.description || ""}} />
                </Panel>
            </div>
        );
    }

    const onRenderTrack = (trackInfo?: TrackInfo, index?: number) => {
        return (
            <div>
                <Link onClick={() => history.push(`/track/${trackInfo?.trackId}`)}>{trackInfo?.name}</Link>
            </div>
        );
    }

    const startMarker = (route: Route) => {
        const point = route.geojson.geometry.coordinates.map((t: number[]) => [t[1], t[0]])[0];
        const playIcon = new Icon({
            iconUrl: play_icon,
            iconAnchor: [10, 20],
            iconSize: [27, 27]
        });

        return <Marker icon={playIcon} position={point}/>;
    };

    const endMarker = (route: Route) => {
        const lastPos = route.geojson.geometry.coordinates.length - 1;
        const point = route.geojson.geometry.coordinates.map((t: number[]) => [t[1], t[0]])[lastPos];
        const flagIcon = new Icon({
            iconUrl: flag_icon,
            iconAnchor: [10, 20],
            iconSize: [27, 27]
        });
        

        return <Marker icon={flagIcon} position={point}/>;
    }

    const monuments = pois?.filter(t => t.type === "monument");
    const restaurants = pois?.filter(t => t.type === "restaurant");
    const events = pois?.filter(t => t.type === "event");

    return (<>
    {!route && (
        <Spinner label="Cargando ruta" size={SpinnerSize.large}/>
    )}
    {route && (
        <div className="routeView">
            <MapBase bounds={routeBounds()}>
                <GeoJSON data={route.geojson} style={{color: "#8A430A", stroke: true, weight: 3}}/>
                {startMarker(route)}
                {endMarker(route)}
                {openPanel && pois !== null && (
                    <Marker position={[pois[openPanel].lat, pois[openPanel].lon]}/>
                )}
            </MapBase>
            <div className="routeViewData">
                <h3>Monumentos cercanos</h3>
                <h3>Bares y restaurantes cercanos</h3>
                {(monuments !== undefined && <List style={{marginLeft: "10px", overflowY: "auto"}} items={monuments} onRenderCell={onRenderPoi}/>) || <Spinner size={SpinnerSize.medium}/>}
                {(restaurants !== undefined && <List style={{marginLeft: "10px", overflowY: "auto"}} items={restaurants} onRenderCell={onRenderPoi}/>) || <Spinner size={SpinnerSize.medium}/>}
                <h3>Eventos próximos cercanos</h3>
                <h3>Tracks de los usuarios</h3>
                {(events !== undefined && <List style={{marginLeft: "10px", overflowY: "auto"}} items={events} onRenderCell={onRenderPoi}/>) || <Spinner size={SpinnerSize.medium}/>}
                <List style={{marginLeft: "10px", overflowY: "auto"}} items={route.tracks} onRenderCell={onRenderTrack}/>
            </div>
        </div>
    )}
    </>)
}