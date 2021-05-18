import { MapContainer, WMSTileLayer, Marker, useMapEvent, Polyline } from "react-leaflet";
import { CRS, LatLng, LatLngTuple } from "leaflet";

import User from "../models/User";
import { ApiService } from "../services/ApiService";
import "./RouteMaker.css";
import { useState } from "react";
import { ChoiceGroup, PrimaryButton, TextField } from "@fluentui/react";

type Props = {
    apiService: ApiService,
    user: User | null,
}

type WmsType = "ign" | "itacyl";

export default function RouteMaker({apiService, user}: Props) {
    const [wms, setWms] = useState<WmsType>("ign");
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [route, setRoute] = useState<LatLng[]>([]);
    const [error, setError] = useState(false);

    const wmsOptions = [
        {key: "ign", text: "Mapa"},
        {key: "itacyl", text: "Satélite"}
    ];

    const cylBounds = () => [[40.82, -6.06], [42.56, -6.06], [42.56, -3.33], [40.82, -3.33]] as LatLngTuple[];

    const ClickHandler = () => {
        useMapEvent("click", (evt) => {
            console.log(evt.latlng);
            const newRoute = [...route];
            newRoute.push(evt.latlng);
            setRoute(newRoute);
        });
        return null;
    }

    const publish = () => {
        if(name.length === 0){
            return;
        }
        if(description.length === 0){
            return;
        }
        if(route.length < 2){
            return;
        }

        apiService.createRoute({
            name,
            description,
            geojson: {
                type: "Feature",
                geometry: {
                    type: "LineString",
                    coordinates: route.map(t => [t.lng, t.lat])
                },
                properties: {

                }
            }
        }).then(() => {
            window.location.href = "/";
        }).catch(() => {
            setError(true);
        });
    };


    return (
        <div className="maker">
            <div className="makerText">
                <h2>Crea tu ruta</h2>
                <p>Diseña tu ruta con el mapa presente en esta página. Es muy recomendable que la ruta que crees la hayas realizado con anterioridad. Todas las rutas son revisadas por el equipo de Ruta x Ruta antes de ser publicadas.</p>
                <p>Haz click para añadir un punto, que se conectará con el anterior por línea recta. Haz click sobre el último punto para borrarlo.</p>
                {user === null && (
                <h3>Debes iniciar sesión para crear rutas</h3>
                )}
                {user !== null && (
                    <div className="makerEditor">
                        <div>
                            <MapContainer bounds={cylBounds()} scrollWheelZoom={true} style={{width: "500px", height: "500px"}}>
                                { wms === "ign" && <WMSTileLayer url="https://www.ign.es/wms-inspire/mapa-raster" format="image/png" crs={CRS.EPSG4326} tileSize={256} layers="mtn_rasterizado" attribution="© Instituto Geográfico Nacional"/> }
                                { wms === "itacyl" && <WMSTileLayer url="http://orto.wms.itacyl.es/WMS?" format="image/jpeg" crs={CRS.EPSG4326} tileSize={256} layers="Ortofoto_2017" attribution="© ITaCyL. Junta de Castilla y León"/>}
                                <ClickHandler/>
                                <Polyline positions={route}/>
                                {route.length > 0 && (
                                    <Marker position={route[route.length-1]} eventHandlers={{
                                        click: () => {
                                            const newRoute = [...route];
                                            newRoute.pop();
                                            setRoute(newRoute);
                                        }
                                    }}/>
                                )}
                            </MapContainer>
                        </div>
                        <div>
                            <ChoiceGroup defaultSelectedKey="ign" options={wmsOptions} onChange={(evt, option) => setWms(option?.key as WmsType)} label="Base" required={true}/>
                            <TextField value={name} label="Nombre" required onChange={(evt, newValue) => setName(newValue || "")}/>
                            <TextField value={description} label="Descripción" required multiline onChange={(evt, newValue) => setDescription(newValue || "")}/>
                            <br/>
                            <PrimaryButton text="Publicar ruta" onClick={publish}/>
                            <br/>
                            { error && <p className="error">Ha habido un error a la hora de publicar la ruta. Revisa los datos.</p> }
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}