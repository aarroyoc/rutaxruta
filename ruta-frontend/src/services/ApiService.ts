import Route from "../models/Route";
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

}
