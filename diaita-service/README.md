# Diaita service

Ktor 3 API for first-party password authentication, profiles, nutrition logs, workout logs, recommendations, and exercise search. Persistence is handled by SQLite through JDBC; the schema and starter exercise catalog initialize automatically.

```bash
JWT_SECRET="replace-this-with-a-long-random-secret" ./gradlew run
```

Optional environment variables:

- `DIAITA_DB_PATH` changes the SQLite file location.
- `DIAITA_CORS_HOSTS` changes the allowed frontend hosts (for example, `localhost:3100,127.0.0.1:3100`).
- `SPOONACULAR_API_KEY` enables remote food search.

Run the complete unit, route, persistence, and end-to-end suite with `./gradlew test`.
