package com.diaita.lib.mappings

import com.diaita.dto.MealBucketItemResponseDto
import com.diaita.dto.MealBucketResponseDto
import com.diaita.dto.MealItemOperationsRequestDto
import com.diaita.dto.UpsertMealItemRequestDto
import com.diaita.dto.UpsertMealRequestDto
import com.diaita.dto.UpsertMealsRequestDto

fun UpsertMealsRequestDto.toNormalizedRequest(): UpsertMealsRequestDto {
    return copy(
        userId = userId.trim(),
        meals = meals.map { it.toNormalizedMeal() }
    )
}

fun UpsertMealRequestDto.toNormalizedMeal(): UpsertMealRequestDto {
    return copy(
        id = id?.trim()?.ifEmpty { null },
        mealType = normalizedMealType(),
        eatenAt = eatenAt.trim(),
        notes = notes?.trim(),
        itemOps = itemOps.toNormalizedOperations()
    )
}

fun MealItemOperationsRequestDto.toNormalizedOperations(): MealItemOperationsRequestDto {
    return copy(
        upsert = upsert.map { it.toNormalizedItem() },
        deleteIds = deleteIds.map(String::trim)
    )
}

fun UpsertMealItemRequestDto.toNormalizedItem(): UpsertMealItemRequestDto {
    return copy(
        id = id?.trim()?.ifEmpty { null },
        itemType = normalizedItemType(),
        itemId = itemId?.trim()?.ifEmpty { null },
        itemName = itemName.trim(),
        brandName = brandName?.trim(),
        unit = unit?.trim()
    )
}

fun MealBucketResponseDto.sortedByFoodName(): MealBucketResponseDto {
    return copy(items = items.sortedBy(MealBucketItemResponseDto::foodName))
}
