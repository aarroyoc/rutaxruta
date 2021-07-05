import TrackLine from "./TrackLine";
import TrackPoint from "./TrackPoint";

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
    points: TrackPoint[],
}

export default Track;