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
import com.diaita.lib.factories.PromptFactory
import com.diaita.lib.mappings.*
import com.diaita.lib.prompt_extensions.toPromptVariables
import com.diaita.repo.RecommendationRepo
import com.diaita.repo.UserRepo
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement


class UserService(
    private val userRepo: UserRepo,
    private val client: GeminiRestClient,
    private val recommendationRepo: RecommendationRepo
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun registerUserProfile(request: RegisterUserProfileRequestDto): ServiceResult<RegisterUserProfileResponseDto> = coroutineScope {
        val upsertResult = async { runCatching { userRepo.upsertUserProfile(request) } }.await()
        val profile = upsertResult.getOrElse {
            return@coroutineScope ServiceResult.Failure("upsertUserProfile failed: ${it.message}")
        }
        if (profile == null) {
            return@coroutineScope ServiceResult.Failure("upsertUserProfile failed: returned null")
        }

        val recommendationResult = async { runCatching { genRecommendations(profile) } }.await()

        val recommendation = recommendationResult.getOrElse {
            return@coroutineScope ServiceResult.Failure("genRecommendations failed: ${it.message}")
        }
        if (recommendation == null) {
            return@coroutineScope ServiceResult.Failure("genRecommendations failed: returned null")
        }

        val saved = try {
            saveUserRecommendations(request.userId, recommendation)
        } catch (e: Exception) {
            return@coroutineScope ServiceResult.Failure("saveUserRecommendations failed: ${e.message}")
        }
        if (!saved) {
            return@coroutineScope ServiceResult.Failure("saveUserRecommendations failed: save returned false")
        }

        ServiceResult.Success(RegisterUserProfileResponseDto(profile = profile, recommendation = recommendation))
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
        UserSettingsPage.BASIC_DEMOGRAPHICS -> runSettingsAction(
            action = action,
            userId = userId,
            payload = payload,
            getter = userRepo::getBasicDemographics,
            updater = userRepo::updateBasicDemographics,
            deleter = userRepo::deleteBasicDemographics,
            decodeDto = { json.decodeFromJsonElement<BasicDemographicsDto>(it) },
            toEntity = { payload, id -> payload.toEntity(id) },
            toDto = { entity -> entity.toDto() }
        )
        UserSettingsPage.ACTIVITY_LIFESTYLE -> runSettingsAction(
            action = action,
            userId = userId,
            payload = payload,
            getter = userRepo::getActivityLifestyle,
            updater = userRepo::updateActivityLifestyle,
            deleter = userRepo::deleteActivityLifestyle,
            decodeDto = { json.decodeFromJsonElement<ActivityLevelLifestyleDto>(it) },
            toEntity = { payload, id -> payload.toEntity(id) },
            toDto = { entity -> entity.toDto() }
        )
        UserSettingsPage.GOALS_PRIORITIES -> runSettingsAction(
            action = action,
            userId = userId,
            payload = payload,
            getter = userRepo::getGoalsPriorities,
            updater = userRepo::updateGoalsPriorities,
            deleter = userRepo::deleteGoalsPriorities,
            decodeDto = { json.decodeFromJsonElement<GoalsPrioritiesDto>(it) },
            toEntity = { payload, id -> payload.toEntity(id) },
            toDto = { entity -> entity.toDto() }
        )
        UserSettingsPage.TRAINING_BACKGROUND -> runSettingsAction(
            action = action,
            userId = userId,
            payload = payload,
            getter = userRepo::getTrainingBackground,
            updater = userRepo::updateTrainingBackground,
            deleter = userRepo::deleteTrainingBackground,
            decodeDto = { json.decodeFromJsonElement<TrainingBackgroundDto>(it) },
            toEntity = { payload, id -> payload.toEntity(id) },
            toDto = { entity -> entity.toDto() }
        )
        UserSettingsPage.NUTRITION_HISTORY -> runSettingsAction(
            action = action,
            userId = userId,
            payload = payload,
            getter = userRepo::getNutritionHistory,
            updater = userRepo::updateNutritionHistory,
            deleter = userRepo::deleteNutritionHistory,
            decodeDto = { json.decodeFromJsonElement<NutritionDietHistoryDto>(it) },
            toEntity = { payload, id -> payload.toEntity(id) },
            toDto = { entity -> entity.toDto() }
        )
    }

    private suspend fun <TDto : Any, TEntity : Any> runSettingsAction(
        action: UserSettingsAction,
        userId: String,
        payload: JsonElement?,
        getter: suspend (String) -> TEntity?,
        updater: suspend (String, TEntity) -> TEntity?,
        deleter: suspend (String) -> Boolean,
        decodeDto: (JsonElement) -> TDto,
        toEntity: (TDto, String) -> TEntity,
        toDto: (TEntity) -> TDto
    ): Any? = when (action) {
        UserSettingsAction.GET -> getter(userId)?.let(toDto)
        UserSettingsAction.UPDATE -> payload?.let(decodeDto)?.let { updater(userId, toEntity(it, userId))?.let(toDto) }
        UserSettingsAction.DELETE -> deleter(userId)
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
