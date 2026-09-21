package com.diaita.dto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WorkoutSearchRequestDtoTest {

    @Test
    fun validate_rejects_negative_page() {
        val request = WorkoutSearchRequestDto(exerciseTypes = listOf("Cardio/Conditioning"), page = -1)

        val validation = request.validate()

        assertFalse(validation.isValid)
        assertEquals("Page number must be non-negative", validation.errorMessage)
    }

    @Test
    fun validate_rejects_page_size_above_100() {
        val request = WorkoutSearchRequestDto(exerciseTypes = listOf("Cardio/Conditioning"), pageSize = 101)

        val validation = request.validate()

        assertFalse(validation.isValid)
        assertEquals("Page size must be between 1 and 100", validation.errorMessage)
    }

    @Test
    fun validate_rejects_when_all_filters_missing_or_blank() {
        val request = WorkoutSearchRequestDto(
            query = "  ",
            exerciseTypes = emptyList(),
            exerciseVariation = "",
            primaryFitnessFocuses = null
        )

        val validation = request.validate()

        assertFalse(validation.isValid)
        assertEquals("At least one search filter must be provided", validation.errorMessage)
    }

    @Test
    fun validate_accepts_when_at_least_one_filter_is_provided() {
        val request = WorkoutSearchRequestDto(primaryFitnessFocuses = listOf("Back"))

        val validation = request.validate()

        assertTrue(validation.isValid)
        assertEquals(null, validation.errorMessage)
    }

    @Test
    fun validate_rejects_invalid_exercise_type() {
        val request = WorkoutSearchRequestDto(exerciseTypes = listOf("powerlifting"))

        val validation = request.validate()

        assertFalse(validation.isValid)
        assertTrue(validation.errorMessage?.contains("Invalid exercise type") == true)
    }

    @Test
    fun normalized_canonicalizes_exercise_types_and_focuses() {
        val request = WorkoutSearchRequestDto(
            exerciseTypes = listOf("Cardio Conditioning", "SPORT"),
            primaryFitnessFocuses = listOf(" Back ", "CHEST")
        )

        val normalized = request.normalized()

        assertEquals(listOf("cardio/conditioning", "sport"), normalized.exerciseTypes)
        assertEquals(listOf("back", "chest"), normalized.primaryFitnessFocuses)
    }
}
