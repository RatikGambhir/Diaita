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
import com.diaita.dto.RegisterUserProfileResponseDto
import com.diaita.dto.RegisteredUserProfileDto
import com.diaita.dto.ServiceResult
import com.diaita.dto.TrainingBackgroundDto
import com.diaita.dto.UserSettingsAction
import com.diaita.dto.UserSettingsPage
import com.diaita.entity.ActivityLifestyleRowEntity
import com.diaita.entity.BasicDemographicsRowEntity
import com.diaita.entity.GoalsPrioritiesRowEntity
import com.diaita.entity.NutritionHistoryRowEntity
import com.diaita.entity.TrainingBackgroundRowEntity
import com.diaita.lib.factories.PromptFactory
import com.diaita.lib.logging.logger
import com.diaita.lib.mappings.*
import com.diaita.lib.prompt_extensions.toPromptVariables
import com.diaita.repo.RecommendationRepo
import com.diaita.repo.UserRepo
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement


class UserService(
    private val userRepo: UserRepo,
    private val client: GeminiRestClient,
    private val recommendationRepo: RecommendationRepo
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val log = logger()

    /**
     * Persists the profile, then generates and stores recommendations for it.
     * Each step depends on the previous one, so they run sequentially; any
     * failure short-circuits with the step that caused it.
     */
    suspend fun registerUserProfile(
        request: RegisterUserProfileRequestDto
    ): ServiceResult<RegisterUserProfileResponseDto> {
        val profile = runStep("upsertUserProfile") { userRepo.upsertUserProfile(request) }
            ?: return ServiceResult.Failure("upsertUserProfile failed")

        val recommendation = runStep("genRecommendations") { genRecommendations(profile) }
            ?: return ServiceResult.Failure("genRecommendations failed")

        val saved = runStep("saveUserRecommendations") {
            saveUserRecommendations(request.userId, recommendation)
        }
        if (saved != true) {
            return ServiceResult.Failure("saveUserRecommendations failed")
        }

        return ServiceResult.Success(
            RegisterUserProfileResponseDto(profile = profile, recommendation = recommendation)
        )
    }

    /** Runs one registration step, logging and swallowing failures into `null`. */
    private suspend fun <T> runStep(name: String, block: suspend () -> T?): T? =
        try {
            block() ?: null.also { log.error("{} failed: returned null", name) }
        } catch (e: Exception) {
            log.error("{} failed: {}", name, e.message, e)
            null
        }

    suspend fun genRecommendations(
        request: RegisterUserProfileRequestDto,
        config: GenerationConfigDto? = null,
        systemInstruction: String? = null
    ): RecommendationDto? = genRecommendations(
        promptVariables = request.toPromptVariables(),
        config = config,
        systemInstruction = systemInstruction
    )

    suspend fun generateAndSaveRecommendations(userId: String): ServiceResult<RecommendationDto> {
        val profile = userRepo.getFullProfile(userId)
            ?: return ServiceResult.Failure("getFullProfile failed: profile not found for userId=$userId")
        val recommendation = try {
            genRecommendations(profile)
        } catch (e: Exception) {
            return ServiceResult.Failure("genRecommendations failed: ${e.message}")
        } ?: return ServiceResult.Failure("genRecommendations failed: returned null")
        return if (saveUserRecommendations(userId, recommendation)) {
            ServiceResult.Success(recommendation)
        } else {
            ServiceResult.Failure("saveUserRecommendations failed: save returned false")
        }
    }

    suspend fun getUserProfile(userId: String): RegisteredUserProfileDto? {
        return userRepo.getFullProfile(userId)
    }

    suspend fun saveUserRecommendations(userId: String, recommendation: RecommendationDto): Boolean {
        return recommendationRepo.saveRecommendation(userId, recommendation)
    }

    suspend fun getRecommendations(userId: String): RecommendationDto? {
        return recommendationRepo.getRecommendationByUserId(userId)
    }

    suspend fun handleUserSettings(
        userId: String,
        page: UserSettingsPage,
        action: UserSettingsAction,
        payload: JsonElement? = null
    ): Any? = when (page) {
        UserSettingsPage.BASIC_DEMOGRAPHICS ->
            runSettingsAction(page, action, userId, payload, BasicDemographicsDto::toEntity, BasicDemographicsRowEntity::toDto)
        UserSettingsPage.ACTIVITY_LIFESTYLE ->
            runSettingsAction(page, action, userId, payload, ActivityLevelLifestyleDto::toEntity, ActivityLifestyleRowEntity::toDto)
        UserSettingsPage.GOALS_PRIORITIES ->
            runSettingsAction(page, action, userId, payload, GoalsPrioritiesDto::toEntity, GoalsPrioritiesRowEntity::toDto)
        UserSettingsPage.TRAINING_BACKGROUND ->
            runSettingsAction(page, action, userId, payload, TrainingBackgroundDto::toEntity, TrainingBackgroundRowEntity::toDto)
        UserSettingsPage.NUTRITION_HISTORY ->
            runSettingsAction(page, action, userId, payload, NutritionDietHistoryDto::toEntity, NutritionHistoryRowEntity::toDto)
    }

    /**
     * Decodes the payload into [TDto], hands the corresponding row entity to the
     * repository, and maps whatever comes back to [TDto] again. `DELETE` returns
     * a boolean instead of a DTO.
     */
    private suspend inline fun <reified TDto : Any, reified TEntity : Any> runSettingsAction(
        page: UserSettingsPage,
        action: UserSettingsAction,
        userId: String,
        payload: JsonElement?,
        toEntity: (TDto, String) -> TEntity,
        toDto: (TEntity) -> TDto
    ): Any? {
        val updateEntity = if (action == UserSettingsAction.UPDATE) {
            payload
                ?.let { json.decodeFromJsonElement<TDto>(it) }
                ?.let { dto -> toEntity(dto, userId) }
        } else {
            null
        }

        val result = genAction(action)(page, userId, updateEntity)
        return when (action) {
            UserSettingsAction.DELETE -> result as? Boolean ?: false
            UserSettingsAction.GET,
            UserSettingsAction.UPDATE -> (result as? TEntity)?.let(toDto)
        }
    }

    private fun genAction(
        action: UserSettingsAction
    ): suspend (UserSettingsPage, String, Any?) -> Any? = when (action) {
        UserSettingsAction.GET -> { page, userId, _ ->
            userRepo.getSettingsSection(page, userId)
        }
        UserSettingsAction.UPDATE -> { page, userId, entity ->
            entity?.let { userRepo.updateSettingsSection(page, userId, it) }
        }
        UserSettingsAction.DELETE -> { page, userId, _ ->
            userRepo.deleteSettingsSection(page, userId)
        }
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
        return "$prompt\n\nReturn only strict RFC 8259 JSON that matches the provided response schema. Do not use trailing commas."
    }

    private companion object {
        const val STRUCTURED_OUTPUT_SYSTEM_INSTRUCTION =
            "Return only strict RFC 8259 JSON that matches the provided response schema. Do not include markdown, comments, or prose. Do not include trailing commas."
    }
}
