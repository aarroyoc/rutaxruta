import TrackLine from "./TrackLine";

type Track = {
    name: string,
    userId: string,
    userName: string,
    minSpeed: number,
    maxSpeed: number,
    segments: TrackLine[],
    duration: number,
    distance: number,
    meanSpeed: number,
}

export default Track;