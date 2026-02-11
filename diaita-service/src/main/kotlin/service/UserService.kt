package com.diaita.service

import com.diaita.dto.*
import com.diaita.entity.UserMetadataEntity
import com.diaita.lib.clients.GeminiRestClient
import com.diaita.lib.factories.PromptFactory
import com.diaita.lib.mappings.*
import com.diaita.lib.prompt_extensions.toPromptVariables
import com.diaita.repo.UserRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope


class UserService(private val userRepo: UserRepo, private val client: GeminiRestClient) {

    suspend fun registerUserProfile(request: RegisterUserProfileRequestDto): String? {
        //TODO: Add coroutine scope here
        val result = userRepo.upsertFullProfile(request)
        if (result != "Mutation Success") {
            return null
        }
        val recommendation = genRecommendations(request)
        if (recommendation == null) {
            return null
        }
        return "Mutation Success"
    }

    suspend fun registerUser(request: RegisterUserProfileRequestDto): String? = coroutineScope {
        val upsertDeferred = async(Dispatchers.IO) { userRepo.upsertFullProfile(request) }
        val recommendationDeferred = async(Dispatchers.Default) {genRecommendations(request)}
        upsertDeferred.await()
        recommendationDeferred.await() ?: return@coroutineScope null
        "Mutation Success"
    }

    suspend fun genRecommendations(
        request: RegisterUserProfileRequestDto,
        config: GenerationConfigDto? = null,
        systemInstruction: String? = null
    ): String? {
        val prompt = PromptFactory.getPromptWithVariables("registerUserMetadata", request.toPromptVariables())
        val result = client.askQuestionStream(prompt, systemInstruction, config)
        return result
    }

   suspend fun saveUserRecommendations(userId: String): UserMetadataEntity? {
       return null
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

    suspend fun getMedicalHistory(userId: String): MedicalHistoryDto? {
        return userRepo.getMedicalHistory(userId)?.toDto()
    }

    suspend fun updateMedicalHistory(userId: String, dto: MedicalHistoryDto): MedicalHistoryDto? {
        return userRepo.updateMedicalHistory(userId, dto.toEntity(userId))?.toDto()
    }

    suspend fun deleteMedicalHistory(userId: String): Boolean {
        return userRepo.deleteMedicalHistory(userId)
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

    suspend fun getBehavioralFactors(userId: String): BehavioralFactorsDto? {
        return userRepo.getBehavioralFactors(userId)?.toDto()
    }

    suspend fun updateBehavioralFactors(userId: String, dto: BehavioralFactorsDto): BehavioralFactorsDto? {
        return userRepo.updateBehavioralFactors(userId, dto.toEntity(userId))?.toDto()
    }

    suspend fun deleteBehavioralFactors(userId: String): Boolean {
        return userRepo.deleteBehavioralFactors(userId)
    }

    suspend fun getMetricsTracking(userId: String): MetricsTrackingDto? {
        return userRepo.getMetricsTracking(userId)?.toDto()
    }

    suspend fun updateMetricsTracking(userId: String, dto: MetricsTrackingDto): MetricsTrackingDto? {
        return userRepo.updateMetricsTracking(userId, dto.toEntity(userId))?.toDto()
    }

    suspend fun deleteMetricsTracking(userId: String): Boolean {
        return userRepo.deleteMetricsTracking(userId)
    }
}
