package com.diaita

import com.diaita.database.SQLiteDatabase
import io.ktor.server.application.*
import java.nio.file.Files
import java.nio.file.Path

fun Application.configureDatabases(): SQLiteDatabase {
    val configuredPath = System.getenv("DIAITA_DB_PATH")
        ?.trim()
        ?.takeIf(String::isNotEmpty)
        ?: environment.config.propertyOrNull("database.path")?.getString()
        ?: "./data/diaita.db"

    if (configuredPath != ":memory:" && !configuredPath.startsWith("file:")) {
        Path.of(configuredPath).toAbsolutePath().parent?.let(Files::createDirectories)
    }

    return SQLiteDatabase(configuredPath).also {
        log.info("SQLite database ready at {}", configuredPath)
    }
}
