package com.nutrify.repo

import com.nutrify.dto.WorkoutSearchRequestDto
import com.nutrify.lib.factories.SupabaseManager
import io.github.jan.supabase.SupabaseClient
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

class WorkoutRepoTest {

    private val supabaseClient = mockk<SupabaseClient>(relaxed = true)
    private val repo = WorkoutRepo(SupabaseManager(supabaseClient))

    @Test
    fun buildFilters_creates_expected_exact_and_keyword_filters() {
        val request = WorkoutSearchRequestDto(
            exercise = "Rowing",
            exerciseType = "Cardio/Conditioning",
            exerciseVariation = "steady",
            primaryFitnessFocus = "endurance"
        )

        val filters = repo.buildFilters(request)

        assertEquals("eq" to "Rowing", filters["exercise"])
        assertEquals("eq" to "Cardio/Conditioning", filters["exercise_type"])
        assertEquals("ilike" to "steady", filters["exercise_variation"])
        assertEquals("ilike" to "endurance", filters["primary_fitness_focus"])
        assertEquals(4, filters.size)
    }

    @Test
    fun buildFilters_ignores_blank_values() {
        val request = WorkoutSearchRequestDto(
            exercise = "  ",
            exerciseType = "",
            exerciseVariation = "\n",
            primaryFitnessFocus = "   "
        )

        val filters = repo.buildFilters(request)

        assertEquals(0, filters.size)
    }
}
