package com.team45.mysustainablecity.reps

import com.team45.mysustainablecity.data.classes.User
import com.team45.mysustainablecity.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email

class UserRep {
    private val client = SupabaseClientProvider.client


    fun registerUser(user: User): Int {
        TODO("Register a user")
    }

    //suspend because needs to pause without blocking main thread
    suspend fun loginUser(email: String, password: String) {
        try {
            client.auth.signInWith(Email)
        } catch (e: AuthRestException) {

        }
    }

}