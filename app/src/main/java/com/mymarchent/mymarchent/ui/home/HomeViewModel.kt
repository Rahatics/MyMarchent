package com.mymarchent.mymarchent.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mymarchent.mymarchent.data.model.Order
import com.mymarchent.mymarchent.data.model.SyncResult
import com.mymarchent.mymarchent.data.repository.HomeRepository
import com.mymarchent.mymarchent.util.NetworkState
import kotlinx.coroutines.launch
import java.io.IOException

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HomeRepository(application)

    private val _syncResult = MutableLiveData<SyncResult>()
    val syncResult: LiveData<SyncResult> = _syncResult

    private val _awaitingConfirmationCount = MutableLiveData<Int>()
    val awaitingConfirmationCount: LiveData<Int> = _awaitingConfirmationCount

    private val _confirmedTodayCount = MutableLiveData<Int>(0)
    val confirmedTodayCount: LiveData<Int> = _confirmedTodayCount

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    private val _approvalStatus = MutableLiveData<String>()
    val approvalStatus: LiveData<String> = _approvalStatus

    init {
        performSync()
    }

    fun performSync() {
        if (!NetworkState.isConnected(getApplication())) {
            _error.value = "No internet connection."
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val pendingOrders = repository.getPendingOrders()
                if (pendingOrders == null) {
                    _error.postValue("Could not fetch pending orders.")
                    _isLoading.postValue(false)
                    return@launch
                }
                _awaitingConfirmationCount.postValue(pendingOrders.size)

                val smsTransactions = repository.getSmsTransactions()
                val smsTrxMap = smsTransactions.associateBy { it.trxId }

                val matched = pendingOrders.filter { 
                    val sms = smsTrxMap[it.customerTrxId]
                    sms != null && sms.amount == it.amount
                }
                val unmatched = pendingOrders.filterNot { matched.contains(it) }

                _syncResult.postValue(SyncResult(matched, unmatched))

            } catch (e: IOException) {
                _error.postValue("Network error. Could not sync data.")
            } catch (e: Exception) {
                _error.postValue("An unknown error occurred during sync: ${e.message}")
            }
            _isLoading.postValue(false)
        }
    }
    
    fun approveSingleOrder(order: Order) {
        approveMatchedOrders(listOf(order))
    }

    fun approveMatchedOrders(orders: List<Order>) {
        if (!NetworkState.isConnected(getApplication())) {
            _error.value = "No internet connection."
            return
        }

        if (orders.isEmpty()) {
            _error.value = "No orders to approve."
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val orderIds = orders.map { it.orderId }
                val response = repository.confirmPayments(orderIds)

                if (response?.isSuccessful == true) {
                    val message = if (orderIds.size == 1) "Payment for order #${orders.first().orderId} approved!" else "${orderIds.size} payments approved successfully!"
                    _approvalStatus.postValue(message)
                    _confirmedTodayCount.value = (_confirmedTodayCount.value ?: 0) + orderIds.size
                    performSync()
                } else {
                    _error.postValue("Approval failed: ${response?.errorBody()?.string()}")
                }
            } catch (e: IOException) {
                _error.postValue("Network error. Could not approve payments.")
            } catch (e: Exception) {
                _error.postValue("An unknown error occurred during approval: ${e.message}")
            }
             _isLoading.postValue(false)
        }
    }
}