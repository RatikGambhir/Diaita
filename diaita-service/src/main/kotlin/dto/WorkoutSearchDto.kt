package com.diaita.dto

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutSearchRequestDto(
    val exercise: String? = null,
    val exerciseType: String? = null,
    val exerciseVariation: String? = null,
    val primaryFitnessFocus: String? = null,
    val page: Int = 0,
    val pageSize: Int = 20
) {
    fun validate(): ValidationResultDto {
        if (page < 0) {
            return ValidationResultDto(false, "Page number must be non-negative")
        }
        if (pageSize !in 1..100) {
            return ValidationResultDto(false, "Page size must be between 1 and 100")
        }

        val hasAnyFilter = !exercise.isNullOrBlank() ||
            !exerciseType.isNullOrBlank() ||
            !exerciseVariation.isNullOrBlank() ||
            !primaryFitnessFocus.isNullOrBlank()

        if (!hasAnyFilter) {
            return ValidationResultDto(false, "At least one search filter must be provided")
        }

        return ValidationResultDto(true, null)
    }
}

@Serializable
data class ValidationResultDto(
    val isValid: Boolean,
    val errorMessage: String? = null
)

@Serializable
data class WorkoutSearchResponseDto(
    val exercises: List<ExerciseDto>,
    val pagination: PaginationMetadataDto
)

@Serializable
data class ExerciseDto(
    val id: Int? = null,
    val exercise: String,
    val exerciseType: String? = null,
    val exerciseVariation: String? = null,
    val primaryFitnessFocus: String? = null,
    val secondaryFitnessFocus: String? = null,
    val description: String? = null
)

@Serializable
data class PaginationMetadataDto(
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val hasMore: Boolean,
    val hasPrevious: Boolean
)
