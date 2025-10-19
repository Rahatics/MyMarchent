package com.mymarchent.mymarchent.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mymarchent.mymarchent.data.local.SessionManager
import com.mymarchent.mymarchent.data.repository.LoginRepository
import com.mymarchent.mymarchent.util.NetworkState
import kotlinx.coroutines.launch
import java.io.IOException

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LoginRepository()
    private val sessionManager = SessionManager(application)

    private val _authResult = MutableLiveData<Boolean>()
    val authResult: LiveData<Boolean> = _authResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun verifyApiKeys(apiKey: String, secretKey: String) {
        if (!NetworkState.isConnected(getApplication())) {
            _error.value = "No internet connection."
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.verifyKeys(apiKey, secretKey)
                if (response.isSuccessful && response.body()?.status == "success") {
                    sessionManager.saveKeys(apiKey, secretKey)
                    _authResult.postValue(true)
                } else {
                    _authResult.postValue(false)
                    _error.postValue(response.errorBody()?.string() ?: "Invalid API or Secret Key")
                }
            } catch (e: IOException) {
                _authResult.postValue(false)
                _error.postValue("Network error. Please check your connection.")
            } catch (e: Exception) {
                _authResult.postValue(false)
                _error.postValue("An unknown error occurred: ${e.message}")
            }
            _isLoading.postValue(false)
        }
    }
}