package com.nutrify.lib.mappings

import com.nutrify.dto.ExerciseDto
import com.nutrify.dto.PaginationMetadataDto
import com.nutrify.dto.WorkoutSearchResponseDto
import com.nutrify.entity.ExerciseEntity
import com.nutrify.lib.factories.PaginatedResult

private fun ExerciseEntity.toDescription(): String? {
    val parts = listOfNotNull(
        equipment?.takeIf { it.isNotBlank() }?.let { "Equipment: $it" },
        mechanics?.takeIf { it.isNotBlank() }?.let { "Mechanics: $it" },
        utility?.takeIf { it.isNotBlank() }?.let { "Utility: $it" },
        force?.takeIf { it.isNotBlank() }?.let { "Force: $it" }
    )
    return parts.takeIf { it.isNotEmpty() }?.joinToString(" | ")
}

fun ExerciseEntity.toDto() = ExerciseDto(
    id = id,
    exercise = exercise,
    exerciseType = exerciseType,
    exerciseVariation = exerciseVariation,
    primaryFitnessFocus = primaryFitnessFocus,
    secondaryFitnessFocus = secondaryFitnessFocus,
    description = toDescription()
)

fun PaginatedResult<ExerciseEntity>.toResponseDto() = WorkoutSearchResponseDto(
    exercises = data.map { it.toDto() },
    pagination = PaginationMetadataDto(
        total = total,
        page = page,
        pageSize = pageSize,
        totalPages = if (pageSize > 0) (total + pageSize - 1) / pageSize else 0,
        hasMore = hasMore,
        hasPrevious = page > 0
    )
)
