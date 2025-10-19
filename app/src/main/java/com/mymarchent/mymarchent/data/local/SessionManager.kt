package com.mymarchent.mymarchent.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Manages the user session, storing and retrieving API keys securely.
 */
class SessionManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secret_session_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val API_KEY = "api_key"
        private const val SECRET_KEY = "secret_key"
    }

    fun saveKeys(apiKey: String, secretKey: String) {
        sharedPreferences.edit()
            .putString(API_KEY, apiKey)
            .putString(SECRET_KEY, secretKey)
            .apply()
    }

    fun getApiKey(): String? {
        return sharedPreferences.getString(API_KEY, null)
    }

    fun getSecretKey(): String? {
        return sharedPreferences.getString(SECRET_KEY, null)
    }
    
    fun isLoggedIn(): Boolean {
        return getApiKey() != null && getSecretKey() != null
    }

    fun clearKeys() {
        sharedPreferences.edit().clear().apply()
    }
}