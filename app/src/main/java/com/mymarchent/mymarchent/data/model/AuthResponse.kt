package com.mymarchent.mymarchent.data.model

/**
 * Represents the response from the server after an authentication test.
 */
data class AuthResponse(
    val status: String,
    val message: String
)