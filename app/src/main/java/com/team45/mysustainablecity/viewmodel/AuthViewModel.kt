package com.team45.mysustainablecity.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team45.mysustainablecity.data.classes.Alert
import com.team45.mysustainablecity.data.classes.User
import com.team45.mysustainablecity.data.remote.SupabaseClientProvider
import com.team45.mysustainablecity.reps.UserRep
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
class AuthViewModel(
    private val userRep: UserRep,
): ViewModel() {

    private val client = SupabaseClientProvider.client

    private val _authState = MutableStateFlow<User?>(null)
    val authState: StateFlow<User?> = _authState

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts

    private val _operationSuccess = MutableStateFlow<Boolean?>(null)
    val operationSuccess: StateFlow<Boolean?> = _operationSuccess

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    init {
        observeAuthState()
    }
    /**
     * Observe Supabase session automatically
     */
    private fun observeAuthState() {
        viewModelScope.launch {
            userRep.observeSession().collect { user:User? ->
                _authState.value = user
                _isAuthenticated.value = client.auth.currentSessionOrNull()?.user?.id != null

                if (user != null) {
                    Log.d("AuthViewModel", "Authenticated user: $user")
                } else {
                    Log.d("AuthViewModel", "User is null")
                }
            }
        }
    }

    fun register(
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null

            try {
                userRep.registerUser(email, password)
                Log.d("AuthViewModel", "register requested")
            } catch (e: AuthRestException) {
                _errorState.value = e.description
            } catch (e: Exception) {
                _errorState.value = e.message ?: "User creation failed"
                Log.e("AuthViewModel", "Auth error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null

            try {
                userRep.loginUser(email, password)
                Log.d("AuthViewModel", "Login request sent")
            } catch (e: AuthRestException) {
                Log.e("AuthViewModel", "Auth error: ${e.description}")
                _errorState.value = e.description
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login failed: ${e.message}")
                _errorState.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Update user profile
     */
    fun updateUser(user: User) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val result = userRep.updateUser(user)
                if (result) {
                    _authState.value = user
                }
                _operationSuccess.value = result


            } catch (e: Exception) {
                Log.e("AuthViewModel", "Update user failed: ${e.message}")
                _operationSuccess.value = false
            }
            _isLoading.value = false
        }
    }

    /**
     * Delete user account
     */
    fun deleteUser(userId: String?) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val result = userRep.deleteUser(userId)
                _operationSuccess.value = result

                if (result) {
                    _authState.value = null
                    _isAuthenticated.value = false
                }

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Delete user failed: ${e.message}")
                _operationSuccess.value = false
            }

            _isLoading.value = false
        }
    }


    /**
     * Load current user from database
     */
    fun loadSelf() {
        viewModelScope.launch {
            try {
                val user = userRep.getSelf()
                _authState.value = user
                _isAuthenticated.value = user != null

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Failed to load user: ${e.message}")
            }
        }
    }

    /**
     * Load alerts for the user
     */
    fun loadAlerts(userId: String?) {
        viewModelScope.launch {
            try {
                userRep.getAlerts(userId).collect { alertsList ->
                    _alerts.value = alertsList
                }

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Failed to load alerts: ${e.message}")
            }
        }
    }

    fun logout() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null

            try {
                userRep.logout()
            } catch (e: Exception) {
                _errorState.value = e.message ?: "Logout failed"
            } finally {
                _isLoading.value = false
            }
        }
    }
}