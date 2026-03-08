package com.team45.mysustainablecity.reps

import android.util.Log
import com.team45.mysustainablecity.data.classes.Alert
import com.team45.mysustainablecity.data.classes.User
import com.team45.mysustainablecity.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalTime::class)
class UserRep {
    private val client = SupabaseClientProvider.client


    /**
     * Register user with Supabase Auth + insert profile
     */
    @OptIn(ExperimentalUuidApi::class)
    suspend fun registerUser(
        email: String,
        password: String,
    ) {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }
    //suspend because needs to pause without blocking main thread
    suspend fun loginUser(email: String, password: String) {
        try {
            val result = client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Log.d("UserRep", "Sign in result: $result")
        } catch (e: Exception) {
            Log.e("UserRep", "Login failed: ${e.message}")
        }
    }

    suspend fun logout() {
        client.auth.signOut()
    }

    @OptIn(ExperimentalUuidApi::class)
    fun observeSession(): Flow<User?> = flow {

        Log.d("UserRep", "Starting session observation")

        val existing = client.auth.currentSessionOrNull()

        if (existing != null) {
            Log.d("UserRep", "Existing session found")
            emit(loadUser(existing.user?.id))
        } else {
            Log.d("UserRep", "No existing session")
            emit(null)
        }

        client.auth.sessionStatus.collect { status ->

            Log.d("UserRep", "Session status update: $status")

            when (status) {
                is SessionStatus.Authenticated -> {
                    emit(loadUser(status.session.user?.id))
                }

                is SessionStatus.NotAuthenticated -> {
                    emit(null)
                }

                else -> Unit
            }
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun loadUser(id: String?): User? {
        if (id == null) return null

        Log.d("UserRep", "Loading profile for $id")

        return client
            .from("users")
            .select {
                filter { eq("user_id", id) }
            }
            .decodeSingleOrNull<User>()
    }

    /**
     * Update a user in Supabase
     */
    suspend fun updateUser(user: User): Boolean {
        Log.d("UserRep", "Updating user profile for ${user.userID}")
        try {
            client.from("users").update(user) {
                filter {
                    eq("user_id", user.userID)
                }
            }
            return true
        } catch (e: Exception) {
            Log.e("UserRep", "Failed to update user profile: ${e.message}")
            return false
        }
    }


    suspend fun deleteUser(id: String?): Boolean {
        if (id == null) return false

        Log.d("UserRep", "Attempting to delete user profile for $id")
        try {
            client.from("users").delete {
                filter {
                    eq("user_id", id)
                }
            }
            Log.d("UserRep", "Successfully deleted user profile for $id")
            return true
        } catch (e: Exception) {
            Log.e("UserRep", "Failed to delete user profile: ${e.message}")
            return false
        }
    }

    suspend fun getSelf(): User? {
        val currentUser = client.auth.currentUserOrNull()
        if (currentUser != null) {
            Log.d("UserRep", "Found currently authenticated user ${currentUser.id}, returning...")
            return loadUser(currentUser.id)
        } else {
            Log.d("UserRep", "No user currently authenticated")
            return null
        }
    }

    suspend fun getAlerts(id: String?): StateFlow<List<Alert>> {
        val alertsFlow = MutableStateFlow<List<Alert>>(emptyList())

        if (id == null) return alertsFlow

        try {
            val alerts = client.from("alerts").select {
                filter { eq("user_id", id) }
            }.decodeList<Alert>()
            Log.d("UserRep", "Alerts: $alerts")
            alertsFlow.value = alerts
        } catch (e: Exception) {
            Log.e("UserRep", "Failed to get alerts: ${e.message}")
        }
        return alertsFlow
    }

    suspend fun markAlertRead(alertId: String?): Boolean {
        if (alertId == null) return false
        try {
            client.from("alerts").update(mapOf("is_read" to true)) {    // Set is_read field to true, no need to check if its false before
                filter { eq("alert_id", alertId) }
            }
            Log.d("UserRep", "Successfully marked alert: $alertId as read")
            return true
        } catch (e: Exception) {
            Log.e("UserRep", "Failed to mark alert as read: ${e.message}")
            return false
        }
    }

    suspend fun markAllRead(): Boolean {
        try {
            client.from("alerts").update(mapOf("is_read" to true)) {
                filter { eq("user_id", getSelf()?.userID ?: error("User ID is null")) }     // return false if the userID is null
            }
            Log.d("UserRep", "Successfully marked as read all alerts for user ${getSelf()?.userID}")
            return true
        } catch (e: Exception) {
            Log.e("UserRep", "Failed to mark all alerts as read: ${e.message}")
            return false
        }
    }
}