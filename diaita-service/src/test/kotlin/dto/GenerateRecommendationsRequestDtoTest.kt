package com.diaita.dto

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GenerateRecommendationsRequestDtoTest {

    private val json = Json

    @Test
    fun decodes_a_preferences_object() {
        val decoded = json.decodeFromString<GenerateRecommendationsRequestDto>(
            """{"preferences":{"workoutType":"strength"}}"""
        )

        assertEquals(mapOf("workoutType" to "strength"), decoded.preferences)
    }

    @Test
    fun decodes_an_empty_object_to_no_preferences() {
        assertTrue(json.decodeFromString<GenerateRecommendationsRequestDto>("{}").preferences.isEmpty())
    }

    @Test
    fun rejects_an_overlong_preference_value() {
        val request = GenerateRecommendationsRequestDto(mapOf("goal" to "x".repeat(201)))

        assertFalse(request.validate().isValid)
    }

    @Test
    fun rejects_a_blank_preference_key() {
        assertFalse(GenerateRecommendationsRequestDto(mapOf("  " to "value")).validate().isValid)
    }

    @Test
    fun accepts_preferences_within_the_limits() {
        assertTrue(GenerateRecommendationsRequestDto(mapOf("goal" to "strength")).validate().isValid)
    }
}
