import { ChoiceGroup } from "@fluentui/react";
import { CRS, LatLngTuple } from "leaflet";
import { useState } from "react";
import { MapContainer, WMSTileLayer } from "react-leaflet";
import "./MapBase.css";

type Props = {
    children: React.ReactNode,
    bounds?: LatLngTuple[],
}

type WmsType = "ign" | "itacyl";

const wmsOptions = [
    {key: "ign", text: "Mapa"},
    {key: "itacyl", text: "Satélite"}
];

function MapBase({bounds, children}: Props){
    const [wms, setWms] = useState<WmsType>("itacyl");
    if(bounds === undefined){
        bounds = [[40.82, -6.06], [42.56, -6.06], [42.56, -3.33], [40.82, -3.33]] as LatLngTuple[];
    }
    return (
        <div className="mapBase">
            <MapContainer boxZoom bounds={bounds} scrollWheelZoom={true} className="mapContainer">
                { wms === "ign" && <WMSTileLayer url="https://www.ign.es/wms-inspire/mapa-raster" format="image/png" crs={CRS.EPSG4326} tileSize={256} layers="mtn_rasterizado" attribution="© Instituto Geográfico Nacional"/> }
                { wms === "itacyl" && <WMSTileLayer url="http://orto.wms.itacyl.es/WMS?" format="image/jpeg" crs={CRS.EPSG4326} tileSize={256} layers="Ortofoto_2017" attribution="© ITaCyL. Junta de Castilla y León"/>}
                {children}
            </MapContainer>
            <div className="mapChoice">
                <ChoiceGroup defaultSelectedKey={wms} options={wmsOptions} onChange={(evt, option) => setWms(option?.key as WmsType)} label="Base"/>
            </div>
        </div>
    );
}

export default MapBase;