package com.nutrify.repo

import com.nutrify.dto.WorkoutSearchRequestDto
import com.nutrify.entity.ExerciseEntity
import com.nutrify.lib.factories.PaginatedResult
import com.nutrify.lib.factories.Result
import com.nutrify.lib.factories.SupabaseManager

class WorkoutRepo(private val supabaseManager: SupabaseManager) {

    private val tableName = "exercises"

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
