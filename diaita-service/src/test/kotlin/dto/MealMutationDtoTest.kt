package com.diaita.dto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MealMutationDtoTest {

    @Test
    fun validate_returns_valid_for_single_meal_request() {
        val result = validRequest().validate()

        assertTrue(result.isValid)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun validate_returns_error_for_blank_user_id() {
        val result = validRequest(userId = " ").validate()

        assertFalse(result.isValid)
        assertEquals("userId", result.errors.single().path)
    }

    @Test
    fun validate_returns_error_for_invalid_user_id() {
        val result = validRequest(userId = "not-a-uuid").validate()

        assertFalse(result.isValid)
        assertEquals("userId", result.errors.single().path)
    }

    @Test
    fun validate_returns_error_for_empty_meals() {
        val result = validRequest(meals = emptyList()).validate()

        assertFalse(result.isValid)
        assertEquals("meals", result.errors.single().path)
    }

    @Test
    fun validate_returns_error_for_duplicate_meal_ids() {
        val mealId = "11111111-1111-1111-1111-111111111111"
        val request = validRequest(
            meals = listOf(
                validMeal(id = mealId),
                validMeal(id = mealId, mealType = "lunch")
            )
        )

        val result = request.validate()

        assertFalse(result.isValid)
        assertTrue(result.errors.single().message.contains("duplicate meal id"))
    }

    @Test
    fun validate_returns_error_for_invalid_meal_id() {
        val result = validRequest(
            meals = listOf(validMeal(id = "bad-id"))
        ).validate()

        assertFalse(result.isValid)
        assertEquals("meals[0].id", result.errors.single().path)
    }

    @Test
    fun validate_returns_error_for_invalid_meal_type() {
        val result = validRequest(
            meals = listOf(validMeal(mealType = "brunch"))
        ).validate()

        assertFalse(result.isValid)
        assertEquals("meals[0].mealType", result.errors.single().path)
    }

    @Test
    fun validate_returns_error_for_invalid_timestamp() {
        val result = validRequest(
            meals = listOf(validMeal(eatenAt = "03-09-2026"))
        ).validate()

        assertFalse(result.isValid)
        assertEquals("meals[0].eatenAt", result.errors.single().path)
    }

    @Test
    fun validate_returns_error_for_cross_day_batch_request() {
        val result = validRequest(
            meals = listOf(
                validMeal(eatenAt = "2026-03-09T08:00:00Z"),
                validMeal(
                    id = "11111111-1111-1111-1111-111111111111",
                    mealType = "dinner",
                    eatenAt = "2026-03-10T01:00:00Z"
                )
            )
        ).validate()

        assertFalse(result.isValid)
        assertEquals("meals", result.errors.single().path)
    }

    @Test
    fun validate_returns_error_for_notes_over_limit() {
        val result = validRequest(
            meals = listOf(validMeal(notes = "a".repeat(2001)))
        ).validate()

        assertFalse(result.isValid)
        assertEquals("meals[0].notes", result.errors.single().path)
    }

    @Test
    fun validate_returns_error_for_duplicate_item_ids() {
        val itemId = "22222222-2222-2222-2222-222222222222"
        val result = validRequest(
            meals = listOf(
                validMeal(
                    itemOps = MealItemOperationsRequestDto(
                        upsert = listOf(
                            validItem(id = itemId),
                            validItem(id = itemId, itemName = "banana")
                        )
                    )
                )
            )
        ).validate()

        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.message.contains("duplicate item id") })
    }

    @Test
    fun validate_returns_error_for_duplicate_delete_ids() {
        val itemId = "33333333-3333-3333-3333-333333333333"
        val result = validRequest(
            meals = listOf(
                validMeal(
                    itemOps = MealItemOperationsRequestDto(
                        upsert = listOf(validItem()),
                        deleteIds = listOf(itemId, itemId)
                    )
                )
            )
        ).validate()

        assertFalse(result.isValid)
        assertTrue(result.errors.single().message.contains("duplicate delete id"))
    }

    @Test
    fun validate_returns_error_for_invalid_delete_id() {
        val result = validRequest(
            meals = listOf(
                validMeal(
                    itemOps = MealItemOperationsRequestDto(
                        upsert = listOf(validItem()),
                        deleteIds = listOf("bad-delete-id")
                    )
                )
            )
        ).validate()

        assertFalse(result.isValid)
        assertEquals("meals[0].itemOps.deleteIds[0]", result.errors.single().path)
    }

    @Test
    fun validate_returns_error_for_invalid_item_type() {
        val result = validRequest(
            meals = listOf(validMeal(itemOps = MealItemOperationsRequestDto(upsert = listOf(validItem(itemType = "recipe")))))
        ).validate()

        assertFalse(result.isValid)
        assertEquals("meals[0].itemOps.upsert[0].itemType", result.errors.single().path)
    }

    @Test
    fun validate_returns_error_for_blank_item_name() {
        val result = validRequest(
            meals = listOf(validMeal(itemOps = MealItemOperationsRequestDto(upsert = listOf(validItem(itemName = " ")))))
        ).validate()

        assertFalse(result.isValid)
        assertEquals("meals[0].itemOps.upsert[0].itemName", result.errors.single().path)
    }

    @Test
    fun validate_returns_error_for_non_positive_quantity() {
        val result = validRequest(
            meals = listOf(validMeal(itemOps = MealItemOperationsRequestDto(upsert = listOf(validItem(quantity = 0.0)))))
        ).validate()

        assertFalse(result.isValid)
        assertEquals("meals[0].itemOps.upsert[0].quantity", result.errors.single().path)
    }

    @Test
    fun validate_returns_error_for_negative_calories() {
        val result = validRequest(
            meals = listOf(validMeal(itemOps = MealItemOperationsRequestDto(upsert = listOf(validItem(calories = -1.0)))))
        ).validate()

        assertFalse(result.isValid)
        assertEquals("meals[0].itemOps.upsert[0].calories", result.errors.single().path)
    }

    @Test
    fun validate_returns_error_for_negative_protein() {
        val result = validRequest(
            meals = listOf(validMeal(itemOps = MealItemOperationsRequestDto(upsert = listOf(validItem(proteinG = -1.0)))))
        ).validate()

        assertFalse(result.isValid)
        assertEquals("meals[0].itemOps.upsert[0].proteinG", result.errors.single().path)
    }

    @Test
    fun validate_returns_error_for_negative_carbs() {
        val result = validRequest(
            meals = listOf(validMeal(itemOps = MealItemOperationsRequestDto(upsert = listOf(validItem(carbsG = -1.0)))))
        ).validate()

        assertFalse(result.isValid)
        assertEquals("meals[0].itemOps.upsert[0].carbsG", result.errors.single().path)
    }

    @Test
    fun validate_returns_error_for_negative_fat() {
        val result = validRequest(
            meals = listOf(validMeal(itemOps = MealItemOperationsRequestDto(upsert = listOf(validItem(fatG = -1.0)))))
        ).validate()

        assertFalse(result.isValid)
        assertEquals("meals[0].itemOps.upsert[0].fatG", result.errors.single().path)
    }

    @Test
    fun validate_returns_error_for_negative_position() {
        val result = validRequest(
            meals = listOf(validMeal(itemOps = MealItemOperationsRequestDto(upsert = listOf(validItem(position = -1)))))
        ).validate()

        assertFalse(result.isValid)
        assertEquals("meals[0].itemOps.upsert[0].position", result.errors.single().path)
    }

    @Test
    fun validate_returns_error_for_duplicate_positions() {
        val result = validRequest(
            meals = listOf(
                validMeal(
                    itemOps = MealItemOperationsRequestDto(
                        upsert = listOf(
                            validItem(position = 0),
                            validItem(id = "44444444-4444-4444-4444-444444444444", itemName = "banana", position = 0)
                        )
                    )
                )
            )
        ).validate()

        assertFalse(result.isValid)
        assertEquals("meals[0].itemOps.upsert[1].position", result.errors.single().path)
    }

    @Test
    fun validate_returns_error_for_custom_item_with_item_id() {
        val result = validRequest(
            meals = listOf(
                validMeal(
                    itemOps = MealItemOperationsRequestDto(
                        upsert = listOf(validItem(itemType = "custom", itemId = "9003"))
                    )
                )
            )
        ).validate()

        assertFalse(result.isValid)
        assertEquals("meals[0].itemOps.upsert[0].itemId", result.errors.single().path)
    }

    private fun validRequest(
        userId: String = "123e4567-e89b-12d3-a456-426614174000",
        meals: List<UpsertMealRequestDto> = listOf(validMeal())
    ): UpsertMealsRequestDto {
        return UpsertMealsRequestDto(
            userId = userId,
            meals = meals
        )
    }

    private fun validMeal(
        id: String? = null,
        mealType: String = "breakfast",
        eatenAt: String = "2026-03-09T08:00:00Z",
        notes: String? = "morning meal",
        itemOps: MealItemOperationsRequestDto = MealItemOperationsRequestDto(upsert = listOf(validItem()))
    ): UpsertMealRequestDto {
        return UpsertMealRequestDto(
            id = id,
            mealType = mealType,
            eatenAt = eatenAt,
            notes = notes,
            itemOps = itemOps
        )
    }

    private fun validItem(
        id: String? = null,
        itemType: String = "ingredient",
        itemId: String? = "9003",
        itemName: String = "apple",
        quantity: Double = 1.0,
        calories: Double = 95.0,
        proteinG: Double = 0.5,
        carbsG: Double = 25.0,
        fatG: Double = 0.3,
        position: Int = 0
    ): UpsertMealItemRequestDto {
        return UpsertMealItemRequestDto(
            id = id,
            itemType = itemType,
            itemId = itemId,
            itemName = itemName,
            brandName = null,
            quantity = quantity,
            unit = "piece",
            calories = calories,
            proteinG = proteinG,
            carbsG = carbsG,
            fatG = fatG,
            position = position
        )
    }
}
