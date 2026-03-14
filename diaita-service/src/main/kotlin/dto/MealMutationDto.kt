package com.diaita.dto

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

@Serializable
data class UpsertMealsRequestDto(
    val userId: String,
    val meals: List<UpsertMealRequestDto>
) {
    fun validate(): MealMutationValidationResult {
        val errors = mutableListOf<ValidationErrorResponseDto>()

        if (!userId.isUuid()) {
            errors += ValidationErrorResponseDto("userId", "must be a valid UUID")
        }

        if (meals.isEmpty()) {
            errors += ValidationErrorResponseDto("meals", "must contain at least one meal")
        }

        val duplicateMealIds = meals.mapNotNull { it.id }
            .groupingBy { it }
            .eachCount()
            .filterValues { it > 1 }
            .keys

        duplicateMealIds.forEach { id ->
            errors += ValidationErrorResponseDto("meals", "duplicate meal id '$id' in request")
        }

        meals.forEachIndexed { mealIndex, meal ->
            val mealPath = "meals[$mealIndex]"

            if (meal.id != null && !meal.id.isUuid()) {
                errors += ValidationErrorResponseDto("$mealPath.id", "must be a valid UUID")
            }

            if (meal.mealType.isBlank()) {
                errors += ValidationErrorResponseDto("$mealPath.mealType", "must not be blank")
            } else if (meal.normalizedMealType() !in VALID_MEAL_TYPES) {
                errors += ValidationErrorResponseDto(
                    "$mealPath.mealType",
                    "must be one of ${VALID_MEAL_TYPES.joinToString()}"
                )
            }

            if (meal.eatenAt.isBlank()) {
                errors += ValidationErrorResponseDto("$mealPath.eatenAt", "must not be blank")
            } else if (!meal.eatenAt.isIsoInstant()) {
                errors += ValidationErrorResponseDto("$mealPath.eatenAt", "must be a valid ISO-8601 timestamp")
            }

            if (meal.notes != null && meal.notes.trim().length > MAX_NOTES_LENGTH) {
                errors += ValidationErrorResponseDto(
                    "$mealPath.notes",
                    "must be at most $MAX_NOTES_LENGTH characters"
                )
            }

            val upsertItemIds = meal.itemOps.upsert.mapNotNull { it.id }
                .groupingBy { it }
                .eachCount()
                .filterValues { it > 1 }
                .keys

            upsertItemIds.forEach { id ->
                errors += ValidationErrorResponseDto("$mealPath.itemOps.upsert", "duplicate item id '$id' in request")
            }

            val duplicateDeleteIds = meal.itemOps.deleteIds
                .groupingBy { it }
                .eachCount()
                .filterValues { it > 1 }
                .keys

            duplicateDeleteIds.forEach { id ->
                errors += ValidationErrorResponseDto("$mealPath.itemOps.deleteIds", "duplicate delete id '$id' in request")
            }

            meal.itemOps.deleteIds.forEachIndexed { deleteIndex, deleteId ->
                if (!deleteId.isUuid()) {
                    errors += ValidationErrorResponseDto(
                        "$mealPath.itemOps.deleteIds[$deleteIndex]",
                        "must be a valid UUID"
                    )
                }
            }

            val positions = mutableSetOf<Int>()
            meal.itemOps.upsert.forEachIndexed { itemIndex, item ->
                val itemPath = "$mealPath.itemOps.upsert[$itemIndex]"

                if (item.id != null && !item.id.isUuid()) {
                    errors += ValidationErrorResponseDto("$itemPath.id", "must be a valid UUID")
                }

                if (item.itemType.isBlank()) {
                    errors += ValidationErrorResponseDto("$itemPath.itemType", "must not be blank")
                } else if (item.normalizedItemType() !in VALID_ITEM_TYPES) {
                    errors += ValidationErrorResponseDto(
                        "$itemPath.itemType",
                        "must be one of ${VALID_ITEM_TYPES.joinToString()}"
                    )
                }

                if (item.itemName.isBlank()) {
                    errors += ValidationErrorResponseDto("$itemPath.itemName", "must not be blank")
                }

                if (item.quantity <= 0.0) {
                    errors += ValidationErrorResponseDto("$itemPath.quantity", "must be greater than 0")
                }

                if (item.calories < 0.0) {
                    errors += ValidationErrorResponseDto("$itemPath.calories", "must be greater than or equal to 0")
                }

                if (item.proteinG < 0.0) {
                    errors += ValidationErrorResponseDto("$itemPath.proteinG", "must be greater than or equal to 0")
                }

                if (item.carbsG < 0.0) {
                    errors += ValidationErrorResponseDto("$itemPath.carbsG", "must be greater than or equal to 0")
                }

                if (item.fatG < 0.0) {
                    errors += ValidationErrorResponseDto("$itemPath.fatG", "must be greater than or equal to 0")
                }

                if (item.position < 0) {
                    errors += ValidationErrorResponseDto("$itemPath.position", "must be greater than or equal to 0")
                } else if (!positions.add(item.position)) {
                    errors += ValidationErrorResponseDto("$itemPath.position", "must be unique within a meal")
                }

                if (item.normalizedItemType() == ITEM_TYPE_CUSTOM && !item.itemId.isNullOrBlank()) {
                    errors += ValidationErrorResponseDto("$itemPath.itemId", "must be null for custom items")
                }
            }
        }

        val nutritionDates = meals.mapNotNull { it.eatenAt.nutritionDateOrNull() }.distinct()
        if (nutritionDates.size > 1) {
            errors += ValidationErrorResponseDto("meals", "must all belong to the same nutrition day")
        }

        return MealMutationValidationResult(errors)
    }

    companion object {
        private const val MAX_NOTES_LENGTH = 2000
        private const val ITEM_TYPE_CUSTOM = "custom"

        val VALID_MEAL_TYPES = setOf("breakfast", "lunch", "dinner", "snack")
        val VALID_ITEM_TYPES = setOf("ingredient", "product", "menu_item", ITEM_TYPE_CUSTOM)
    }
}

@Serializable
data class UpsertMealRequestDto(
    val id: String? = null,
    val mealType: String,
    val eatenAt: String,
    val notes: String? = null,
    val itemOps: MealItemOperationsRequestDto = MealItemOperationsRequestDto()
) {
    fun normalizedMealType(): String = mealType.trim().lowercase()
}

@Serializable
data class MealItemOperationsRequestDto(
    val upsert: List<UpsertMealItemRequestDto> = emptyList(),
    val deleteIds: List<String> = emptyList()
)

@Serializable
data class UpsertMealItemRequestDto(
    val id: String? = null,
    val itemType: String,
    val itemId: String? = null,
    val itemName: String,
    val brandName: String? = null,
    val quantity: Double,
    val unit: String? = null,
    val calories: Double,
    val proteinG: Double,
    val carbsG: Double,
    val fatG: Double,
    val position: Int
) {
    fun normalizedItemType(): String = itemType.trim().lowercase()
}

@Serializable
data class NutritionDaySummaryResponseDto(
    val date: String,
    val totalCal: Double,
    val totalProtein: Double,
    val totalFat: Double,
    val totalCarb: Double,
    val analytics: NutritionAnalyticsResponseDto,
    val breakfast: MealBucketResponseDto,
    val lunch: MealBucketResponseDto,
    val dinner: MealBucketResponseDto,
    val snacks: MealBucketResponseDto
)

@Serializable
data class NutritionAnalyticsResponseDto(
    val recCal: Double? = null,
    val recProtein: Double? = null,
    val recCarb: Double? = null,
    val recFat: Double? = null,
    val historical: HistoricalMacroAveragesResponseDto? = null
)

@Serializable
data class HistoricalMacroAveragesResponseDto(
    val avgCal: Double? = null,
    val avgProtein: Double? = null,
    val avgFat: Double? = null,
    val avgCarbs: Double? = null
)

@Serializable
data class MealBucketResponseDto(
    val items: List<MealBucketItemResponseDto> = emptyList(),
    val historical: HistoricalMacroAveragesResponseDto? = null
)

@Serializable
data class MealBucketItemResponseDto(
    val foodName: String,
    val cal: Double,
    val fat: Double,
    val protein: Double,
    val carb: Double,
    val servings: Double,
    val servingSize: String? = null
)

@Serializable
data class ValidationErrorResponseDto(
    val path: String,
    val message: String
)

@Serializable
data class ValidationErrorsResponseDto(
    val errors: List<ValidationErrorResponseDto>
)

data class MealMutationValidationResult(
    val errors: List<ValidationErrorResponseDto>
) {
    val isValid: Boolean = errors.isEmpty()
}

private fun String.isUuid(): Boolean =
    runCatching { UUID.fromString(trim()) }.isSuccess

private fun String.isIsoInstant(): Boolean =
    runCatching { Instant.parse(trim()) }.isSuccess

private fun String.nutritionDateOrNull(): String? =
    runCatching {
        Instant.parse(trim())
            .atOffset(ZoneOffset.UTC)
            .toLocalDate()
            .toString()
    }.getOrNull()
