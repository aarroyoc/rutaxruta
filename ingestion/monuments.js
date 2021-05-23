const axios = require("axios").default;
const MongoClient = require("mongodb").MongoClient;

const MONUMENTS_URI = "https://opendata.jcyl.es/ficheros/cct/wtur/monumentos.json"
const MONGODB_URI = ""

async function main(){
    const client = new MongoClient(MONGODB_URI);
    const response = await axios.get(MONUMENTS_URI, {responseType: "json"});
    await client.connect();
    const db = client.db("rutaxruta");
    const collection = db.collection("poi");
    const poiDocs = response.data.monumentos.map(monument => {
        return {
            name: monument.nombre,
            description: monument.Descripcion,
            type: "monument",
            location: {
                type: "Point",
                coordinates: [parseFloat(monument.coordenadas.longitud), parseFloat(monument.coordenadas.latitud)]
            }
        }
    });
    const result = await collection.insertMany(poiDocs);
    console.log(`Inserted ${result.insertedCount} documents`);
}

main()
.then(() => console.log("Finished"))
.catch(e => console.error(e))
