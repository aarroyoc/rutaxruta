const axios = require("axios").default;
const MongoClient = require("mongodb").MongoClient;

const EVENTS_URI = "https://datosabiertos.jcyl.es/web/jcyl/risp/es/cultura-ocio/agenda_cultural/1284806871500.json"
const MONGODB_URI = ""

async function main() {
    const client = new MongoClient(MONGODB_URI);
    const response = await axios.get(EVENTS_URI, {responseType: "json"});
    await client.connect();
    const db = client.db("rutaxruta");
    const collection = db.collection("poi");
    const docs = response.data.map(event => {
        return {
            name: event.fields.titulo,
            description: event.fields.descripcion,
            type: "event",
            location: event.geometry
        }
    });
    const result = await collection.insertMany(docs);
    console.log(`Inserted ${result.insertedCount} documents`);
}

main()
.then(() => console.log("Finished"))
.catch(e => console.error(e))