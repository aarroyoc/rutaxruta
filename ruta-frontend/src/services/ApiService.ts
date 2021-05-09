import Route from "../models/Route";

export class ApiService{
    private baseUrl = process.env.REACT_APP_API_URI;
    private jwt: string | null = null;

    setJwt(newJwt: string) {
        this.jwt = newJwt;
    }

    getToken(googleToken: string): Promise<string> {
        return fetch(`${this.baseUrl}/token`, {method: "POST", body: googleToken})
        .then(r => r.text())
    }

    getRoute(id: string): Promise<Route> {
        return fetch(`${this.baseUrl}/route/${id}`)
        .then(t => t.json())
    }

}
