package com.diaita.service

import com.diaita.lib.clients.GeminiRestClient
import com.diaita.lib.builders.ResponseSchemaBuilder
import com.diaita.dto.ActivityLevelLifestyleDto
import com.diaita.dto.BasicDemographicsDto
import com.diaita.dto.GenerationConfigDto
import com.diaita.dto.GoalsPrioritiesDto
import com.diaita.dto.NutritionDietHistoryDto
import com.diaita.dto.RecommendationDto
import com.diaita.dto.RegisterUserProfileRequestDto
import com.diaita.dto.RegisteredUserProfileDto
import com.diaita.dto.TrainingBackgroundDto
import com.diaita.lib.factories.PromptFactory
import com.diaita.lib.mappings.*
import com.diaita.lib.prompt_extensions.toPromptVariables
import com.diaita.repo.RecommendationRepo
import com.diaita.repo.UserRepo


class UserService(
    private val userRepo: UserRepo,
    private val client: GeminiRestClient,
    private val recommendationRepo: RecommendationRepo
) {

    suspend fun registerUserProfile(request: RegisterUserProfileRequestDto): String? {
        val result = userRepo.upsertFullProfile(request)
        if (result != "Mutation Success") {
            return null
        }
        val recommendation = genRecommendations(request)
        if (recommendation == null) {
            return null
        }
        if (!saveUserRecommendations(request.userId, recommendation)) {
            return null
        }
        return "Mutation Success"
    }

    suspend fun registerUser(request: RegisterUserProfileRequestDto): String? = registerUserProfile(request)

    suspend fun genRecommendations(
        request: RegisterUserProfileRequestDto,
        config: GenerationConfigDto? = null,
        systemInstruction: String? = null
    ): RecommendationDto? = genRecommendations(
        promptVariables = request.toPromptVariables(),
        config = config,
        systemInstruction = systemInstruction
    )

    suspend fun genRecommendations(
        profile: RegisteredUserProfileDto,
        config: GenerationConfigDto? = null,
        systemInstruction: String? = null
    ): RecommendationDto? = genRecommendations(
        promptVariables = profile.toPromptVariables(),
        config = config,
        systemInstruction = systemInstruction
    )

    suspend fun generateAndSaveRecommendations(userId: String): RecommendationDto? {
        val profile = userRepo.getFullProfile(userId) ?: return null
        val recommendation = genRecommendations(profile) ?: return null
        return recommendation.takeIf { saveUserRecommendations(userId, it) }
    }

    suspend fun saveUserRecommendations(userId: String, recommendation: RecommendationDto): Boolean {
        return recommendationRepo.saveRecommendation(userId, recommendation)
    }

    suspend fun getRecommendations(userId: String): RecommendationDto? {
        return recommendationRepo.getRecommendationByUserId(userId)
    }

    suspend fun getBasicDemographics(userId: String): BasicDemographicsDto? {
        return userRepo.getBasicDemographics(userId)?.toDto()
    }

    suspend fun updateBasicDemographics(
        userId: String,
        dto: BasicDemographicsDto
    ): BasicDemographicsDto? {
        return userRepo.updateBasicDemographics(userId, dto.toEntity(userId))?.toDto()
    }

    suspend fun deleteBasicDemographics(userId: String): Boolean {
        return userRepo.deleteBasicDemographics(userId)
    }

    suspend fun getActivityLifestyle(userId: String): ActivityLevelLifestyleDto? {
        return userRepo.getActivityLifestyle(userId)?.toDto()
    }

    suspend fun updateActivityLifestyle(
        userId: String,
        dto: ActivityLevelLifestyleDto
    ): ActivityLevelLifestyleDto? {
        return userRepo.updateActivityLifestyle(userId, dto.toEntity(userId))?.toDto()
    }

    suspend fun deleteActivityLifestyle(userId: String): Boolean {
        return userRepo.deleteActivityLifestyle(userId)
    }

    suspend fun getGoalsPriorities(userId: String): GoalsPrioritiesDto? {
        return userRepo.getGoalsPriorities(userId)?.toDto()
    }

    suspend fun updateGoalsPriorities(userId: String, dto: GoalsPrioritiesDto): GoalsPrioritiesDto? {
        return userRepo.updateGoalsPriorities(userId, dto.toEntity(userId))?.toDto()
    }

    suspend fun deleteGoalsPriorities(userId: String): Boolean {
        return userRepo.deleteGoalsPriorities(userId)
    }

    suspend fun getTrainingBackground(userId: String): TrainingBackgroundDto? {
        return userRepo.getTrainingBackground(userId)?.toDto()
    }

    suspend fun updateTrainingBackground(
        userId: String,
        dto: TrainingBackgroundDto
    ): TrainingBackgroundDto? {
        return userRepo.updateTrainingBackground(userId, dto.toEntity(userId))?.toDto()
    }

    suspend fun deleteTrainingBackground(userId: String): Boolean {
        return userRepo.deleteTrainingBackground(userId)
    }

    suspend fun getNutritionHistory(userId: String): NutritionDietHistoryDto? {
        return userRepo.getNutritionHistory(userId)?.toDto()
    }

    suspend fun updateNutritionHistory(
        userId: String,
        dto: NutritionDietHistoryDto
    ): NutritionDietHistoryDto? {
        return userRepo.updateNutritionHistory(userId, dto.toEntity(userId))?.toDto()
    }

    suspend fun deleteNutritionHistory(userId: String): Boolean {
        return userRepo.deleteNutritionHistory(userId)
    }
    private suspend fun genRecommendations(
        promptVariables: Map<String, Any>,
        config: GenerationConfigDto?,
        systemInstruction: String?
    ): RecommendationDto? {
        val prompt = buildStructuredRecommendationPrompt(promptVariables)
        return client.askQuestionStructured(
            prompt = prompt,
            responseSchema = ResponseSchemaBuilder.buildRecommendationSchema(),
            serializer = RecommendationDto.serializer(),
            systemInstruction = systemInstruction ?: STRUCTURED_OUTPUT_SYSTEM_INSTRUCTION,
            config = config
        )
    }

    private fun buildStructuredRecommendationPrompt(promptVariables: Map<String, Any>): String {
        val prompt = PromptFactory.getPromptWithVariables("registerUserMetadata", promptVariables)
        return "$prompt\n\nReturn only valid JSON that matches the provided response schema."
    }

    private companion object {
        const val STRUCTURED_OUTPUT_SYSTEM_INSTRUCTION =
            "Return only JSON that matches the provided response schema. Do not include markdown or prose."
    }
}
