package com.nutrify.lib.clients


import com.nutrify.dto.GeminiRequest
import com.nutrify.dto.GeminiResponse
import com.nutrify.lib.factories.PromptFactory
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class GeminiRestClient(val apiKey: String, val baseUrl: String = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent") : RestClient(apiKey, baseUrl) {

    override fun getFood(): String {
        return searchRecipes("hello")
    }

    suspend fun askQuestion(
        prompt: String,
        config: com.nutrify.dto.GenerationConfig? = null,
        systemInstruction: String? = null
    ): String? {
        val request = GeminiRequest.fromPrompt(prompt, config, systemInstruction)
        try {
            return client.post {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<GeminiResponse>().candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
        } catch (e: Exception) {
            println("Error calling Gemini API: ${e.message}")
            return null
        }
    }

}