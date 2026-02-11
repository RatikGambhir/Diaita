package com.diaita.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequestDto(
    val contents: List<GeminiContentDto>,
    val generationConfig: GenerationConfigDto? = null,
    val systemInstruction: SystemInstructionDto? = null
) {
    companion object {
        fun fromPrompt(prompt: String, config: GenerationConfigDto? = null, systemInstruction: String? = null): GeminiRequestDto {
            return GeminiRequestDto(
                contents = listOf(
                    GeminiContentDto(
                        parts = listOf(GeminiPartDto(text = prompt))
                    )
                ),
                generationConfig = config,
                systemInstruction = systemInstruction?.let { SystemInstructionDto(parts = listOf(GeminiPartDto(text = it))) }
            )
        }
    }
}

@Serializable
data class GenerationConfigDto(
    val temperature: Double? = null,
    val topK: Int? = null,
    val topP: Double? = null,
    val maxOutputTokens: Int? = null,
    val responseMimeType: String? = null,
    val responseSchema: ResponseSchemaDto? = null
)

@Serializable
data class ResponseSchemaDto(
    val type: String,
    val properties: Map<String, SchemaPropertyDto>? = null,
    val required: List<String>? = null
)

@Serializable
data class SchemaPropertyDto(
    val type: String,
    val description: String? = null
)

@Serializable
data class SystemInstructionDto(
    val parts: List<GeminiPartDto>
)

@Serializable
data class GeminiContentDto(
    val parts: List<GeminiPartDto>
)

@Serializable
data class GeminiPartDto(
    val text: String
)

@Serializable
data class GeminiResponseDto(
    val candidates: List<GeminiCandidateDto>
)

@Serializable
data class GeminiCandidateDto(
    val content: GeminiContentDto
)
