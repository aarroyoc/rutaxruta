import TrackInfo from "./TrackInfo";

type Route = {
    id: string,
    name: string,
    description: string,
    geojson: any,
    tracks: TrackInfo[]
};

export default Route;