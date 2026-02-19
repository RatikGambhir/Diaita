package com.diaita.routers

import com.diaita.controllers.NutritionController
import com.diaita.dto.IngredientInformationDto
import com.diaita.dto.IngredientSearchFiltersDto
import com.diaita.dto.IngredientSearchResponseDto
import com.diaita.dto.IngredientSearchResultDto
import com.diaita.dto.MenuItemInformationDto
import com.diaita.dto.MenuItemSearchFiltersDto
import com.diaita.dto.MenuItemSearchResponseDto
import com.diaita.dto.MenuItemSearchResultDto
import com.diaita.dto.ProductInformationDto
import com.diaita.dto.ProductSearchFiltersDto
import com.diaita.dto.ProductSearchResponseDto
import com.diaita.dto.SpoonacularNutrientDto
import com.diaita.dto.SpoonacularNutritionDto
import com.diaita.lib.clients.NutritionRestClient
import com.diaita.repo.NutritionRepo
import com.diaita.service.NutritionService
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
import io.ktor.serialization.kotlinx.json.json
import io.mockk.mockk
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NutritionRouterTest {

    private val json = Json

    private fun Application.testModule(controller: NutritionController) {
        install(ContentNegotiation) {
            json()
        }
        configureNutritionRoutes(controller)
    }

    @Test
    fun ingredient_search_returns_200_with_mapped_foods() = testApplication {
        val fakeClient = FakeNutritionClient().apply {
            ingredientSearchResponse = IngredientSearchResponseDto(
                results = listOf(IngredientSearchResultDto(id = 1, name = "Chicken")),
                totalResults = 1,
                offset = 0,
                number = 1
            )
            ingredientDetails[1] = IngredientInformationDto(
                id = 1,
                name = "Chicken Breast",
                amount = 100.0,
                unit = "grams",
                nutrition = SpoonacularNutritionDto(
                    nutrients = listOf(
                        SpoonacularNutrientDto(name = "Calories", amount = 165.0),
                        SpoonacularNutrientDto(name = "Protein", amount = 31.0)
                    )
                )
            )
        }

        val controller = NutritionController(NutritionService(mockk<NutritionRepo>(relaxed = true), fakeClient))

        application { testModule(controller) }

        val response = client.post("/nutrition/search/ingredients") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(IngredientSearchFiltersDto(query = "chicken", number = 1)))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Chicken Breast"))
    }

    @Test
    fun ingredient_search_rejects_blank_query() = testApplication {
        val controller = NutritionController(NutritionService(mockk<NutritionRepo>(relaxed = true), FakeNutritionClient()))
        application { testModule(controller) }

        val response = client.post("/nutrition/search/ingredients") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(IngredientSearchFiltersDto(query = "   ")))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("query"))
    }

    @Test
    fun product_search_rejects_malformed_payload() = testApplication {
        val controller = NutritionController(NutritionService(mockk<NutritionRepo>(relaxed = true), FakeNutritionClient()))
        application { testModule(controller) }

        val response = client.post("/nutrition/search/products") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""{"invalid":"shape"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid request payload"))
    }

    @Test
    fun get_ingredient_rejects_non_numeric_id() = testApplication {
        val controller = NutritionController(NutritionService(mockk<NutritionRepo>(relaxed = true), FakeNutritionClient()))
        application { testModule(controller) }

        val response = client.get("/nutrition/ingredient/abc")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun get_product_returns_not_found_when_no_product_exists() = testApplication {
        val controller = NutritionController(NutritionService(mockk<NutritionRepo>(relaxed = true), FakeNutritionClient()))
        application { testModule(controller) }

        val response = client.get("/nutrition/product/999")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun menu_item_search_returns_200_with_mapped_foods() = testApplication {
        val fakeClient = FakeNutritionClient().apply {
            menuItemSearchResponse = MenuItemSearchResponseDto(
                menuItems = listOf(MenuItemSearchResultDto(id = 11, title = "Bacon King Burger")),
                totalMenuItems = 1,
                offset = 0,
                number = 1
            )
            menuItemDetails[11] = MenuItemInformationDto(
                id = 11,
                title = "Bacon King Burger",
                restaurantChain = "Burger King",
                nutrition = SpoonacularNutritionDto(
                    nutrients = listOf(
                        SpoonacularNutrientDto(name = "Calories", amount = 1040.0),
                        SpoonacularNutrientDto(name = "Protein", amount = 57.0)
                    )
                )
            )
        }

        val controller = NutritionController(NutritionService(mockk<NutritionRepo>(relaxed = true), fakeClient))

        application { testModule(controller) }

        val response = client.post("/nutrition/search/menuItems") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(MenuItemSearchFiltersDto(query = "burger", number = 1)))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Bacon King Burger"))
    }

    @Test
    fun menu_item_search_rejects_blank_query() = testApplication {
        val controller = NutritionController(NutritionService(mockk<NutritionRepo>(relaxed = true), FakeNutritionClient()))
        application { testModule(controller) }

        val response = client.post("/nutrition/search/menuItems") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(MenuItemSearchFiltersDto(query = "   ")))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("query"))
    }

    @Test
    fun get_menu_item_returns_not_found_when_no_item_exists() = testApplication {
        val controller = NutritionController(NutritionService(mockk<NutritionRepo>(relaxed = true), FakeNutritionClient()))
        application { testModule(controller) }

        val response = client.get("/nutrition/menuItem/999")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun get_menu_item_rejects_non_numeric_id() = testApplication {
        val controller = NutritionController(NutritionService(mockk<NutritionRepo>(relaxed = true), FakeNutritionClient()))
        application { testModule(controller) }

        val response = client.get("/nutrition/menuItem/abc")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    private class FakeNutritionClient : NutritionRestClient("test", "https://example.com") {
        var ingredientSearchResponse: IngredientSearchResponseDto? = null
        var productSearchResponse: ProductSearchResponseDto? = null
        var menuItemSearchResponse: MenuItemSearchResponseDto? = null
        val ingredientDetails: MutableMap<Int, IngredientInformationDto?> = mutableMapOf()
        val productDetails: MutableMap<Int, ProductInformationDto?> = mutableMapOf()
        val menuItemDetails: MutableMap<Int, MenuItemInformationDto?> = mutableMapOf()

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

        override suspend fun searchMenuItems(
            query: String,
            filters: MenuItemSearchFiltersDto
        ): MenuItemSearchResponseDto? {
            return menuItemSearchResponse
        }

        override suspend fun getMenuItemInformation(id: Int): MenuItemInformationDto? {
            return menuItemDetails[id]
        }
    }
}
