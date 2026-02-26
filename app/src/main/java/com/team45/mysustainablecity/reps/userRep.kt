package com.team45.mysustainablecity.reps

import com.team45.mysustainablecity.data.classes.User
import com.team45.mysustainablecity.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.datetime.Instant

class UserRep {
    private val client = SupabaseClientProvider.client


    /**
     * Register user with Supabase Auth + insert profile
     */
    suspend fun registerUser(
        email: String,
        password: String,
        role: String
    ) {
        val result = client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }

        val authUser = result ?: throw Exception("User creation failed")

        client.from("users").insert(
            mapOf(
                "user_id" to authUser.id,
                "email" to email,
                "role" to role,
                "is_active" to true
            )
        )
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

    @OptIn(ExperimentalUuidApi::class, ExperimentalCoroutinesApi::class)
    fun observeSession(): Flow<User?> =
        client.auth.sessionStatus.mapLatest { status ->
            when (status) {
                is SessionStatus.Authenticated -> {
                    val authUser = status.session.user ?: return@mapLatest null

                    val profile = client
                        .from("users")
                        .select {
                            filter { eq("user_id", authUser.id) }
                        }
                        .decodeSingleOrNull<User>()
                        ?: return@mapLatest null

                    User(
                        userId = Uuid.parse(authUser.id),
                        email = authUser.email!!,
                        role = profile.role,
                        createdAt = profile.createdAt,
                        isActive = profile.isActive,
                        lastLoginAt = authUser.lastSignInAt
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