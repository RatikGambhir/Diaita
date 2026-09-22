package com.diaita.repo

import com.diaita.database.SQLiteDatabase
import com.diaita.dto.UpsertMealsRequestDto
import com.diaita.entity.MealItemRowEntity
import com.diaita.entity.MealRowEntity
import java.sql.Connection
import java.sql.ResultSet
import java.time.Instant
import java.util.UUID

class NutritionRepo(private val database: SQLiteDatabase) {
    suspend fun getMealsForUser(userId: String): List<MealRowEntity> = database.connection { connection ->
        connection.prepareStatement(
            """SELECT id, user_id, meal_type, eaten_at, notes, created_at, updated_at
               FROM meals WHERE user_id = ? ORDER BY eaten_at, created_at"""
        ).use { statement ->
            statement.setString(1, userId)
            statement.executeQuery().use { result -> buildList { while (result.next()) add(result.toMeal()) } }
        }
    }

    suspend fun getMealItemsForUser(userId: String): List<MealItemRowEntity> = database.connection { connection ->
        connection.prepareStatement(
            """SELECT id, meal_id, user_id, item_type, item_id, item_name, brand_name, quantity, unit,
                      calories, protein_g, carbs_g, fat_g, position, created_at, updated_at
               FROM meal_items WHERE user_id = ? ORDER BY meal_id, position, created_at"""
        ).use { statement ->
            statement.setString(1, userId)
            statement.executeQuery().use { result -> buildList { while (result.next()) add(result.toMealItem()) } }
        }
    }

    suspend fun upsertMeals(request: UpsertMealsRequestDto): Boolean = runCatching {
        database.transaction { connection ->
            request.meals.forEach { meal ->
                val mealId = meal.id ?: UUID.randomUUID().toString()
                requireOwnerOrMissing(connection, "meals", mealId, request.userId)
                val now = Instant.now().toString()
                connection.prepareStatement(
                    """INSERT INTO meals (id, user_id, meal_type, eaten_at, notes, created_at, updated_at)
                       VALUES (?, ?, ?, ?, ?, ?, ?)
                       ON CONFLICT(id) DO UPDATE SET meal_type = excluded.meal_type, eaten_at = excluded.eaten_at,
                           notes = excluded.notes, updated_at = excluded.updated_at"""
                ).use { statement ->
                    statement.setString(1, mealId)
                    statement.setString(2, request.userId)
                    statement.setString(3, meal.mealType)
                    statement.setString(4, meal.eatenAt)
                    statement.setString(5, meal.notes)
                    statement.setString(6, now)
                    statement.setString(7, now)
                    statement.executeUpdate()
                }

                meal.itemOps.deleteIds.forEach { itemId ->
                    connection.prepareStatement(
                        "DELETE FROM meal_items WHERE id = ? AND meal_id = ? AND user_id = ?"
                    ).use { statement ->
                        statement.setString(1, itemId)
                        statement.setString(2, mealId)
                        statement.setString(3, request.userId)
                        statement.executeUpdate()
                    }
                }

                meal.itemOps.upsert.forEach { item ->
                    val itemId = item.id ?: UUID.randomUUID().toString()
                    requireOwnerOrMissing(connection, "meal_items", itemId, request.userId)
                    connection.prepareStatement(
                        """INSERT INTO meal_items
                           (id, meal_id, user_id, item_type, item_id, item_name, brand_name, quantity, unit,
                            calories, protein_g, carbs_g, fat_g, position, created_at, updated_at)
                           VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                           ON CONFLICT(id) DO UPDATE SET item_type = excluded.item_type, item_id = excluded.item_id,
                               item_name = excluded.item_name, brand_name = excluded.brand_name,
                               quantity = excluded.quantity, unit = excluded.unit, calories = excluded.calories,
                               protein_g = excluded.protein_g, carbs_g = excluded.carbs_g, fat_g = excluded.fat_g,
                               position = excluded.position, updated_at = excluded.updated_at"""
                    ).use { statement ->
                        statement.setString(1, itemId)
                        statement.setString(2, mealId)
                        statement.setString(3, request.userId)
                        statement.setString(4, item.itemType)
                        statement.setString(5, item.itemId)
                        statement.setString(6, item.itemName)
                        statement.setString(7, item.brandName)
                        statement.setDouble(8, item.quantity)
                        statement.setString(9, item.unit)
                        statement.setDouble(10, item.calories)
                        statement.setDouble(11, item.proteinG)
                        statement.setDouble(12, item.carbsG)
                        statement.setDouble(13, item.fatG)
                        statement.setInt(14, item.position)
                        statement.setString(15, now)
                        statement.setString(16, now)
                        statement.executeUpdate()
                    }
                }
            }
        }
        true
    }.getOrDefault(false)

    private fun requireOwnerOrMissing(connection: Connection, table: String, id: String, userId: String) {
        connection.prepareStatement("SELECT user_id FROM $table WHERE id = ?").use { statement ->
            statement.setString(1, id)
            statement.executeQuery().use { result ->
                if (result.next()) require(result.getString(1) == userId) { "Resource does not belong to user" }
            }
        }
    }

    private fun ResultSet.toMeal() = MealRowEntity(
        id = getString("id"),
        userId = getString("user_id"),
        mealType = getString("meal_type"),
        eatenAt = getString("eaten_at"),
        notes = getString("notes"),
        createdAt = getString("created_at"),
        updatedAt = getString("updated_at")
    )

    private fun ResultSet.toMealItem() = MealItemRowEntity(
        id = getString("id"),
        mealId = getString("meal_id"),
        userId = getString("user_id"),
        itemType = getString("item_type"),
        itemId = getString("item_id"),
        itemName = getString("item_name"),
        brandName = getString("brand_name"),
        quantity = getDouble("quantity"),
        unit = getString("unit"),
        calories = getDouble("calories"),
        proteinG = getDouble("protein_g"),
        carbsG = getDouble("carbs_g"),
        fatG = getDouble("fat_g"),
        position = getInt("position"),
        createdAt = getString("created_at"),
        updatedAt = getString("updated_at")
    )
}
