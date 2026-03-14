package com.team45.mysustainablecity.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team45.mysustainablecity.data.classes.Alert
import com.team45.mysustainablecity.reps.UserRep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlertsViewModel (
    //private val userRep: UserRep,
): ViewModel(){
    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState
    /**
     * Load alerts for the user
     */
    fun loadAlerts(userId: String?) {
        viewModelScope.launch {

            _isLoading.value = true
            _errorState.value = null

            try {
                UserRep.getAlerts(userId).collect { alertsList ->
                    Log.d("AlertsViewModel", "Successfully loaded alerts: $alertsList")
                    _alerts.value = alertsList
                }

            } catch (e: Exception) {
                Log.e("AlertsViewModel", "Failed to load alerts: ${e.message}")
                _errorState.value = e.message

            } finally {
                _isLoading.value = false
            }
        }
    }


    /**
     * Mark alert as read
     */
    fun markAlertAsRead(alertId: String?) {
        viewModelScope.launch {

            _isLoading.value = true
            _errorState.value = null

            try {
                val success = UserRep.markAlertRead(alertId)

                if (success) {
                    Log.d("AlertsViewModel", "Alert marked as read")
                }else{
                    _errorState.value = "Failed to mark alert as read"
                    Log.e("AlertsViewModel", "Failed to mark alert as read")
                }

            } catch (e: Exception) {
                Log.e("AlertsViewModel", "Failed to mark alert as read: ${e.message}")
                _errorState.value = e.message

            } finally {
                _isLoading.value = false
            }
        }
    }
}