package com.mymarchent.mymarchent.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mymarchent.mymarchent.data.local.SessionManager

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)

    private val _apiKey = MutableLiveData<String?>()
    val apiKey: LiveData<String?> = _apiKey

    fun loadUserDetails() {
        _apiKey.value = sessionManager.getApiKey()
    }

    fun logout() {
        sessionManager.clearKeys()
    }
}