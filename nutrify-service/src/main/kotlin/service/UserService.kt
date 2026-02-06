package com.nutrify.service

import com.nutrify.dto.GenerationConfig
import com.nutrify.dto.RegisterUserProfileRequest
import com.nutrify.entity.UserMetadata
import com.nutrify.lib.clients.GeminiRestClient
import com.nutrify.lib.factories.PromptFactory
import com.nutrify.lib.prompt_extensions.toPromptVariables
import com.nutrify.repo.UserRepo


class UserService(private val userRepo: UserRepo, private val client: GeminiRestClient) {

    suspend fun registerUserProfile(request: RegisterUserProfileRequest): String? {
        val result = userRepo.upsertFullProfile(request)
        if (result != "Mutation Success") {
            return null
        }
        genRecommendations(request)
        return "Mutation Success"
    }

    suspend fun genRecommendations(
        request: RegisterUserProfileRequest,
        config: GenerationConfig? = null,
        systemInstruction: String? = null
    ): String? {
        val prompt = PromptFactory.getPromptWithVariables("registerUserMetadata", request.toPromptVariables())
        val result = client.askQuestion(prompt, config, systemInstruction)
        return result
    }

   suspend fun saveUserRecommendations(userId: String): UserMetadata? {
       return null
   }
}
