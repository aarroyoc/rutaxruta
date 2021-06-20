import TrackLine from "./TrackLine";

type Track = {
    name: string,
    userId: string,
    userName: string,
    minSpeed: number,
    maxSpeed: number,
    segments: TrackLine[],
}

export default Track;