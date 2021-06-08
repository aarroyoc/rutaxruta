import CreateRouteRequest from "../models/CreateRouteRequest";
import CreateTrackRequest from "../models/CreateTrackRequest";
import Poi from "../models/Poi";
import Route from "../models/Route";
import Track from "../models/Track";
import User from "../models/User";

export class ApiService{
    private baseUrl = process.env.REACT_APP_API_URI;
    private jwt: string | null = null;

    setJwt(newJwt: string) {
        this.jwt = newJwt;
    }

    getToken(googleToken: string): Promise<string> {
        return fetch(`${this.baseUrl}/token`, {method: "POST", body: googleToken})
        .then(r => r.text());
    }

    getMe(): Promise<User> {
        return fetch(`${this.baseUrl}/user/me`, {headers: {
            "Authorization": `Bearer ${this.jwt}`
        }}).then(t => t.json());
    }

    getRoute(id: string): Promise<Route> {
        return fetch(`${this.baseUrl}/route/${id}`)
        .then(t => t.json());
    }

    getRouteNearPois(id: string): Promise<Poi[]> {
        return fetch(`${this.baseUrl}/route/${id}/near`)
        .then(t => t.json());
    }

    listRoutes(): Promise<Array<Route>> {
        return fetch(`${this.baseUrl}/route`)
        .then(t => t.json())
    }

    createRoute(request: CreateRouteRequest): Promise<any> {
        return fetch(`${this.baseUrl}/route`, {
            method: "POST",
            body: JSON.stringify(request),
            headers: {
                "Authorization": `Bearer ${this.jwt}`,
                "Content-Type": "application/json"
            }
        });
    }

    getTrack(id: string): Promise<Track> {
        return fetch(`${this.baseUrl}/track/${id}`)
        .then(t => t.json());
    }

    previewTrack(request: CreateTrackRequest): Promise<Track> {
        return fetch(`${this.baseUrl}/track/preview`, {
            method: "POST",
            body: JSON.stringify(request),
            headers: {
                "Authorization": `Bearer ${this.jwt}`,
                "Content-Type": "application/json"
            }
        }).then(t => t.json());
    }

    createTrack(request: CreateTrackRequest): Promise<any> {
        return fetch(`${this.baseUrl}/track`, {
            method: "POST",
            body: JSON.stringify(request),
            headers: {
                "Authorization": `Bearer ${this.jwt}`,
                "Content-Type": "application/json"
            }
        })
    }

}
