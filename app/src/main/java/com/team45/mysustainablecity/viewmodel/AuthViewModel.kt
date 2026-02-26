package com.team45.mysustainablecity.viewmodel

import TokenManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team45.mysustainablecity.data.classes.User
import com.team45.mysustainablecity.reps.userRep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

class AuthViewModel(
    private val userRep: UserRep,
    private val tokenManager: TokenManager
): ViewModel() {

    private val _authState = MutableStateFlow<User?>(null)
    val authState: StateFlow<User?> = _authState

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    fun registerUser(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null

            try {

                val token = userRep.registerUser(user)

                // ✅ Save token securely
                tokenManager.saveToken(token)

                // ✅ Update states
                _authState.value = user
                _isAuthenticated.value = true


            } catch (e: Exception) {
                _errorState.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null

            try {
                val token = userRep.loginUser(email,password)
                tokenManager.saveToken(token)
                _authState.value = userRep.getSelf()
                _isAuthenticated.value = true

            } catch (e: Exception) {
                _errorState.value = e.message
                _isAuthenticated.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        tokenManager.clearToken()
        _authState.value = null
        _isAuthenticated.value = false
    }

}