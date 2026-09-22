# Diaita

Diaita is a self-hosted nutrition and workout tracker built with Nuxt/Vue and Kotlin/Ktor. Account data, profiles, food logs, and workouts are stored in a local SQLite database.

## Run locally

Requirements: JDK 23 and Node.js 20 or newer.

Start the API:

```bash
cd diaita-service
JWT_SECRET="replace-this-with-a-long-random-secret" ./gradlew run
```

The service listens on `http://localhost:8080` and creates `diaita-service/data/diaita.db` on first launch.

In a second terminal, start the web app:

```bash
cd diaita-web/ui
npm install
npm run dev
```

Open `http://localhost:3000`, create an account, complete the optional profile setup, and begin logging food and workouts.

## Configuration

- `JWT_SECRET`: signing secret for access tokens. Always set this outside local development.
- `DIAITA_DB_PATH`: optional SQLite file path; defaults to `./data/diaita.db` relative to the service.
- `DIAITA_CORS_HOSTS`: comma-separated frontend hosts without a scheme; defaults to `localhost:3000,127.0.0.1:3000`.
- `SPOONACULAR_API_KEY`: optional. Enables ingredient/product search. Custom foods and all tracking features work without it.
- `NUXT_PUBLIC_API_URL`: optional API base URL for the web build; defaults to `http://localhost:8080`.

## Verification

```bash
cd diaita-service && ./gradlew test
cd ../diaita-web/ui && npm run build
```
