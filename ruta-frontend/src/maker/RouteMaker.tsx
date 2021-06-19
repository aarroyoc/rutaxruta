import { Marker, useMapEvent, Polyline } from "react-leaflet";
import { LatLng } from "leaflet";

import User from "../models/User";
import { ApiService } from "../services/ApiService";
import "./RouteMaker.css";
import { useState } from "react";
import { PrimaryButton, TextField } from "@fluentui/react";
import { useHistory } from "react-router";
import MapBase from "../map/MapBase";

type Props = {
    apiService: ApiService,
    user: User | null,
}

export default function RouteMaker({apiService, user}: Props) {
    const history = useHistory();
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [route, setRoute] = useState<LatLng[]>([]);
    const [error, setError] = useState(false);

    const ClickHandler = () => {
        useMapEvent("click", (evt) => {
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
            history.push("/");
        }).catch(() => {
            setError(true);
        });
    };


    return (
        <div className="maker">
            <div className="makerText">
                <h2>Propón tu ruta</h2>
                <p>Diseña tu ruta con el mapa presente en esta página. Es muy recomendable que la ruta que crees la hayas realizado con anterioridad. Todas las rutas son revisadas por el equipo de Ruta x Ruta antes de ser publicadas.</p>
                <p>Haz click para añadir un punto, que se conectará con el anterior por línea recta. Haz click sobre el último punto para borrarlo.</p>
                {user === null && (
                <h3>Debes iniciar sesión para crear rutas</h3>
                )}
                {user !== null && (
                    <div className="makerEditor">
                        <div>
                            <MapBase>
                                <Polyline positions={route}/>
                                <ClickHandler/>
                                {route.length > 0 && (
                                    <Marker position={route[route.length-1]} eventHandlers={{
                                        click: () => {
                                            const newRoute = [...route];
                                            newRoute.pop();
                                            setRoute(newRoute);
                                        }
                                    }}/>
                                )}
                            </MapBase>
                        </div>
                        <div>
                            <TextField value={name} label="Nombre" required onChange={(evt, newValue) => setName(newValue || "")}/>
                            <TextField value={description} label="Descripción" required multiline onChange={(evt, newValue) => setDescription(newValue || "")}/>
                            <br/>
                            <PrimaryButton text="Proponer ruta" onClick={publish}/>
                            <br/>
                            { error && <p className="error">Ha habido un error a la hora de publicar la ruta. Revisa los datos.</p> }
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}