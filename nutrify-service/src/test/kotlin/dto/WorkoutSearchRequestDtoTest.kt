package com.nutrify.dto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WorkoutSearchRequestDtoTest {

    @Test
    fun validate_rejects_negative_page() {
        val request = WorkoutSearchRequestDto(exerciseType = "Cardio", page = -1)

        val validation = request.validate()

        assertFalse(validation.isValid)
        assertEquals("Page number must be non-negative", validation.errorMessage)
    }

    @Test
    fun validate_rejects_page_size_above_100() {
        val request = WorkoutSearchRequestDto(exerciseType = "Cardio", pageSize = 101)

        val validation = request.validate()

        assertFalse(validation.isValid)
        assertEquals("Page size must be between 1 and 100", validation.errorMessage)
    }

    @Test
    fun validate_rejects_when_all_filters_missing_or_blank() {
        val request = WorkoutSearchRequestDto(
            exercise = "  ",
            exerciseType = null,
            exerciseVariation = "",
            primaryFitnessFocus = null
        )

        val validation = request.validate()

        assertFalse(validation.isValid)
        assertEquals("At least one search filter must be provided", validation.errorMessage)
    }

    @Test
    fun validate_accepts_when_at_least_one_filter_is_provided() {
        val request = WorkoutSearchRequestDto(primaryFitnessFocus = "endurance")

        val validation = request.validate()

        assertTrue(validation.isValid)
        assertEquals(null, validation.errorMessage)
    }
}
