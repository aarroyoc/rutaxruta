# Ruta x ruta

## Running locally

* Start MongoDB
```shell
docker-compose up mongo
```

* Start backend

Required environment variables:
- MONGODB_URI (defaults to localhost)
- GOOGLE_CLIENT_ID
- GOOGLE_CLIENT_SECRET
- PORT (defaults to 8080)

```shell
mvn clean package
(mvn -DskipTests=true package)
```

* Start frontend

Required environment variables
- REACT_APP_API_URI (defaults to http://localhost:8080)
- GOOGLE_CLIENT_ID

```shell
cd ruta-frontend
npm install
npm run start
```