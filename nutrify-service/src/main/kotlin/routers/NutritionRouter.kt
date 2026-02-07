package com.nutrify.routers

import com.nutrify.controllers.NutritionController
import com.nutrify.dto.IngredientSearchFiltersDto
import com.nutrify.dto.ProductSearchFiltersDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureNutritionRoutes(nutritionController: NutritionController) {
    routing {
        post("/nutrition/search/ingredients") {
            val filters = try {
                call.receive<IngredientSearchFiltersDto>()
            } catch (e: Exception) {
                call.respondText("Invalid request payload", status = HttpStatusCode.BadRequest)
                return@post
            }

            if (filters.query.isBlank()) {
                call.respondText("Invalid request: 'query' field is required", status = HttpStatusCode.BadRequest)
                return@post
            }

            val result = nutritionController.searchIngredients(filters)
            if (result != null) {
                call.respond(HttpStatusCode.OK, result)
            } else {
                call.respondText("Failed to search ingredients", status = HttpStatusCode.InternalServerError)
            }
        }

        post("/nutrition/search/products") {
            val filters = try {
                call.receive<ProductSearchFiltersDto>()
            } catch (e: Exception) {
                call.respondText("Invalid request payload", status = HttpStatusCode.BadRequest)
                return@post
            }

            if (filters.query.isBlank()) {
                call.respondText("Invalid request: 'query' field is required", status = HttpStatusCode.BadRequest)
                return@post
            }

            val result = nutritionController.searchProducts(filters)
            if (result != null) {
                call.respond(HttpStatusCode.OK, result)
            } else {
                call.respondText("Failed to search products", status = HttpStatusCode.InternalServerError)
            }
        }

        get("/nutrition/ingredient/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respondText("Invalid ingredient id", status = HttpStatusCode.BadRequest)
                return@get
            }

            val food = nutritionController.getIngredientById(id)
            if (food != null) {
                call.respond(HttpStatusCode.OK, food)
            } else {
                call.respondText("Ingredient not found", status = HttpStatusCode.NotFound)
            }
        }

        get("/nutrition/product/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respondText("Invalid product id", status = HttpStatusCode.BadRequest)
                return@get
            }

            val food = nutritionController.getProductById(id)
            if (food != null) {
                call.respond(HttpStatusCode.OK, food)
            } else {
                call.respondText("Product not found", status = HttpStatusCode.NotFound)
            }
        }
    }
}
