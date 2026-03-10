package com.diaita.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecommendationDto(
    val nutrition: NutritionRecommendationDto,
    val training: TrainingRecommendationDto
)

@Serializable
data class NutritionRecommendationDto(
    val calories: CaloriesDto,
    val macros: MacrosDto,
    @SerialName("meal_structure")
    val mealStructure: MealStructureDto,
    val foods: FoodsDto,
    val checkins: CheckinsDto,
    @SerialName("adjustment_rules")
    val adjustmentRules: AdjustmentRulesDto
)

@Serializable
data class CaloriesDto(
    val baseline: Int,
    @SerialName("training_day")
    val trainingDay: Int,
    @SerialName("rest_day")
    val restDay: Int
)

@Serializable
data class MacrosDto(
    @SerialName("training_day")
    val trainingDay: MacroRatioDto,
    @SerialName("rest_day")
    val restDay: MacroRatioDto
)

@Serializable
data class MacroRatioDto(
    val protein: Int,
    val carbs: Int,
    val fat: Int
)

@Serializable
data class MealStructureDto(
    val meals: List<MealDto>
)

@Serializable
data class MealDto(
    val name: String,
    val time: String,
    @SerialName("calorie_percent")
    val caloriePercent: Int
)

@Serializable
data class FoodsDto(
    val proteins: List<String>,
    val carbs: List<String>,
    val fats: List<String>,
    val vegetables: List<String>,
    val fruits: List<String>
)

@Serializable
data class CheckinsDto(
    val metrics: List<String>,
    val frequency: String
)

@Serializable
data class AdjustmentRulesDto(
    @SerialName("plateau_trigger")
    val plateauTrigger: String,
    val adjustments: List<AdjustmentDto>
)

@Serializable
data class AdjustmentDto(
    val condition: String,
    val action: String
)

@Serializable
data class TrainingRecommendationDto(
    val focus: FocusDto,
    val split: SplitDto,
    @SerialName("exercise_library")
    val exerciseLibrary: ExerciseLibraryDto,
    val phases: List<PhaseDto>,
    @SerialName("day_by_day_plan")
    val dayByDayPlan: DayByDayPlanDto,
    @SerialName("progression_rules")
    val progressionRules: ProgressionRulesDto
)

@Serializable
data class FocusDto(
    val primary: String,
    val secondary: List<String>
)

@Serializable
data class SplitDto(
    @SerialName("days_per_week")
    val daysPerWeek: Int,
    val groupings: List<String>
)

@Serializable
data class ExerciseLibraryDto(
    val chest: List<RecommendationExerciseDto>,
    val back: List<RecommendationExerciseDto>,
    val shoulders: List<RecommendationExerciseDto>,
    val legs: List<RecommendationExerciseDto>,
    val arms: List<RecommendationExerciseDto>,
    val core: List<RecommendationExerciseDto>
)

@Serializable
data class RecommendationExerciseDto(
    val name: String,
    val equipment: String,
    val type: String
)

@Serializable
data class PhaseDto(
    val name: String,
    val duration: String,
    val focus: String
)

@Serializable
data class DayByDayPlanDto(
    val weeks: List<WeekDto>
)

@Serializable
data class WeekDto(
    @SerialName("week_number")
    val weekNumber: Int,
    val days: List<DayDto>
)

@Serializable
data class DayDto(
    val day: String,
    val focus: String,
    val exercises: List<WorkoutExerciseDto>
)

@Serializable
data class WorkoutExerciseDto(
    val exercise: String,
    val sets: Int,
    val reps: String,
    @SerialName("rest_seconds")
    val restSeconds: Int,
    val notes: String? = null
)

@Serializable
data class ProgressionRulesDto(
    val load: LoadProgressionDto,
    val performance: PerformanceDto
)

@Serializable
data class LoadProgressionDto(
    val increase: String,
    val frequency: String
)

@Serializable
data class PerformanceDto(
    val metrics: List<String>
)
