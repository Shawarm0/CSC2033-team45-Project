package com.team45.mysustainablecity.data.remote

import com.team45.mysustainablecity.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.ktor.client.engine.okhttp.OkHttp


/**
 * Singleton object providing configured Supabase client and auth utilities.
 */
object SupabaseClientProvider {
    /**
     * Initialized Supabase client with installed modules and HTTP engine.
     */
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.API_KEY,
    ) {
        install(Postgrest)
        install(Realtime)
        install(Auth) {
            autoLoadFromStorage = true
            alwaysAutoRefresh = true
        }

        httpEngine = OkHttp.create()
    }
    var isAuth = false
}