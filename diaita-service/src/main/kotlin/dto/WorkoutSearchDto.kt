package com.diaita.dto

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutSearchRequestDto(
    val query: String? = null,
    val exerciseTypes: List<String>? = null,
    val exerciseVariation: String? = null,
    val primaryFitnessFocuses: List<String>? = null,
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

        val normalized = normalized()
        if (normalized.query != null && normalized.query.length > MAX_QUERY_LENGTH) {
            return ValidationResultDto(false, "Query must be at most $MAX_QUERY_LENGTH characters")
        }
        if (normalized.exerciseVariation != null && normalized.exerciseVariation.length > MAX_EXERCISE_VARIATION_LENGTH) {
            return ValidationResultDto(
                false,
                "Exercise variation must be at most $MAX_EXERCISE_VARIATION_LENGTH characters"
            )
        }
        val invalidExerciseType = normalized.rawExerciseTypes?.firstOrNull { canonicalizeExerciseType(it) == null }
        if (invalidExerciseType != null) {
            return ValidationResultDto(
                false,
                "Invalid exercise type '$invalidExerciseType'. Allowed values: ${CANONICAL_EXERCISE_TYPES.joinToString(", ")}"
            )
        }

        val hasAnyFilter = normalized.query != null ||
            normalized.exerciseTypes != null ||
            normalized.exerciseVariation != null ||
            normalized.primaryFitnessFocuses != null

        if (!hasAnyFilter) {
            return ValidationResultDto(false, "At least one search filter must be provided")
        }

        return ValidationResultDto(true, null)
    }

    fun normalized(): NormalizedWorkoutSearchRequestDto {
        val rawExerciseTypes = normalizeList(exerciseTypes)
        return NormalizedWorkoutSearchRequestDto(
            query = query?.trim()?.takeIf { it.isNotEmpty() },
            rawExerciseTypes = rawExerciseTypes,
            exerciseTypes = rawExerciseTypes
                ?.mapNotNull(::canonicalizeExerciseType)
                ?.distinct()
                ?.takeIf { it.isNotEmpty() },
            exerciseVariation = exerciseVariation?.trim()?.takeIf { it.isNotEmpty() },
            primaryFitnessFocuses = normalizeList(primaryFitnessFocuses)
                ?.map(String::lowercase)
                ?.distinct()
                ?.takeIf { it.isNotEmpty() },
            page = page.coerceAtLeast(0),
            pageSize = pageSize.coerceIn(1, 100)
        )
    }

    private fun normalizeList(values: List<String>?): List<String>? =
        values
            ?.mapNotNull { it.trim().takeIf(String::isNotEmpty) }
            ?.takeIf { it.isNotEmpty() }

    private fun canonicalizeExerciseType(value: String): String? {
        val normalized = value
            .trim()
            .lowercase()
            .replace("-", " ")
            .replace("_", " ")
            .replace(WHITESPACE_REGEX, " ")
        val mapped = EXERCISE_TYPE_ALIASES[normalized] ?: normalized
        return mapped.takeIf { it in CANONICAL_EXERCISE_TYPES }
    }

    companion object {
        private const val MAX_QUERY_LENGTH = 120
        private const val MAX_EXERCISE_VARIATION_LENGTH = 120
        private val CANONICAL_EXERCISE_TYPES = setOf(
            "sport",
            "cardio/conditioning",
            "compound",
            "isolation",
            "dynamic stretching"
        )
        private val EXERCISE_TYPE_ALIASES = mapOf(
            "cardio conditioning" to "cardio/conditioning"
        )
        private val WHITESPACE_REGEX = Regex("\\s+")
    }
}

data class NormalizedWorkoutSearchRequestDto(
    val query: String?,
    val rawExerciseTypes: List<String>?,
    val exerciseTypes: List<String>?,
    val exerciseVariation: String?,
    val primaryFitnessFocuses: List<String>?,
    val page: Int,
    val pageSize: Int
)

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
