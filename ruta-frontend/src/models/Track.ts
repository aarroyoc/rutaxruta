import TrackLine from "./TrackLine";

type Track = {
    name: string,
    minSpeed: number,
    maxSpeed: number,
    segments: TrackLine[],
}

export default Track;