package com.diaita.testdata

import com.diaita.dto.AdjustmentDto
import com.diaita.dto.AdjustmentRulesDto
import com.diaita.dto.CaloriesDto
import com.diaita.dto.CheckinsDto
import com.diaita.dto.DayByDayPlanDto
import com.diaita.dto.DayDto
import com.diaita.dto.ExerciseLibraryDto
import com.diaita.dto.FocusDto
import com.diaita.dto.FoodsDto
import com.diaita.dto.LoadProgressionDto
import com.diaita.dto.MacroRatioDto
import com.diaita.dto.MacrosDto
import com.diaita.dto.MealDto
import com.diaita.dto.MealStructureDto
import com.diaita.dto.NutritionRecommendationDto
import com.diaita.dto.PerformanceDto
import com.diaita.dto.PhaseDto
import com.diaita.dto.ProgressionRulesDto
import com.diaita.dto.RecommendationDto
import com.diaita.dto.RecommendationExerciseDto
import com.diaita.dto.RegisteredUserProfileDto
import com.diaita.dto.SplitDto
import com.diaita.dto.TrainingRecommendationDto
import com.diaita.dto.WeekDto
import com.diaita.dto.WorkoutExerciseDto

object RecommendationTestData {

    fun recommendation(): RecommendationDto = RecommendationDto(
        nutrition = NutritionRecommendationDto(
            calories = CaloriesDto(
                baseline = 2300,
                trainingDay = 2500,
                restDay = 2100
            ),
            macros = MacrosDto(
                trainingDay = MacroRatioDto(protein = 30, carbs = 45, fat = 25),
                restDay = MacroRatioDto(protein = 35, carbs = 35, fat = 30)
            ),
            mealStructure = MealStructureDto(
                meals = listOf(
                    MealDto(name = "Breakfast", time = "07:00", caloriePercent = 25),
                    MealDto(name = "Lunch", time = "12:30", caloriePercent = 35),
                    MealDto(name = "Dinner", time = "19:00", caloriePercent = 40)
                )
            ),
            foods = FoodsDto(
                proteins = listOf("Chicken breast", "Greek yogurt"),
                carbs = listOf("Rice", "Oats"),
                fats = listOf("Avocado", "Olive oil"),
                vegetables = listOf("Spinach", "Broccoli"),
                fruits = listOf("Banana", "Berries")
            ),
            checkins = CheckinsDto(
                metrics = listOf("Scale weight", "Workout performance"),
                frequency = "Weekly"
            ),
            adjustmentRules = AdjustmentRulesDto(
                plateauTrigger = "No progress for 2 weeks",
                adjustments = listOf(
                    AdjustmentDto(
                        condition = "Weight is stagnant",
                        action = "Reduce calories by 100"
                    )
                )
            )
        ),
        training = TrainingRecommendationDto(
            focus = FocusDto(
                primary = "Muscle gain",
                secondary = listOf("Strength")
            ),
            split = SplitDto(
                daysPerWeek = 4,
                groupings = listOf("Upper", "Lower", "Push", "Pull")
            ),
            exerciseLibrary = ExerciseLibraryDto(
                chest = listOf(RecommendationExerciseDto("Bench Press", "Barbell", "Compound")),
                back = listOf(RecommendationExerciseDto("Row", "Cable", "Compound")),
                shoulders = listOf(RecommendationExerciseDto("Shoulder Press", "Dumbbell", "Compound")),
                legs = listOf(RecommendationExerciseDto("Squat", "Barbell", "Compound")),
                arms = listOf(RecommendationExerciseDto("Curl", "Dumbbell", "Isolation")),
                core = listOf(RecommendationExerciseDto("Plank", "Bodyweight", "Isometric"))
            ),
            phases = listOf(
                PhaseDto(name = "Foundation", duration = "4 weeks", focus = "Technique")
            ),
            dayByDayPlan = DayByDayPlanDto(
                weeks = listOf(
                    WeekDto(
                        weekNumber = 1,
                        days = listOf(
                            DayDto(
                                day = "Monday",
                                focus = "Upper",
                                exercises = listOf(
                                    WorkoutExerciseDto(
                                        exercise = "Bench Press",
                                        sets = 4,
                                        reps = "8-10",
                                        restSeconds = 90,
                                        notes = "Leave 1-2 reps in reserve"
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            progressionRules = ProgressionRulesDto(
                load = LoadProgressionDto(
                    increase = "Add 2.5-5 lb when all reps are completed",
                    frequency = "Weekly"
                ),
                performance = PerformanceDto(
                    metrics = listOf("Load", "Reps")
                )
            )
        )
    )

    fun registeredProfile(userId: String = UserProfileTestData.fullRequest().userId): RegisteredUserProfileDto {
        val request = UserProfileTestData.fullRequest(userId)
        return RegisteredUserProfileDto(
            userId = request.userId,
            basicDemographics = request.basicDemographics,
            activityLifestyle = request.activityLifestyle,
            goals = request.goals,
            trainingBackground = request.trainingBackground,
            nutritionHistory = request.nutritionHistory
        )
    }
}
