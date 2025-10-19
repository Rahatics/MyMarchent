package com.mymarchent.mymarchent.data.model

/**
 * Represents the request body sent to the server to confirm matched orders.
 */
data class ConfirmPaymentsRequest(
    val orderIds: List<String>
)