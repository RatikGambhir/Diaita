package com.diaita.routers

import com.diaita.controllers.NutritionController
import com.diaita.dto.IngredientSearchFiltersDto
import com.diaita.dto.MenuItemSearchFiltersDto
import com.diaita.dto.ProductSearchFiltersDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

private suspend inline fun <reified T : Any> ApplicationCall.handleSearchRequest(
    searchFailureMessage: String,
    querySelector: (T) -> String,
    search: suspend (T) -> Any?
) {
    val filters = try {
        receive<T>()
    } catch (e: Exception) {
        respondText("Invalid request payload", status = HttpStatusCode.BadRequest)
        return
    }

    if (querySelector(filters).isBlank()) {
        respondText("Invalid request: 'query' field is required", status = HttpStatusCode.BadRequest)
        return
    }

    val result = search(filters)
    if (result != null) {
        respond(HttpStatusCode.OK, result)
    } else {
        respondText(searchFailureMessage, status = HttpStatusCode.InternalServerError)
    }
}

private suspend fun ApplicationCall.handleGetByIdRequest(
    invalidIdMessage: String,
    notFoundMessage: String,
    getById: suspend (Int) -> Any?
) {
    val id = parameters["id"]?.toIntOrNull()
    if (id == null) {
        respondText(invalidIdMessage, status = HttpStatusCode.BadRequest)
        return
    }

    val food = getById(id)
    if (food != null) {
        respond(HttpStatusCode.OK, food)
    } else {
        respondText(notFoundMessage, status = HttpStatusCode.NotFound)
    }
}

fun Application.configureNutritionRoutes(nutritionController: NutritionController) {
    routing {
        post("/nutrition/search/ingredients") {
            call.handleSearchRequest<IngredientSearchFiltersDto>(
                searchFailureMessage = "Failed to search ingredients",
                querySelector = { it.query },
                search = { nutritionController.searchIngredients(it) }
            )
        }

        post("/nutrition/search/products") {
            call.handleSearchRequest<ProductSearchFiltersDto>(
                searchFailureMessage = "Failed to search products",
                querySelector = { it.query },
                search = { nutritionController.searchProducts(it) }
            )
        }

        post("/nutrition/search/menuItems") {
            call.handleSearchRequest<MenuItemSearchFiltersDto>(
                searchFailureMessage = "Failed to search menu items",
                querySelector = { it.query },
                search = { nutritionController.searchMenuItems(it) }
            )
        }

        get("/nutrition/ingredient/{id}") {
            call.handleGetByIdRequest(
                invalidIdMessage = "Invalid ingredient id",
                notFoundMessage = "Ingredient not found",
                getById = { nutritionController.getIngredientById(it) }
            )
        }

        get("/nutrition/product/{id}") {
            call.handleGetByIdRequest(
                invalidIdMessage = "Invalid product id",
                notFoundMessage = "Product not found",
                getById = { nutritionController.getProductById(it) }
            )
        }

        get("/nutrition/menuItem/{id}") {
            call.handleGetByIdRequest(
                invalidIdMessage = "Invalid menu item id",
                notFoundMessage = "Menu item not found",
                getById = { nutritionController.getMenuItemById(it) }
            )
        }
    }
}
