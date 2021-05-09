import { Spinner, SpinnerSize, Text } from "@fluentui/react";
import { MapContainer, GeoJSON, WMSTileLayer } from "react-leaflet";
import { useEffect, useState } from "react";
import Route from "../models/Route";
import { ApiService } from "../services/ApiService";
import { CRS } from "leaflet";

type Props = {
    id: string
};

export function RouteView({id}: Props){
    
    const [route, setRoute] = useState<Route | null>(null);
    useEffect(() => {
        const apiService = new ApiService();
        apiService.getRoute(id).then(newRoute => {
            setRoute(newRoute);
        });
    });
    const routeBounds = () => {
        if(route){
            return route.geojson.geometry.coordinates.map((t: number[]) => [t[1], t[0]]);
        }
    }

    return (<>
    {!route && (
        <Spinner label="Cargando ruta" size={SpinnerSize.large}/>
    )}
    {route && (
        <>
        <h2>{route.name}</h2>
        <MapContainer bounds={routeBounds()} scrollWheelZoom={true} style={{width: "500px", height: "500px"}}>
            <WMSTileLayer url=" http://orto.wms.itacyl.es/WMS?" format="image/jpeg" crs={CRS.EPSG4326} tileSize={256} layers="Ortofoto_2020" attribution="© ITaCyL. Junta de Castilla y León"/>
            <GeoJSON data={route.geojson} style={{color: "#8A430A", stroke: true, weight: 3}}/>
        </MapContainer>
        <Text>{route.description}</Text>
        </>
    )}
    </>)
}