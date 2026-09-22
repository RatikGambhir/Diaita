package com.diaita.database

import java.sql.Connection
import java.sql.DriverManager

class SQLiteDatabase(path: String) {
    private val jdbcUrl = if (path.startsWith("jdbc:sqlite:")) path else "jdbc:sqlite:$path"

    init {
        Class.forName("org.sqlite.JDBC")
        connection { connection ->
            connection.createStatement().use { statement ->
                statement.execute("PRAGMA foreign_keys = ON")
                statement.execute("PRAGMA journal_mode = WAL")
                statement.execute("PRAGMA busy_timeout = 5000")
            }
        }
        initializeSchema()
    }

    fun <T> connection(block: (Connection) -> T): T =
        DriverManager.getConnection(jdbcUrl).use { connection ->
            connection.createStatement().use { statement ->
                statement.execute("PRAGMA foreign_keys = ON")
                statement.execute("PRAGMA busy_timeout = 5000")
            }
            block(connection)
        }

    fun <T> transaction(block: (Connection) -> T): T = connection { connection ->
        connection.autoCommit = false
        try {
            block(connection).also { connection.commit() }
        } catch (exception: Exception) {
            connection.rollback()
            throw exception
        } finally {
            connection.autoCommit = true
        }
    }

    private fun initializeSchema() = connection { connection ->
        connection.createStatement().use { statement ->
            SCHEMA_STATEMENTS.forEach(statement::executeUpdate)
        }
        seedExercises(connection)
    }

    private fun seedExercises(connection: Connection) {
        val sql = """
            INSERT OR IGNORE INTO exercises
                (id, exercise, exercise_type, exercise_variation, primary_fitness_focus, secondary_fitness_focus, equipment, mechanics, utility, force)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        connection.prepareStatement(sql).use { statement ->
            EXERCISE_SEEDS.forEach { values ->
                values.forEachIndexed { index, value -> statement.setObject(index + 1, value) }
                statement.addBatch()
            }
            statement.executeBatch()
        }
    }

    companion object {
        private val SCHEMA_STATEMENTS = listOf(
            """CREATE TABLE IF NOT EXISTS users (
                id TEXT PRIMARY KEY,
                email TEXT NOT NULL UNIQUE COLLATE NOCASE,
                display_name TEXT NOT NULL,
                password_hash TEXT NOT NULL,
                password_salt TEXT NOT NULL,
                created_at TEXT NOT NULL,
                updated_at TEXT NOT NULL
            )""",
            """CREATE TABLE IF NOT EXISTS sessions (
                id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                expires_at TEXT NOT NULL,
                revoked_at TEXT,
                created_at TEXT NOT NULL
            )""",
            "CREATE INDEX IF NOT EXISTS idx_sessions_user_id ON sessions(user_id)",
            """CREATE TABLE IF NOT EXISTS user_profiles (
                user_id TEXT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
                profile_json TEXT NOT NULL,
                updated_at TEXT NOT NULL
            )""",
            """CREATE TABLE IF NOT EXISTS user_settings (
                user_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                section TEXT NOT NULL,
                data_json TEXT NOT NULL,
                updated_at TEXT NOT NULL,
                PRIMARY KEY (user_id, section)
            )""",
            """CREATE TABLE IF NOT EXISTS recommendations (
                user_id TEXT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
                data_json TEXT NOT NULL,
                updated_at TEXT NOT NULL
            )""",
            """CREATE TABLE IF NOT EXISTS meals (
                id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                meal_type TEXT NOT NULL CHECK (meal_type IN ('breakfast','lunch','dinner','snack')),
                eaten_at TEXT NOT NULL,
                notes TEXT,
                created_at TEXT NOT NULL,
                updated_at TEXT NOT NULL
            )""",
            "CREATE INDEX IF NOT EXISTS idx_meals_user_eaten_at ON meals(user_id, eaten_at)",
            """CREATE TABLE IF NOT EXISTS meal_items (
                id TEXT PRIMARY KEY,
                meal_id TEXT NOT NULL REFERENCES meals(id) ON DELETE CASCADE,
                user_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                item_type TEXT NOT NULL,
                item_id TEXT,
                item_name TEXT NOT NULL,
                brand_name TEXT,
                quantity REAL NOT NULL,
                unit TEXT,
                calories REAL NOT NULL,
                protein_g REAL NOT NULL,
                carbs_g REAL NOT NULL,
                fat_g REAL NOT NULL,
                position INTEGER NOT NULL,
                created_at TEXT NOT NULL,
                updated_at TEXT NOT NULL
            )""",
            "CREATE INDEX IF NOT EXISTS idx_meal_items_user_meal ON meal_items(user_id, meal_id)",
            """CREATE TABLE IF NOT EXISTS exercises (
                id INTEGER PRIMARY KEY,
                exercise TEXT NOT NULL,
                exercise_type TEXT,
                exercise_variation TEXT,
                primary_fitness_focus TEXT,
                secondary_fitness_focus TEXT,
                equipment TEXT,
                mechanics TEXT,
                utility TEXT,
                force TEXT
            )""",
            "CREATE INDEX IF NOT EXISTS idx_exercises_name ON exercises(exercise COLLATE NOCASE)",
            """CREATE TABLE IF NOT EXISTS workout_logs (
                id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                name TEXT NOT NULL,
                performed_at TEXT NOT NULL,
                duration_minutes INTEGER NOT NULL DEFAULT 0,
                notes TEXT,
                created_at TEXT NOT NULL,
                updated_at TEXT NOT NULL
            )""",
            "CREATE INDEX IF NOT EXISTS idx_workout_logs_user_date ON workout_logs(user_id, performed_at DESC)",
            """CREATE TABLE IF NOT EXISTS workout_exercises (
                id TEXT PRIMARY KEY,
                workout_id TEXT NOT NULL REFERENCES workout_logs(id) ON DELETE CASCADE,
                user_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                exercise_id INTEGER REFERENCES exercises(id) ON DELETE SET NULL,
                exercise_name TEXT NOT NULL,
                category TEXT NOT NULL,
                sets INTEGER NOT NULL DEFAULT 0,
                reps INTEGER,
                weight_kg REAL,
                duration_minutes INTEGER,
                distance_km REAL,
                notes TEXT,
                position INTEGER NOT NULL,
                created_at TEXT NOT NULL,
                updated_at TEXT NOT NULL
            )""",
            "CREATE INDEX IF NOT EXISTS idx_workout_exercises_workout ON workout_exercises(workout_id, position)"
        )

        private val EXERCISE_SEEDS = listOf(
            listOf(1, "Barbell Back Squat", "compound", "barbell", "legs", "core", "barbell", "compound", "strength", "push"),
            listOf(2, "Bench Press", "compound", "barbell", "chest", "triceps", "barbell", "compound", "strength", "push"),
            listOf(3, "Deadlift", "compound", "barbell", "back", "legs", "barbell", "compound", "strength", "pull"),
            listOf(4, "Overhead Press", "compound", "barbell", "shoulders", "triceps", "barbell", "compound", "strength", "push"),
            listOf(5, "Pull-Up", "compound", "bodyweight", "back", "arms", "pull-up bar", "compound", "strength", "pull"),
            listOf(6, "Dumbbell Row", "compound", "dumbbell", "back", "arms", "dumbbell", "compound", "strength", "pull"),
            listOf(7, "Bicep Curl", "isolation", "dumbbell", "arms", null, "dumbbell", "isolation", "strength", "pull"),
            listOf(8, "Tricep Extension", "isolation", "cable", "arms", null, "cable", "isolation", "strength", "push"),
            listOf(9, "Running", "cardio/conditioning", "steady state", "cardio", "legs", "none", "cyclic", "conditioning", null),
            listOf(10, "Cycling", "cardio/conditioning", "steady state", "cardio", "legs", "bike", "cyclic", "conditioning", null),
            listOf(11, "Rowing", "cardio/conditioning", "ergometer", "back", "legs", "rower", "cyclic", "conditioning", "pull"),
            listOf(12, "Jump Rope", "cardio/conditioning", "interval", "cardio", "calves", "jump rope", "cyclic", "conditioning", null),
            listOf(13, "Walking Lunge", "compound", "bodyweight", "legs", "glutes", "none", "compound", "strength", "push"),
            listOf(14, "Mountain Climber", "cardio/conditioning", "bodyweight", "core", "shoulders", "none", "compound", "conditioning", null),
            listOf(15, "Dynamic Hip Stretch", "dynamic stretching", "mobility", "legs", "hips", "none", "mobility", "warmup", null)
        )
    }
}
