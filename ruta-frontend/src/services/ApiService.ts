import Route from "../models/Route";

export class ApiService{
    private baseUrl = "http://localhost:8080/api"

    getRoute(id: string): Promise<Route> {
        return fetch(`${this.baseUrl}/route/${id}`)
        .then(t => t.json())
    }

}
