package com.diaita.controllers

import com.diaita.dto.ActivityLevelLifestyleDto
import com.diaita.dto.BasicDemographicsDto
import com.diaita.dto.BehavioralFactorsDto
import com.diaita.dto.GoalsPrioritiesDto
import com.diaita.dto.MedicalHistoryDto
import com.diaita.dto.MetricsTrackingDto
import com.diaita.dto.NutritionDietHistoryDto
import com.diaita.dto.RegisterUserProfileRequestDto
import com.diaita.dto.TrainingBackgroundDto
import com.diaita.service.UserService

class UserController(private val userService: UserService) {
    suspend fun registerUserProfile(request: RegisterUserProfileRequestDto): String? {
        return userService.registerUserProfile(request)
    }

    suspend fun getBasicDemographics(userId: String): BasicDemographicsDto? {
        return userService.getBasicDemographics(userId)
    }

    suspend fun updateBasicDemographics(userId: String, dto: BasicDemographicsDto): BasicDemographicsDto? {
        return userService.updateBasicDemographics(userId, dto)
    }

    suspend fun deleteBasicDemographics(userId: String): Boolean {
        return userService.deleteBasicDemographics(userId)
    }

    suspend fun getActivityLifestyle(userId: String): ActivityLevelLifestyleDto? {
        return userService.getActivityLifestyle(userId)
    }

    suspend fun updateActivityLifestyle(
        userId: String,
        dto: ActivityLevelLifestyleDto
    ): ActivityLevelLifestyleDto? {
        return userService.updateActivityLifestyle(userId, dto)
    }

    suspend fun deleteActivityLifestyle(userId: String): Boolean {
        return userService.deleteActivityLifestyle(userId)
    }

    suspend fun getGoalsPriorities(userId: String): GoalsPrioritiesDto? {
        return userService.getGoalsPriorities(userId)
    }

    suspend fun updateGoalsPriorities(userId: String, dto: GoalsPrioritiesDto): GoalsPrioritiesDto? {
        return userService.updateGoalsPriorities(userId, dto)
    }

    suspend fun deleteGoalsPriorities(userId: String): Boolean {
        return userService.deleteGoalsPriorities(userId)
    }

    suspend fun getTrainingBackground(userId: String): TrainingBackgroundDto? {
        return userService.getTrainingBackground(userId)
    }

    suspend fun updateTrainingBackground(userId: String, dto: TrainingBackgroundDto): TrainingBackgroundDto? {
        return userService.updateTrainingBackground(userId, dto)
    }

    suspend fun deleteTrainingBackground(userId: String): Boolean {
        return userService.deleteTrainingBackground(userId)
    }

    suspend fun getMedicalHistory(userId: String): MedicalHistoryDto? {
        return userService.getMedicalHistory(userId)
    }

    suspend fun updateMedicalHistory(userId: String, dto: MedicalHistoryDto): MedicalHistoryDto? {
        return userService.updateMedicalHistory(userId, dto)
    }

    suspend fun deleteMedicalHistory(userId: String): Boolean {
        return userService.deleteMedicalHistory(userId)
    }

    suspend fun getNutritionHistory(userId: String): NutritionDietHistoryDto? {
        return userService.getNutritionHistory(userId)
    }

    suspend fun updateNutritionHistory(
        userId: String,
        dto: NutritionDietHistoryDto
    ): NutritionDietHistoryDto? {
        return userService.updateNutritionHistory(userId, dto)
    }

    suspend fun deleteNutritionHistory(userId: String): Boolean {
        return userService.deleteNutritionHistory(userId)
    }

    suspend fun getBehavioralFactors(userId: String): BehavioralFactorsDto? {
        return userService.getBehavioralFactors(userId)
    }

    suspend fun updateBehavioralFactors(userId: String, dto: BehavioralFactorsDto): BehavioralFactorsDto? {
        return userService.updateBehavioralFactors(userId, dto)
    }

    suspend fun deleteBehavioralFactors(userId: String): Boolean {
        return userService.deleteBehavioralFactors(userId)
    }

    suspend fun getMetricsTracking(userId: String): MetricsTrackingDto? {
        return userService.getMetricsTracking(userId)
    }

    suspend fun updateMetricsTracking(userId: String, dto: MetricsTrackingDto): MetricsTrackingDto? {
        return userService.updateMetricsTracking(userId, dto)
    }

    suspend fun deleteMetricsTracking(userId: String): Boolean {
        return userService.deleteMetricsTracking(userId)
    }
}
