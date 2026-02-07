package com.nutrify.lib.clients


import com.nutrify.dto.GeminiRequestDto
import com.nutrify.dto.GeminiResponseDto
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.readUTF8Line
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

class GeminiRestClient(val apiKey: String, val baseUrl: String = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent") : RestClient(apiKey, baseUrl) {

    override fun getFood(): String {
        return searchRecipes("hello")
    }

    suspend fun askQuestion(
        prompt: String,
        config: com.nutrify.dto.GenerationConfigDto? = null,
        systemInstruction: String? = null
    ): String? {
        val request = GeminiRequestDto.fromPrompt(prompt, config, systemInstruction)
        try {
            return client.post {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<GeminiResponseDto>().candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
        } catch (e: Exception) {
            println("Error calling Gemini API: ${e.message}")
            return null
        }
    }

    suspend fun askQuestionStream(
        prompt: String,
        systemInstruction: String? = null,
        config: com.nutrify.dto.GenerationConfigDto? = null,
       // onChunk: suspend (String) -> Unit
    ): String? {
        val request = GeminiRequestDto.fromPrompt(prompt, config, systemInstruction)
        val streamUrl = buildStreamUrl()
        val fullResponse = StringBuilder()

        try {
            client.preparePost(streamUrl) {
                contentType(ContentType.Application.Json)
                headers {
                    append("Accept", "text/event-stream")
                }
                setBody(request)
            }.execute { response ->
                val channel = response.bodyAsChannel()

                while (!channel.isClosedForRead) {
                    val line = channel.readUTF8Line() ?: break
                    if (!line.startsWith("data:")) {
                        continue
                    }

                    val payload = line.removePrefix("data:").trim()
                    if (payload.isBlank() || payload == "[DONE]") {
                        continue
                    }

                    val chunkText = extractTextFromSsePayload(payload)
                    if (chunkText.isNotBlank()) {
                        fullResponse.append(chunkText)
                       // onChunk(chunkText)
                    }
                }
            }

            return fullResponse.toString()
        } catch (e: Exception) {
            println("Error calling Gemini Streaming API: ${e.message}")
            return null
        }
    }

    private fun buildStreamUrl(): String {
        val withoutQuery = baseUrl.substringBefore("?")
        val streamBase = withoutQuery.replace(":generateContent", ":streamGenerateContent")
        return "$streamBase?alt=sse"
    }

    private fun extractTextFromSsePayload(payload: String): String {
        val element = Json.parseToJsonElement(payload)
        if (element !is JsonArray) {
            return extractTextFromChunkObject(element as? JsonObject ?: return "")
        }

        return element.joinToString(separator = "") { item ->
            extractTextFromChunkObject(item as? JsonObject)
        }
    }

    private fun extractTextFromChunkObject(chunk: JsonObject?): String {
        if (chunk == null) {
            return ""
        }

        val candidates = chunk["candidates"] as? JsonArray ?: return ""
        return candidates.joinToString(separator = "") { candidateElement ->
            val candidate = candidateElement as? JsonObject ?: return@joinToString ""
            val content = candidate["content"] as? JsonObject ?: return@joinToString ""
            val parts = content["parts"] as? JsonArray ?: return@joinToString ""

            parts.joinToString(separator = "") { partElement ->
                val part = partElement as? JsonObject ?: return@joinToString ""
                part["text"]?.jsonPrimitive?.contentOrNull ?: ""
            }
        }
    }
}
