package com.diaita.service

import com.diaita.dto.IngredientInformationDto
import com.diaita.dto.IngredientSearchFiltersDto
import com.diaita.dto.IngredientSearchResponseDto
import com.diaita.dto.IngredientSearchResultDto
import com.diaita.dto.ProductInformationDto
import com.diaita.dto.ProductSearchFiltersDto
import com.diaita.dto.ProductSearchResponseDto
import com.diaita.dto.SpoonacularNutrientDto
import com.diaita.dto.SpoonacularNutritionDto
import com.diaita.lib.clients.NutritionRestClient
import com.diaita.repo.NutritionRepo
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NutritionServiceTest {

    @Test
    fun searchIngredients_returns_mapped_foods_and_filters_out_missing_details() = runBlocking {
        val client = FakeNutritionClient().apply {
            ingredientSearchResponse = IngredientSearchResponseDto(
                results = listOf(
                    IngredientSearchResultDto(id = 1, name = "Chicken"),
                    IngredientSearchResultDto(id = 2, name = "Unknown")
                ),
                offset = 0,
                number = 2,
                totalResults = 2
            )
            ingredientDetails[1] = ingredient(id = 1, name = "Chicken Breast", protein = 31.0)
            ingredientDetails[2] = null
        }

        val service = NutritionService(mockk<NutritionRepo>(relaxed = true), client)

        val result = service.searchIngredients(IngredientSearchFiltersDto(query = "chicken", number = 2))

        assertNotNull(result)
        assertEquals(2, result.totalResults)
        assertEquals(1, result.foods.size)
        assertEquals("Chicken Breast", result.foods.first().name)
        assertEquals(31.0, result.foods.first().proteinGPerServingSize)
    }

    @Test
    fun searchProducts_returns_empty_when_search_is_empty() = runBlocking {
        val client = FakeNutritionClient().apply {
            productSearchResponse = ProductSearchResponseDto(
                products = emptyList(),
                offset = 5,
                number = 10,
                totalProducts = 0
            )
        }

        val service = NutritionService(mockk<NutritionRepo>(relaxed = true), client)

        val result = service.searchProducts(ProductSearchFiltersDto(query = "not-a-real-product"))

        assertNotNull(result)
        assertEquals(0, result.totalResults)
        assertEquals(0, result.foods.size)
        assertEquals(5, result.offset)
        assertEquals(10, result.number)
    }

    @Test
    fun searchIngredients_fetches_details_in_parallel() = runBlocking {
        val client = FakeNutritionClient().apply {
            ingredientSearchResponse = IngredientSearchResponseDto(
                results = listOf(
                    IngredientSearchResultDto(id = 11, name = "A"),
                    IngredientSearchResultDto(id = 12, name = "B"),
                    IngredientSearchResultDto(id = 13, name = "C")
                ),
                totalResults = 3,
                number = 3,
                offset = 0
            )
            ingredientDetails[11] = ingredient(11, "A", 10.0)
            ingredientDetails[12] = ingredient(12, "B", 20.0)
            ingredientDetails[13] = ingredient(13, "C", 30.0)
            ingredientDelayMs = 200
        }

        val service = NutritionService(mockk<NutritionRepo>(relaxed = true), client)

        val elapsed = measureTimeMillis {
            val result = service.searchIngredients(IngredientSearchFiltersDto(query = "abc", number = 3))
            assertNotNull(result)
            assertEquals(3, result.foods.size)
        }

        assertTrue(elapsed < 500, "Expected parallel fetches under 500ms, got ${elapsed}ms")
    }

    @Test
    fun getProductById_returns_null_when_client_has_no_data() = runBlocking {
        val client = FakeNutritionClient()
        val service = NutritionService(mockk<NutritionRepo>(relaxed = true), client)

        val result = service.getProductById(999)

        assertNull(result)
    }

    private fun ingredient(id: Int, name: String, protein: Double): IngredientInformationDto {
        return IngredientInformationDto(
            id = id,
            name = name,
            amount = 100.0,
            unit = "grams",
            nutrition = SpoonacularNutritionDto(
                nutrients = listOf(
                    SpoonacularNutrientDto(name = "Calories", amount = 165.0),
                    SpoonacularNutrientDto(name = "Protein", amount = protein),
                    SpoonacularNutrientDto(name = "Carbohydrates", amount = 0.0),
                    SpoonacularNutrientDto(name = "Fat", amount = 3.6)
                )
            )
        )
    }

    private class FakeNutritionClient : NutritionRestClient("test", "https://example.com") {
        var ingredientSearchResponse: IngredientSearchResponseDto? = null
        var productSearchResponse: ProductSearchResponseDto? = null
        val ingredientDetails: MutableMap<Int, IngredientInformationDto?> = mutableMapOf()
        val productDetails: MutableMap<Int, ProductInformationDto?> = mutableMapOf()
        var ingredientDelayMs: Long = 0

        override suspend fun searchIngredients(
            query: String,
            filters: IngredientSearchFiltersDto
        ): IngredientSearchResponseDto? {
            return ingredientSearchResponse
        }

        override suspend fun getIngredientInformation(
            id: Int,
            amount: Double?,
            unit: String?
        ): IngredientInformationDto? {
            if (ingredientDelayMs > 0) {
                delay(ingredientDelayMs)
            }
            return ingredientDetails[id]
        }

        override suspend fun searchProducts(
            query: String,
            filters: ProductSearchFiltersDto
        ): ProductSearchResponseDto? {
            return productSearchResponse
        }

        override suspend fun getProductInformation(id: Int): ProductInformationDto? {
            return productDetails[id]
        }
    }
}
