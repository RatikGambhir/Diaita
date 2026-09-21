package com.diaita.repo

import com.diaita.database.SQLiteDatabase
import com.diaita.dto.AuthUserDto
import com.diaita.entity.AuthUserEntity
import java.sql.ResultSet
import java.time.Instant

class AuthRepo(private val database: SQLiteDatabase) {
    fun createUser(user: AuthUserEntity): Boolean = database.connection { connection ->
        val sql = """
            INSERT INTO users (id, email, display_name, password_hash, password_salt, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        runCatching {
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, user.id)
                statement.setString(2, user.email)
                statement.setString(3, user.displayName)
                statement.setString(4, user.passwordHash)
                statement.setString(5, user.passwordSalt)
                statement.setString(6, user.createdAt)
                statement.setString(7, user.createdAt)
                statement.executeUpdate() == 1
            }
        }.getOrDefault(false)
    }

    fun findByEmail(email: String): AuthUserEntity? = database.connection { connection ->
        connection.prepareStatement(
            "SELECT id, email, display_name, password_hash, password_salt, created_at FROM users WHERE email = ? COLLATE NOCASE"
        ).use { statement ->
            statement.setString(1, email)
            statement.executeQuery().use { result -> if (result.next()) result.toEntity() else null }
        }
    }

    fun findPublicUser(userId: String): AuthUserDto? = database.connection { connection ->
        connection.prepareStatement(
            "SELECT id, email, display_name, created_at FROM users WHERE id = ?"
        ).use { statement ->
            statement.setString(1, userId)
            statement.executeQuery().use { result ->
                if (!result.next()) return@use null
                AuthUserDto(
                    id = result.getString("id"),
                    email = result.getString("email"),
                    displayName = result.getString("display_name"),
                    createdAt = result.getString("created_at")
                )
            }
        }
    }

    fun createSession(sessionId: String, userId: String, expiresAt: Instant) = database.connection { connection ->
        connection.prepareStatement(
            "INSERT INTO sessions (id, user_id, expires_at, created_at) VALUES (?, ?, ?, ?)"
        ).use { statement ->
            statement.setString(1, sessionId)
            statement.setString(2, userId)
            statement.setString(3, expiresAt.toString())
            statement.setString(4, Instant.now().toString())
            statement.executeUpdate()
        }
    }

    fun isSessionActive(sessionId: String, userId: String): Boolean = database.connection { connection ->
        connection.prepareStatement(
            "SELECT expires_at FROM sessions WHERE id = ? AND user_id = ? AND revoked_at IS NULL"
        ).use { statement ->
            statement.setString(1, sessionId)
            statement.setString(2, userId)
            statement.executeQuery().use { result ->
                result.next() && runCatching { Instant.parse(result.getString("expires_at")) > Instant.now() }
                    .getOrDefault(false)
            }
        }
    }

    fun revokeSession(sessionId: String, userId: String): Boolean = database.connection { connection ->
        connection.prepareStatement(
            "UPDATE sessions SET revoked_at = ? WHERE id = ? AND user_id = ? AND revoked_at IS NULL"
        ).use { statement ->
            statement.setString(1, Instant.now().toString())
            statement.setString(2, sessionId)
            statement.setString(3, userId)
            statement.executeUpdate() == 1
        }
    }

    private fun ResultSet.toEntity() = AuthUserEntity(
        id = getString("id"),
        email = getString("email"),
        displayName = getString("display_name"),
        passwordHash = getString("password_hash"),
        passwordSalt = getString("password_salt"),
        createdAt = getString("created_at")
    )
}
