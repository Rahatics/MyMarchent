package com.mymarchent.mymarchent.data.model

/**
 * Represents a single pending order from the server.
 */
data class Order(
    val orderId: String,
    val amount: Double,
    val customerTrxId: String // The TrxID submitted by the customer
)