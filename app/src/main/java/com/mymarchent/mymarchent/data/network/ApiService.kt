package com.mymarchent.mymarchent.data.network

import com.mymarchent.mymarchent.data.model.AuthResponse
import com.mymarchent.mymarchent.data.model.ConfirmPaymentsRequest
import com.mymarchent.mymarchent.data.model.PendingOrdersResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Defines the API endpoints for network calls.
 */
interface ApiService {

    @POST("api/auth/verify")
    suspend fun verifyKeys(
        @Header("X-API-Key") apiKey: String,
        @Header("X-Secret-Key") secretKey: String
    ): Response<AuthResponse>

    @GET("api/orders/pending")
    suspend fun getPendingOrders(
        @Header("X-API-Key") apiKey: String,
        @Header("X-Secret-Key") secretKey: String
    ): Response<PendingOrdersResponse>

    @POST("api/orders/confirm")
    suspend fun confirmPayments(
        @Header("X-API-Key") apiKey: String,
        @Header("X-Secret-Key") secretKey: String,
        @Body request: ConfirmPaymentsRequest
    ): Response<Unit> // Assuming the server returns an empty success response
}