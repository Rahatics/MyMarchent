package com.mymarchent.mymarchent.data.repository

import com.mymarchent.mymarchent.data.network.RetrofitClient

/**
 * Repository responsible for handling authentication-related data operations.
 */
class LoginRepository {

    private val apiService = RetrofitClient.apiService

    /**
     * Calls the ApiService to verify the API keys.
     */
    suspend fun verifyKeys(apiKey: String, secretKey: String) = apiService.verifyKeys(apiKey, secretKey)
}