package com.diaita.repo

import com.diaita.database.SQLiteDatabase
import com.diaita.dto.UpsertWorkoutRequestDto
import com.diaita.dto.WorkoutExerciseLogDto
import com.diaita.dto.WorkoutLogDto
import com.diaita.dto.WorkoutSearchRequestDto
import com.diaita.dto.WorkoutStatsDto
import com.diaita.entity.ExerciseEntity
import com.diaita.lib.factories.PaginatedResult
import com.diaita.lib.factories.Result
import java.sql.Connection
import java.sql.ResultSet
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class WorkoutRepo(private val database: SQLiteDatabase) {
    suspend fun searchExercises(request: WorkoutSearchRequestDto): Result<PaginatedResult<ExerciseEntity>> {
        val validation = request.validate()
        if (!validation.isValid) {
            return Result(null, IllegalArgumentException(validation.errorMessage ?: "Invalid workout search request"))
        }

        return runCatching {
            val normalized = request.normalized()
            val where = mutableListOf<String>()
            val values = mutableListOf<Any>()
            normalized.query?.let {
                where += "LOWER(exercise) LIKE ?"
                values += "%${it.lowercase()}%"
            }
            normalized.exerciseTypes?.let { types ->
                where += "exercise_type IN (${types.joinToString { "?" }})"
                values.addAll(types)
            }
            normalized.exerciseVariation?.let {
                where += "LOWER(COALESCE(exercise_variation, '')) LIKE ?"
                values += "%${it.lowercase()}%"
            }
            normalized.primaryFitnessFocuses?.let { focuses ->
                where += "LOWER(COALESCE(primary_fitness_focus, '')) IN (${focuses.joinToString { "?" }})"
                values.addAll(focuses)
            }
            val whereSql = if (where.isEmpty()) "" else " WHERE ${where.joinToString(" AND ")}"

            database.connection { connection ->
                val total = connection.prepareStatement("SELECT COUNT(*) FROM exercises$whereSql").use { statement ->
                    statement.bind(values)
                    statement.executeQuery().use { result -> result.next(); result.getInt(1) }
                }
                val offset = normalized.page * normalized.pageSize
                val rows = connection.prepareStatement(
                    """SELECT id, exercise, exercise_type, exercise_variation, primary_fitness_focus,
                              secondary_fitness_focus, equipment, mechanics, utility, force
                       FROM exercises$whereSql ORDER BY exercise COLLATE NOCASE, id LIMIT ? OFFSET ?"""
                ).use { statement ->
                    statement.bind(values)
                    statement.setInt(values.size + 1, normalized.pageSize)
                    statement.setInt(values.size + 2, offset)
                    statement.executeQuery().use { result -> buildList { while (result.next()) add(result.toExercise()) } }
                }
                PaginatedResult(rows, total, normalized.page, normalized.pageSize, offset + rows.size < total)
            }
        }.fold(
            onSuccess = { Result(it, null) },
            onFailure = { Result(null, it as? Exception ?: RuntimeException(it)) }
        )
    }

    suspend fun listWorkouts(userId: String, query: String?): List<WorkoutLogDto> = database.connection { connection ->
        val normalizedQuery = query?.trim()?.takeIf(String::isNotEmpty)
        val sql = buildString {
            append("SELECT id, user_id, name, performed_at, duration_minutes, notes FROM workout_logs WHERE user_id = ?")
            if (normalizedQuery != null) append(" AND LOWER(name) LIKE ?")
            append(" ORDER BY performed_at DESC, created_at DESC")
        }
        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, userId)
            normalizedQuery?.let { statement.setString(2, "%${it.lowercase()}%") }
            statement.executeQuery().use { result ->
                buildList { while (result.next()) add(result.toWorkout(connection)) }
            }
        }
    }

    suspend fun getWorkout(userId: String, workoutId: String): WorkoutLogDto? = database.connection { connection ->
        connection.prepareStatement(
            "SELECT id, user_id, name, performed_at, duration_minutes, notes FROM workout_logs WHERE id = ? AND user_id = ?"
        ).use { statement ->
            statement.setString(1, workoutId)
            statement.setString(2, userId)
            statement.executeQuery().use { result -> if (result.next()) result.toWorkout(connection) else null }
        }
    }

    suspend fun createWorkout(userId: String, request: UpsertWorkoutRequestDto): WorkoutLogDto? {
        val id = UUID.randomUUID().toString()
        return saveWorkout(userId, id, request, create = true)
    }

    suspend fun updateWorkout(userId: String, workoutId: String, request: UpsertWorkoutRequestDto): WorkoutLogDto? =
        saveWorkout(userId, workoutId, request, create = false)

    private suspend fun saveWorkout(
        userId: String,
        workoutId: String,
        request: UpsertWorkoutRequestDto,
        create: Boolean
    ): WorkoutLogDto? = runCatching {
        database.transaction { connection ->
            val now = Instant.now().toString()
            if (create) {
                connection.prepareStatement(
                    """INSERT INTO workout_logs
                       (id, user_id, name, performed_at, duration_minutes, notes, created_at, updated_at)
                       VALUES (?, ?, ?, ?, ?, ?, ?, ?)"""
                ).use { statement ->
                    statement.setString(1, workoutId)
                    statement.setString(2, userId)
                    statement.setString(3, request.name.trim())
                    statement.setString(4, request.performedAt.trim())
                    statement.setInt(5, request.durationMinutes)
                    statement.setString(6, request.notes?.trim()?.takeIf(String::isNotEmpty))
                    statement.setString(7, now)
                    statement.setString(8, now)
                    statement.executeUpdate()
                }
            } else {
                val changed = connection.prepareStatement(
                    """UPDATE workout_logs SET name = ?, performed_at = ?, duration_minutes = ?, notes = ?, updated_at = ?
                       WHERE id = ? AND user_id = ?"""
                ).use { statement ->
                    statement.setString(1, request.name.trim())
                    statement.setString(2, request.performedAt.trim())
                    statement.setInt(3, request.durationMinutes)
                    statement.setString(4, request.notes?.trim()?.takeIf(String::isNotEmpty))
                    statement.setString(5, now)
                    statement.setString(6, workoutId)
                    statement.setString(7, userId)
                    statement.executeUpdate()
                }
                if (changed == 0) return@transaction false
                connection.prepareStatement("DELETE FROM workout_exercises WHERE workout_id = ? AND user_id = ?").use { statement ->
                    statement.setString(1, workoutId)
                    statement.setString(2, userId)
                    statement.executeUpdate()
                }
            }

            request.exercises.forEachIndexed { position, exercise ->
                connection.prepareStatement(
                    """INSERT INTO workout_exercises
                       (id, workout_id, user_id, exercise_id, exercise_name, category, sets, reps, weight_kg,
                        duration_minutes, distance_km, notes, position, created_at, updated_at)
                       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"""
                ).use { statement ->
                    statement.setString(1, exercise.id ?: UUID.randomUUID().toString())
                    statement.setString(2, workoutId)
                    statement.setString(3, userId)
                    if (exercise.exerciseId == null) statement.setNull(4, java.sql.Types.INTEGER) else statement.setInt(4, exercise.exerciseId)
                    statement.setString(5, exercise.exerciseName.trim())
                    statement.setString(6, exercise.category.trim().lowercase())
                    statement.setInt(7, exercise.sets)
                    if (exercise.reps == null) statement.setNull(8, java.sql.Types.INTEGER) else statement.setInt(8, exercise.reps)
                    if (exercise.weightKg == null) statement.setNull(9, java.sql.Types.REAL) else statement.setDouble(9, exercise.weightKg)
                    if (exercise.durationMinutes == null) statement.setNull(10, java.sql.Types.INTEGER) else statement.setInt(10, exercise.durationMinutes)
                    if (exercise.distanceKm == null) statement.setNull(11, java.sql.Types.REAL) else statement.setDouble(11, exercise.distanceKm)
                    statement.setString(12, exercise.notes?.trim()?.takeIf(String::isNotEmpty))
                    statement.setInt(13, position)
                    statement.setString(14, now)
                    statement.setString(15, now)
                    statement.executeUpdate()
                }
            }
            true
        }
        getWorkout(userId, workoutId)
    }.getOrNull()

    suspend fun deleteWorkout(userId: String, workoutId: String): Boolean = database.connection { connection ->
        connection.prepareStatement("DELETE FROM workout_logs WHERE id = ? AND user_id = ?").use { statement ->
            statement.setString(1, workoutId)
            statement.setString(2, userId)
            statement.executeUpdate() == 1
        }
    }

    suspend fun workoutStats(userId: String): WorkoutStatsDto = database.connection { connection ->
        val since = Instant.now().minus(30, ChronoUnit.DAYS).toString()
        connection.prepareStatement(
            """SELECT COUNT(*) AS count, COALESCE(SUM(duration_minutes), 0) AS duration
               FROM workout_logs WHERE user_id = ? AND performed_at >= ?"""
        ).use { statement ->
            statement.setString(1, userId)
            statement.setString(2, since)
            statement.executeQuery().use { result ->
                result.next()
                WorkoutStatsDto(
                    workoutsLast30Days = result.getInt("count"),
                    minutesLast30Days = result.getInt("duration"),
                    totalVolumeKg = totalVolume(connection, userId)
                )
            }
        }
    }

    private fun totalVolume(connection: Connection, userId: String): Double = connection.prepareStatement(
        """SELECT COALESCE(SUM(sets * COALESCE(reps, 0) * COALESCE(weight_kg, 0)), 0)
           FROM workout_exercises WHERE user_id = ?"""
    ).use { statement ->
        statement.setString(1, userId)
        statement.executeQuery().use { result -> result.next(); result.getDouble(1) }
    }

    private fun ResultSet.toWorkout(connection: Connection): WorkoutLogDto {
        val workoutId = getString("id")
        val exercises = connection.prepareStatement(
            """SELECT id, exercise_id, exercise_name, category, sets, reps, weight_kg, duration_minutes,
                      distance_km, notes, position FROM workout_exercises WHERE workout_id = ? ORDER BY position"""
        ).use { statement ->
            statement.setString(1, workoutId)
            statement.executeQuery().use { result -> buildList { while (result.next()) add(result.toWorkoutExercise()) } }
        }
        return WorkoutLogDto(
            id = workoutId,
            name = getString("name"),
            performedAt = getString("performed_at"),
            durationMinutes = getInt("duration_minutes"),
            notes = getString("notes"),
            exercises = exercises,
            totalVolumeKg = exercises.sumOf { it.sets * (it.reps ?: 0) * (it.weightKg ?: 0.0) }
        )
    }

    private fun ResultSet.toWorkoutExercise() = WorkoutExerciseLogDto(
        id = getString("id"),
        exerciseId = getInt("exercise_id").takeUnless { wasNull() },
        exerciseName = getString("exercise_name"),
        category = getString("category"),
        sets = getInt("sets"),
        reps = getInt("reps").takeUnless { wasNull() },
        weightKg = getDouble("weight_kg").takeUnless { wasNull() },
        durationMinutes = getInt("duration_minutes").takeUnless { wasNull() },
        distanceKm = getDouble("distance_km").takeUnless { wasNull() },
        notes = getString("notes")
    )

    private fun ResultSet.toExercise() = ExerciseEntity(
        id = getInt("id"),
        exercise = getString("exercise"),
        exerciseType = getString("exercise_type"),
        exerciseVariation = getString("exercise_variation"),
        primaryFitnessFocus = getString("primary_fitness_focus"),
        secondaryFitnessFocus = getString("secondary_fitness_focus"),
        equipment = getString("equipment"),
        mechanics = getString("mechanics"),
        utility = getString("utility"),
        force = getString("force")
    )

    private fun java.sql.PreparedStatement.bind(values: List<Any>) {
        values.forEachIndexed { index, value -> setObject(index + 1, value) }
    }
}
