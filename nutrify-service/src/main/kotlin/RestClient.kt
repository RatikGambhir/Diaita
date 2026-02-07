package com.nutrify
import com.google.genai.Client
import com.nutrify.lib.clients.GeminiRestClient
import com.nutrify.lib.clients.NutritionRestClient
import com.nutrify.lib.clients.RestClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.server.application.*
import io.ktor.client.plugins.defaultRequest

import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.post
import io.ktor.client.request.setBody


//class GeminiClient(val client: Client) {
//
//}




fun Application.configureRestClient(): List<RestClient> {
        val geminiKey = environment.config.property("api.gemini").getString()
        val nutritionAPIKey = environment.config.property("foodapi.apiKey").getString()
        val nutritionAPIUrl = environment.config.property("foodapi.url").getString()
        val geminiClient = GeminiRestClient(geminiKey)
        val nutritionAPIClient = NutritionRestClient(nutritionAPIKey, nutritionAPIUrl)
        return listOf(geminiClient, nutritionAPIClient)
}