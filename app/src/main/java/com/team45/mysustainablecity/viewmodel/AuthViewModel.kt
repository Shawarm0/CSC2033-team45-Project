package com.team45.mysustainablecity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team45.mysustainablecity.data.classes.User
import com.team45.mysustainablecity.reps.UserRep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

class AuthViewModel(
    private val userRep: UserRep,
): ViewModel() {

    private val _authState = MutableStateFlow<User?>(null)
    val authState: StateFlow<User?> = _authState

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

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
                _isAuthenticated.value = user != null
            }
        }
    }

    fun registerUser(user: User,password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null

            try {
                userRep.registerUser(user,password)
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
                userRep.loginUser(email,password)
            } catch (e: Exception) {
                _errorState.value = e.message
                _isAuthenticated.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRep.logout()
        }
    }

}