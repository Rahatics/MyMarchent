package com.mymarchent.mymarchent.data.model

/**
 * Represents the server response containing a list of pending orders.
 */
data class PendingOrdersResponse(
    val status: String,
    val orders: List<Order>
)