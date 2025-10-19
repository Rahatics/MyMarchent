package com.mymarchent.mymarchent.data.model

/**
 * Represents the result of the sync and match process.
 *
 * @param matchedOrders A list of orders where the TrxID was found in the user's SMS.
 * @param unmatchedOrders A list of orders where the TrxID was not found.
 */
data class SyncResult(
    val matchedOrders: List<Order>,
    val unmatchedOrders: List<Order>
)