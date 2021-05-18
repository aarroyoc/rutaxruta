type CreateRouteRequest = {
    name: string,
    description: string,
    geojson: {
        type: string,
        geometry: {
            type: string,
            coordinates: number[][]
        },
        properties: {

        }
    }
}

export default CreateRouteRequest;