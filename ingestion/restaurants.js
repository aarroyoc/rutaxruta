const parse = require("csv-parse");
const axios = require("axios").default;
const MongoClient = require("mongodb").MongoClient;
const Iconv = require("iconv").Iconv;

const RESTAURANTS_URI = "https://datosabiertos.jcyl.es/web/jcyl/risp/es/turismo/restaurantes/1284211839594.csv"
const MONGODB_URI = ""

const parser = parse({
    delimiter: ";",
    columns: true
});

const iconv = new Iconv("ISO-8859-1", "utf-8");

function buildDescription(record) {
    return `Nombre: ${record["Nombre"]}<br>
Dirección: ${record["Dirección"]} ${record["Localidad"]}<br>
Teléfono: ${record["Teléfono 1"]}<br>
Email: ${record["Email"]}`
}

async function main() {
    const client = new MongoClient(MONGODB_URI);
    const response = await axios.get(RESTAURANTS_URI, {responseType: "stream"});
    response.data.pipe(iconv).pipe(parser);
    await client.connect();
    const db = client.db("rutaxruta");
    const collection = db.collection("poi");
    const docs = [];
    for await (const record of parser) {
        if(record["GPS.Longitud"] !== ""){
            docs.push({
                name: record["Nombre"],
                description: buildDescription(record),
                type: "restaurant",
                location: {
                    type: "Point",
                    coordinates: [parseFloat(record["GPS.Longitud"].replace(",", ".")), parseFloat(record["GPS.Latitud"].replace(",", "."))]
                }
            });
        }
    }

    const result = await collection.insertMany(docs);
    console.log(`Inserted ${result.insertedCount} documents`);
}

main()
.then(() => console.log("Finished"))
.catch(e => console.error(e));