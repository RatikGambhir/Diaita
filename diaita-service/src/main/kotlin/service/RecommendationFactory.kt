package com.diaita.service

import com.diaita.dto.*
import kotlin.math.roundToInt

object RecommendationFactory {
    fun create(profile: RegisterUserProfileRequestDto): RecommendationDto {
        val activityMultiplier = when (profile.activityLevel.trim().lowercase().replace(' ', '_')) {
            "sedentary" -> 1.2
            "lightly_active", "light" -> 1.35
            "moderately_active", "moderate" -> 1.5
            "very_active" -> 1.7
            "extremely_active" -> 1.9
            else -> 1.5
        }
        val baseline = (profile.weight * 22.0 * activityMultiplier).roundToInt().coerceAtLeast(1_200)
        val trainingDays = (profile.daysPerWeek ?: 3).coerceIn(1, 6)
        val groupings = when (trainingDays) {
            1, 2 -> listOf("Full Body")
            3 -> listOf("Full Body A", "Full Body B", "Full Body C")
            4 -> listOf("Upper", "Lower", "Upper", "Lower")
            else -> listOf("Push", "Pull", "Legs")
        }
        val dayExercises = listOf(
            WorkoutExerciseDto("Barbell Back Squat", 3, "6-10", 120, "Leave 2 reps in reserve"),
            WorkoutExerciseDto("Bench Press", 3, "6-10", 120, null),
            WorkoutExerciseDto("Dumbbell Row", 3, "8-12", 90, null),
            WorkoutExerciseDto("Dynamic Hip Stretch", 2, "30 seconds", 30, "Controlled range of motion")
        )

        return RecommendationDto(
            nutrition = NutritionRecommendationDto(
                calories = CaloriesDto(baseline, baseline + 150, baseline - 100),
                macros = MacrosDto(
                    trainingDay = MacroRatioDto(30, 45, 25),
                    restDay = MacroRatioDto(35, 35, 30)
                ),
                mealStructure = MealStructureDto(
                    listOf(
                        MealDto("Breakfast", "08:00", 25),
                        MealDto("Lunch", "12:30", 30),
                        MealDto("Dinner", "18:30", 35),
                        MealDto("Snack", "Flexible", 10)
                    )
                ),
                foods = FoodsDto(
                    proteins = listOf("Chicken", "Fish", "Eggs", "Tofu", "Greek yogurt"),
                    carbs = listOf("Oats", "Rice", "Potatoes", "Whole-grain bread"),
                    fats = listOf("Olive oil", "Avocado", "Nuts", "Seeds"),
                    vegetables = listOf("Leafy greens", "Broccoli", "Peppers", "Carrots"),
                    fruits = listOf("Berries", "Bananas", "Apples", "Oranges")
                ),
                checkins = CheckinsDto(listOf("Body weight", "Energy", "Hunger", "Training performance"), "Weekly"),
                adjustmentRules = AdjustmentRulesDto(
                    plateauTrigger = "No progress for two consecutive weeks",
                    adjustments = listOf(
                        AdjustmentDto("Weight loss stalls", "Reduce daily intake by 100-150 calories"),
                        AdjustmentDto("Recovery declines", "Add a rest day and review sleep")
                    )
                )
            ),
            training = TrainingRecommendationDto(
                focus = FocusDto(profile.primaryGoal, listOf("Consistency", "Mobility")),
                split = SplitDto(trainingDays, groupings),
                exerciseLibrary = ExerciseLibraryDto(
                    chest = listOf(RecommendationExerciseDto("Bench Press", "Barbell", "Compound")),
                    back = listOf(RecommendationExerciseDto("Dumbbell Row", "Dumbbell", "Compound")),
                    shoulders = listOf(RecommendationExerciseDto("Overhead Press", "Barbell", "Compound")),
                    legs = listOf(RecommendationExerciseDto("Barbell Back Squat", "Barbell", "Compound")),
                    arms = listOf(RecommendationExerciseDto("Bicep Curl", "Dumbbell", "Isolation")),
                    core = listOf(RecommendationExerciseDto("Plank", "Bodyweight", "Isometric"))
                ),
                phases = listOf(
                    PhaseDto("Foundation", "4 weeks", "Technique and consistency"),
                    PhaseDto("Build", "4 weeks", "Progressive volume"),
                    PhaseDto("Consolidate", "2 weeks", "Performance and recovery")
                ),
                dayByDayPlan = DayByDayPlanDto(
                    weeks = listOf(
                        WeekDto(1, (1..trainingDays).map { day ->
                            DayDto("Day $day", groupings[(day - 1) % groupings.size], dayExercises)
                        })
                    )
                ),
                progressionRules = ProgressionRulesDto(
                    load = LoadProgressionDto("Add 1-2 reps, then 2-5% load", "When all sets reach the top of the rep range"),
                    performance = PerformanceDto(listOf("Completed reps", "Load", "Effort", "Recovery"))
                )
            )
        )
    }
}
