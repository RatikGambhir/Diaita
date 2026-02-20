package com.diaita.service

import com.diaita.dto.FoodSearchResponseDto
import com.diaita.dto.IngredientSearchFiltersDto
import com.diaita.dto.MenuItemSearchFiltersDto
import com.diaita.dto.ProductSearchFiltersDto
import com.diaita.dto.FoodDto
import com.diaita.lib.clients.NutritionRestClient
import com.diaita.lib.mappings.toFoodDto
import com.diaita.repo.NutritionRepo
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class NutritionService(
    private val nutritionRepo: NutritionRepo,
    private val nutritionClient: NutritionRestClient
) {

    private suspend fun <T> buildSearchResponse(
        items: List<T>,
        totalResults: Int,
        offset: Int,
        number: Int,
        fetchFood: suspend (T) -> FoodDto?
    ): FoodSearchResponseDto {
        if (items.isEmpty()) {
            return FoodSearchResponseDto(
                foods = emptyList(),
                totalResults = totalResults,
                offset = offset,
                number = number
            )
        }

        val foods = coroutineScope {
            items.map { item ->
                async {
                    fetchFood(item)
                }
            }.awaitAll().filterNotNull()
        }

        return FoodSearchResponseDto(
            foods = foods,
            totalResults = totalResults,
            offset = offset,
            number = number
        )
    }

    suspend fun searchIngredients(filters: IngredientSearchFiltersDto): FoodSearchResponseDto? {
        return try {
            val searchResponse = nutritionClient.searchIngredients(filters.query, filters) ?: return null

            buildSearchResponse(
                items = searchResponse.results,
                totalResults = searchResponse.totalResults,
                offset = searchResponse.offset,
                number = searchResponse.number
            ) { result ->
                nutritionClient.getIngredientInformation(result.id)?.toFoodDto()
            }
        } catch (e: Exception) {
            println("Error searching ingredients in service: ${e.message}")
            null
        }
    }

    suspend fun searchProducts(filters: ProductSearchFiltersDto): FoodSearchResponseDto? {
        return try {
            val searchResponse = nutritionClient.searchProducts(filters.query, filters) ?: return null

            buildSearchResponse(
                items = searchResponse.products,
                totalResults = searchResponse.totalProducts,
                offset = searchResponse.offset,
                number = searchResponse.number
            ) { result ->
                nutritionClient.getProductInformation(result.id)?.toFoodDto()
            }
        } catch (e: Exception) {
            println("Error searching products in service: ${e.message}")
            null
        }
    }

    suspend fun searchMenuItems(filters: MenuItemSearchFiltersDto): FoodSearchResponseDto? {
        return try {
            val searchResponse = nutritionClient.searchMenuItems(filters.query, filters) ?: return null

            buildSearchResponse(
                items = searchResponse.menuItems,
                totalResults = searchResponse.totalMenuItems,
                offset = searchResponse.offset,
                number = searchResponse.number
            ) { result ->
                nutritionClient.getMenuItemInformation(result.id)?.toFoodDto()
            }
        } catch (e: Exception) {
            println("Error searching menu items in service: ${e.message}")
            null
        }
    }

    suspend fun getIngredientById(id: Int): FoodDto? {
        return nutritionClient.getIngredientInformation(id)?.toFoodDto()
    }

    suspend fun getProductById(id: Int): FoodDto? {
        return nutritionClient.getProductInformation(id)?.toFoodDto()
    }

    suspend fun getMenuItemById(id: Int): FoodDto? {
        return nutritionClient.getMenuItemInformation(id)?.toFoodDto()
    }
}
