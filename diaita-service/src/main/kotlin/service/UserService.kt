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
import com.diaita.dto.ServiceResult
import com.diaita.dto.TrainingBackgroundDto
import com.diaita.lib.factories.PromptFactory
import com.diaita.lib.mappings.*
import com.diaita.lib.prompt_extensions.toPromptVariables
import com.diaita.repo.RecommendationRepo
import com.diaita.repo.UserRepo
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope


class UserService(
    private val userRepo: UserRepo,
    private val client: GeminiRestClient,
    private val recommendationRepo: RecommendationRepo
) {

    suspend fun registerUserProfile(request: RegisterUserProfileRequestDto): ServiceResult<RecommendationDto> = coroutineScope {
        coroutineScope {
            val upsertDeferred = async { runCatching { userRepo.upsertFullProfile(request) } }
            val recommendationDeferred = async { runCatching { genRecommendations(request) } }

            val upsertResult = upsertDeferred.await()
            val recommendationResult = recommendationDeferred.await()
            upsertResult.getOrElse {
                return@coroutineScope ServiceResult.Failure("upsertFullProfile failed: ${it.message}")
            }
//            if (result != "Mutation Success") {
//                return@coroutineScope ServiceResult.Failure("upsertFullProfile failed: $result")
//            }

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

            ServiceResult.Success(recommendation)
        }


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

    suspend fun genRecommendations(
        profile: RegisteredUserProfileDto,
        config: GenerationConfigDto? = null,
        systemInstruction: String? = null
    ): RecommendationDto? = genRecommendations(
        promptVariables = profile.toPromptVariables(),
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
