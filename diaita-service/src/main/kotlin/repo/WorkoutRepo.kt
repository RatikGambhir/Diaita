package com.diaita.repo

import com.diaita.dto.WorkoutSearchRequestDto
import com.diaita.entity.ExerciseEntity
import com.diaita.entity.WorkoutExerciseRowEntity
import com.diaita.entity.WorkoutExerciseWriteRowEntity
import com.diaita.entity.WorkoutRowEntity
import com.diaita.entity.WorkoutWriteRowEntity
import com.diaita.lib.factories.PaginatedResult
import com.diaita.lib.factories.PostgresFactory
import com.diaita.lib.factories.Result
import com.diaita.lib.factories.SupabaseManager

class WorkoutRepo(private val supabaseManager: SupabaseManager) {

    private val tableName = PostgresFactory.EXERCISES_TABLE

    suspend fun searchExercises(request: WorkoutSearchRequestDto): Result<PaginatedResult<ExerciseEntity>> {
        val filters = buildFilters(request)

        if (filters.isEmpty()) {
            return Result(
                PaginatedResult(
                    data = emptyList(),
                    total = 0,
                    page = request.page,
                    pageSize = request.pageSize,
                    hasMore = false
                ),
                null
            )
        }


        return supabaseManager.selectWithFilters(
            table = tableName,
            filters = filters,
            page = request.page,
            pageSize = request.pageSize
        )
    }

    suspend fun insertWorkout(workout: WorkoutWriteRowEntity): WorkoutRowEntity? {
        val result = supabaseManager.upsertMany<WorkoutWriteRowEntity, WorkoutRowEntity>(
            table = PostgresFactory.WORKOUTS_TABLE,
            data = listOf(workout),
            onConflict = PostgresFactory.ID_COLUMN
        )

        return result.body?.firstOrNull()
    }

    suspend fun getWorkout(workoutId: String, userId: String): WorkoutRowEntity? {
        val result = supabaseManager.selectWhereAll<WorkoutRowEntity>(
            table = PostgresFactory.WORKOUTS_TABLE,
            filters = mapOf(
                PostgresFactory.ID_COLUMN to workoutId,
                PostgresFactory.USER_ID_COLUMN to userId
            )
        )

        return result.body?.firstOrNull()
    }

    suspend fun getWorkoutsForUser(userId: String): List<WorkoutRowEntity> {
        val result = supabaseManager.selectWhere<WorkoutRowEntity>(
            table = PostgresFactory.WORKOUTS_TABLE,
            column = PostgresFactory.USER_ID_COLUMN,
            value = userId
        )

        return result.body ?: emptyList()
    }

    suspend fun getExercisesForWorkouts(workoutIds: List<String>): List<WorkoutExerciseRowEntity> {
        val result = supabaseManager.selectWhereIn<WorkoutExerciseRowEntity>(
            table = PostgresFactory.WORKOUT_EXERCISES_TABLE,
            column = PostgresFactory.WORKOUT_ID_COLUMN,
            values = workoutIds
        )

        return result.body ?: emptyList()
    }

    suspend fun updateWorkout(
        workoutId: String,
        userId: String,
        workout: WorkoutWriteRowEntity
    ): WorkoutRowEntity? {
        val result = supabaseManager.updateWhereAll<WorkoutWriteRowEntity, WorkoutRowEntity>(
            table = PostgresFactory.WORKOUTS_TABLE,
            data = workout,
            filters = mapOf(
                PostgresFactory.ID_COLUMN to workoutId,
                PostgresFactory.USER_ID_COLUMN to userId
            )
        )

        return result.body?.firstOrNull()
    }

    suspend fun upsertWorkoutExercises(
        exercises: List<WorkoutExerciseWriteRowEntity>
    ): List<WorkoutExerciseRowEntity> {
        val result = supabaseManager.upsertMany<WorkoutExerciseWriteRowEntity, WorkoutExerciseRowEntity>(
            table = PostgresFactory.WORKOUT_EXERCISES_TABLE,
            data = exercises,
            onConflict = PostgresFactory.ID_COLUMN
        )

        return result.body ?: emptyList()
    }

    suspend fun deleteWorkoutExercises(workoutId: String, exerciseIds: List<String>): Boolean {
        val result = supabaseManager.deleteWhereIn(
            table = PostgresFactory.WORKOUT_EXERCISES_TABLE,
            column = PostgresFactory.ID_COLUMN,
            values = exerciseIds,
            scopeFilters = mapOf(PostgresFactory.WORKOUT_ID_COLUMN to workoutId)
        )

        return result.error == null
    }

    suspend fun deleteWorkout(workoutId: String, userId: String): Boolean {
        val result = supabaseManager.deleteWhereAll(
            table = PostgresFactory.WORKOUTS_TABLE,
            filters = mapOf(
                PostgresFactory.ID_COLUMN to workoutId,
                PostgresFactory.USER_ID_COLUMN to userId
            )
        )

        return result.error == null
    }

    internal fun buildFilters(request: WorkoutSearchRequestDto): Map<String, Pair<String, Any>> {
        val filters = mutableMapOf<String, Pair<String, Any>>()

        request.exercise?.trim()?.takeIf { it.isNotEmpty() }?.let {
            filters["exercise"] = "ilike" to it
        }

        request.exerciseType?.trim()?.takeIf { it.isNotEmpty() }?.let {
            filters["exercise_type"] = "eq" to it
        }

        request.exerciseVariation?.trim()?.takeIf { it.isNotEmpty() }?.let {
            filters["exercise_variation"] = "ilike" to it
        }

        request.primaryFitnessFocus?.trim()?.takeIf { it.isNotEmpty() }?.let {
            filters["primary_fitness_focus"] = "ilike" to it
        }

        return filters
    }
}
