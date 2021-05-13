import { List, Spinner, SpinnerSize, Link, Panel } from "@fluentui/react";
import { MapContainer, GeoJSON, WMSTileLayer, Marker } from "react-leaflet";
import React, { useEffect, useState } from "react";
import Route from "../models/Route";
import { ApiService } from "../services/ApiService";
import { CRS } from "leaflet";
import "./RouteView.css";
import Poi from "../models/Poi";

type Props = {
    id: string,
    apiService: ApiService,
};

export function RouteView({id, apiService}: Props){
    const [route, setRoute] = useState<Route | null>(null);
    const [openPanel, setOpenPanel] = useState<number | undefined>(undefined);
    const [pois, setPois] = useState<Poi[]>([]);

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

    const onRenderMonument = (monument?: Poi, index?: number) => {
        return (
            <div>
                <Link onClick={() => setOpenPanel(index)}>{monument?.name}</Link>
                <Panel 
                    headerText={monument?.name}
                    isBlocking={false}
                    isOpen={openPanel === index}
                    onDismiss={() => setOpenPanel(undefined)}
                    closeButtonAriaLabel="Close">
                    <div dangerouslySetInnerHTML={{__html: monument?.description || ""}} />
                </Panel>
            </div>
        );
    }

    const monuments = pois.filter(t => t.type === "monument");

    return (<>
    {!route && (
        <Spinner label="Cargando ruta" size={SpinnerSize.large}/>
    )}
    {route && (
        <div className="routeView">
            <MapContainer bounds={routeBounds()} scrollWheelZoom={true} style={{width: "500px", height: "500px"}}>
                <WMSTileLayer url=" http://orto.wms.itacyl.es/WMS?" format="image/jpeg" crs={CRS.EPSG4326} tileSize={256} layers="Ortofoto_2020" attribution="© ITaCyL. Junta de Castilla y León"/>
                <GeoJSON data={route.geojson} style={{color: "#8A430A", stroke: true, weight: 3}}/>
                {openPanel && (
                    <Marker position={[monuments[openPanel].lat, monuments[openPanel].lon]}/>
                )}
            </MapContainer>
            <div className="routeViewData">
                <h3>Monumentos cercanos</h3>
                <h3>Bares y restaurantes cercanos</h3>
                <div style={{overflowY: "scroll"}}>
                    <List items={monuments} onRenderCell={onRenderMonument}/>
                </div>
                <div></div>
                <h3>Eventos próximos cercanos</h3>
                <h3>Tracks de los usuarios</h3>
                <div></div>
                <div></div>
                <h3>Comentarios</h3>
                <div></div>
                <div className="commentsArea"></div>
                <h3>Imágenes</h3>
                <div></div>
                <div className="imagesArea"></div>
            </div>
        </div>
    )}
    </>)
}