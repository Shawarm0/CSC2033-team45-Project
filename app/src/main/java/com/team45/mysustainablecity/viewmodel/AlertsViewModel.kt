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
    private val userRep: UserRep,
): ViewModel(){
    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts
    /**
     * Load alerts for the user
     */
    fun loadAlerts(userId: String?) {
        viewModelScope.launch {
            try {
                userRep.getAlerts(userId).collect { alertsList ->
                    Log.d("AuthViewModel", "successfully loaded alerts $alertsList")
                    _alerts.value = alertsList
                }

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Failed to load alerts: ${e.message}")
            }
        }
    }
}