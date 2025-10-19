package com.mymarchent.mymarchent.data.repository

import android.content.Context
import com.mymarchent.mymarchent.data.local.SessionManager
import com.mymarchent.mymarchent.data.model.ConfirmPaymentsRequest
import com.mymarchent.mymarchent.data.model.Order
import com.mymarchent.mymarchent.data.network.RetrofitClient
import com.mymarchent.mymarchent.data.sms.ParsedSms
import com.mymarchent.mymarchent.data.sms.SmsParser
import com.mymarchent.mymarchent.data.sms.SmsReader
import retrofit2.Response

/**
 * Repository for the home screen. Fetches data from network and local sources.
 */
class HomeRepository(private val context: Context) {

    private val apiService = RetrofitClient.apiService
    private val sessionManager = SessionManager(context)
    private val smsReader = SmsReader(context)

    suspend fun getPendingOrders(): List<Order>? {
        val apiKey = sessionManager.getApiKey() ?: return null
        val secretKey = sessionManager.getSecretKey() ?: return null

        val response = apiService.getPendingOrders(apiKey, secretKey)
        return if (response.isSuccessful) {
            response.body()?.orders
        } else {
            null
        }
    }

    suspend fun getSmsTransactions(): List<ParsedSms> {
        val smsList = smsReader.readSmsFromLast24Hours()
        return smsList.mapNotNull { smsBody ->
            SmsParser.parse(smsBody)
        }
    }

    suspend fun confirmPayments(orderIds: List<String>): Response<Unit>? {
        val apiKey = sessionManager.getApiKey() ?: return null
        val secretKey = sessionManager.getSecretKey() ?: return null
        val request = ConfirmPaymentsRequest(orderIds)
        return apiService.confirmPayments(apiKey, secretKey, request)
    }
}