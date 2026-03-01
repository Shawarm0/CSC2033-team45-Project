package com.team45.mysustainablecity.reps

import android.util.Log
import com.team45.mysustainablecity.data.classes.User
import com.team45.mysustainablecity.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.time.ExperimentalTime
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
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
                filter { eq("userID", id) }
            }
            .decodeSingleOrNull<User>()
    }

    /**
     * Update a user in Supabase
     */
    private suspend fun updateUser(user: User): Boolean {
        Log.d("UserRep", "Updating user profile for ${user.userID}")
        try {
            client.from("users").update(user) {
                filter {
                    eq("userID", user.userID)
                }
            }
            return true
        } catch (e: Exception) {
            Log.e("UserRep", "Failed to update user profile: ${e.message}")
            return false
        }
    }


    suspend fun logout() {
        client.auth.signOut()
    }
}