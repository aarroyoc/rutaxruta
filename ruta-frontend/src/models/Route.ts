import TrackInfo from "./TrackInfo";

type Route = {
    id: string,
    name: string,
    description: string,
    comments: string[],
    geojson: any,
    tracks: TrackInfo[]
};

export default Route;