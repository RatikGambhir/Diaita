package com.diaita

import com.diaita.database.SQLiteDatabase
import java.nio.file.Files
import java.time.Instant

fun testDatabase(): SQLiteDatabase {
    val path = Files.createTempFile("diaita-test-", ".db")
    path.toFile().deleteOnExit()
    return SQLiteDatabase(path.toString())
}

fun SQLiteDatabase.insertTestUser(userId: String, email: String = "$userId@example.com") {
    connection { connection ->
        connection.prepareStatement(
            """INSERT INTO users (id, email, display_name, password_hash, password_salt, created_at, updated_at)
               VALUES (?, ?, 'Test User', 'hash', 'salt', ?, ?)"""
        ).use { statement ->
            val now = Instant.now().toString()
            statement.setString(1, userId)
            statement.setString(2, email)
            statement.setString(3, now)
            statement.setString(4, now)
            statement.executeUpdate()
        }
    }
}
