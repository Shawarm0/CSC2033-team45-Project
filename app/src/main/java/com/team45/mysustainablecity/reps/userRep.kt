package com.team45.mysustainablecity.reps

import com.team45.mysustainablecity.data.classes.User
import com.team45.mysustainablecity.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserRep {
    private val client = SupabaseClientProvider.client


    suspend fun registerUser(user: User, password: String): Boolean {
        val result = client.auth.signUpWith(Email) {
            email = user.email
            this.password = password
        }

        val authUser = result ?: return false

        client.from("users").insert(
            mapOf(
                "user_id" to authUser.id,
                "email" to user.email,
                "role" to user.role,
                "is_active" to true
            )
        )

        return true
    }
    //suspend because needs to pause without blocking main thread
    suspend fun loginUser(email: String, password: String) {
        try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        } catch (e: AuthRestException) {
            println("Auth error: ${e.description}")
            throw e
        }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    fun observeSession(): Flow<User?> =
        client.auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Authenticated -> {
                    val authUser = status.session.user ?: return@map null

                    val profile = client
                        .from("users")
                        .select {
                            filter {
                                eq("user_id", authUser.id)
                            }
                        }
                        .decodeSingle<User>()

                    User(
                        userId = Uuid.parse(authUser.id),
                        email = authUser.email!!,
                        role = profile.role,
                        createdAt = profile.createdAt,
                        lastLoginAt = authUser.lastSignInAt,
                        isActive = profile.isActive
                    )
                }
                else -> null
            }
        }

        suspend fun logout() {
            try {
                client.auth.signOut()
            } catch (e: Exception) {
                println("Logout error: ${e.message}")
                throw e
            }
        }

}