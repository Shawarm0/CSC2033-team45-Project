package com.team45.mysustainablecity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team45.mysustainablecity.data.classes.User
import com.team45.mysustainablecity.reps.UserRep
import io.github.jan.supabase.auth.exception.AuthRestException
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

    fun register(
        email: String,
        password: String,
        role: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null

            try {
                userRep.registerUser(email, password, role)
            } catch (e: AuthRestException) {
                _errorState.value = e.description
            } catch (e: Exception) {
                _errorState.value = e.message ?: "User creation failed"
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
            } catch (e: AuthRestException) {
                _errorState.value = e.description
            } catch (e: Exception) {
                _errorState.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                userRep.logout()
            } catch (e: Exception) {
                println("Logout error: ${e.message}")
                throw e
            }
        }
    }
}