package com.diaita.service

import com.diaita.dto.FoodSearchResponseDto
import com.diaita.dto.IngredientSearchFiltersDto
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

    suspend fun searchIngredients(filters: IngredientSearchFiltersDto): FoodSearchResponseDto? {
        return try {
            val searchResponse = nutritionClient.searchIngredients(filters.query, filters) ?: return null

            if (searchResponse.results.isEmpty()) {
                return FoodSearchResponseDto(
                    foods = emptyList(),
                    totalResults = searchResponse.totalResults,
                    offset = searchResponse.offset,
                    number = searchResponse.number
                )
            }

            val foods = coroutineScope {
                searchResponse.results.map { result ->
                    async {
                        nutritionClient.getIngredientInformation(result.id)?.toFoodDto()
                    }
                }.awaitAll().filterNotNull()
            }

            FoodSearchResponseDto(
                foods = foods,
                totalResults = searchResponse.totalResults,
                offset = searchResponse.offset,
                number = searchResponse.number
            )
        } catch (e: Exception) {
            println("Error searching ingredients in service: ${e.message}")
            null
        }
    }

    suspend fun searchProducts(filters: ProductSearchFiltersDto): FoodSearchResponseDto? {
        return try {
            val searchResponse = nutritionClient.searchProducts(filters.query, filters) ?: return null

            if (searchResponse.products.isEmpty()) {
                return FoodSearchResponseDto(
                    foods = emptyList(),
                    totalResults = searchResponse.totalProducts,
                    offset = searchResponse.offset,
                    number = searchResponse.number
                )
            }

            val foods = coroutineScope {
                searchResponse.products.map { result ->
                    async {
                        nutritionClient.getProductInformation(result.id)?.toFoodDto()
                    }
                }.awaitAll().filterNotNull()
            }

            FoodSearchResponseDto(
                foods = foods,
                totalResults = searchResponse.totalProducts,
                offset = searchResponse.offset,
                number = searchResponse.number
            )
        } catch (e: Exception) {
            println("Error searching products in service: ${e.message}")
            null
        }
    }

    suspend fun getIngredientById(id: Int): FoodDto? {
        return nutritionClient.getIngredientInformation(id)?.toFoodDto()
    }

    suspend fun getProductById(id: Int): FoodDto? {
        return nutritionClient.getProductInformation(id)?.toFoodDto()
    }
}
