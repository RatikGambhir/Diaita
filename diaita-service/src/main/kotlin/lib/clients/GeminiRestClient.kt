package com.diaita.lib.clients


import com.diaita.dto.GeminiRequestDto
import com.diaita.dto.GeminiResponseDto
import com.diaita.dto.ResponseSchemaDto
import com.diaita.dto.GenerationConfigDto
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.readUTF8Line
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

class GeminiRestClient(val apiKey: String, val baseUrl: String = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent") : RestClient(apiKey, baseUrl) {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }
    private val normalizedApiKey = apiKey.trim()



    suspend fun askQuestionStream(
        prompt: String,
        systemInstruction: String? = null,
        config: GenerationConfigDto? = null,
       // onChunk: suspend (String) -> Unit
    ): String? {
        val request = GeminiRequestDto.fromPrompt(prompt, config, systemInstruction)
        val streamUrl = buildStreamUrl()
        val fullResponse = StringBuilder()

        try {
            client.preparePost(streamUrl) {
                contentType(ContentType.Application.Json)
                headers {
                    append("x-goog-api-key", normalizedApiKey)
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

    suspend fun <T> askQuestionStructured(
        prompt: String,
        responseSchema: ResponseSchemaDto,
        serializer: DeserializationStrategy<T>,
        systemInstruction: String? = null,
        config: GenerationConfigDto? = null
    ): T? {
        val strictSystemInstruction = buildStrictStructuredInstruction(systemInstruction)
        val baseRequest = buildStructuredRequest(
            prompt = prompt,
            responseSchema = responseSchema,
            systemInstruction = strictSystemInstruction,
            config = config
        )

        try {
            val firstResponse = executeStructuredRequest(baseRequest) ?: return null
            val firstPayload = extractStructuredPayload(firstResponse) ?: return null

            return try {
                json.decodeFromString(serializer, firstPayload)
            } catch (e: SerializationException) {
                println("Structured JSON parse failed, retrying once with stricter instruction: ${e.message}")

                val retryRequest = buildStructuredRequest(
                    prompt = "$prompt\n\n$STRICT_JSON_RETRY_PROMPT_SUFFIX",
                    responseSchema = responseSchema,
                    systemInstruction = strictSystemInstruction,
                    config = config
                )
                val retryResponse = executeStructuredRequest(retryRequest) ?: return null
                val retryPayload = extractStructuredPayload(retryResponse) ?: return null
                json.decodeFromString(serializer, retryPayload)
            }
        } catch (e: Exception) {
            println("Error calling Gemini Structured API: ${e.message}")
            return null
        }
    }

    private suspend fun executeStructuredRequest(request: GeminiRequestDto): GeminiResponseDto? {
        val response = client.post {
            contentType(ContentType.Application.Json)
            headers {
                append("x-goog-api-key", normalizedApiKey)
            }
            setBody(request)
        }.body<GeminiResponseDto>()

        if (response.error != null) {
            println("Gemini API error (${response.error.code} ${response.error.status}): ${response.error.message}")
            return null
        }

        return response
    }

    private fun buildStructuredRequest(
        prompt: String,
        responseSchema: ResponseSchemaDto,
        systemInstruction: String?,
        config: GenerationConfigDto?
    ): GeminiRequestDto {
        return GeminiRequestDto.fromPrompt(
            prompt = prompt,
            config = config?.copy(
                responseMimeType = "application/json",
                responseSchema = responseSchema
            ) ?: GenerationConfigDto(
                temperature = 0.7,
                topP = 0.95,
                topK = 40,
                maxOutputTokens = 65536,
                responseMimeType = "application/json",
                responseSchema = responseSchema
            ),
            systemInstruction = systemInstruction
        )
    }

    private fun buildStrictStructuredInstruction(systemInstruction: String?): String {
        return listOfNotNull(systemInstruction?.trim()?.takeIf { it.isNotBlank() }, STRICT_JSON_SYSTEM_SUFFIX)
            .joinToString(separator = "\n\n")
    }

    private fun extractStructuredPayload(response: GeminiResponseDto): String? {
        val candidate = response.candidates.firstOrNull() ?: return null
        if (candidate.finishReason == "MAX_TOKENS") {
            println("Gemini response truncated: finishReason=MAX_TOKENS. Increase maxOutputTokens.")
            return null
        }
        return candidate.content?.parts?.firstOrNull()?.text
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

    private companion object {
        const val STRICT_JSON_SYSTEM_SUFFIX =
            "Return strictly valid RFC 8259 JSON only. Do not include markdown, comments, trailing commas, code fences, or extra text."
        const val STRICT_JSON_RETRY_PROMPT_SUFFIX =
            "Regenerate the response as strict RFC 8259 JSON. Ensure there are no trailing commas and no text outside JSON."
    }
}
