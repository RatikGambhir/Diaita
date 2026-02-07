package com.nutrify.service

import com.nutrify.dto.GenerationConfigDto
import com.nutrify.dto.RegisterUserProfileRequestDto
import com.nutrify.entity.UserMetadataEntity
import com.nutrify.lib.clients.GeminiRestClient
import com.nutrify.lib.factories.PromptFactory
import com.nutrify.lib.prompt_extensions.toPromptVariables
import com.nutrify.repo.UserRepo
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
}
