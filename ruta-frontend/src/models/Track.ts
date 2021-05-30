import TrackLine from "./TrackLine";

type Track = {
    minSpeed: number,
    maxSpeed: number,
    segments: TrackLine[],
}

export default Track;