package com.team45.mysustainablecity.reps

import com.team45.mysustainablecity.data.classes.Post
import com.team45.mysustainablecity.data.classes.PostInfo
import com.team45.mysustainablecity.data.remote.SupabaseClientProvider
import io.github.jan.supabase.functions.functions
import io.ktor.client.call.body
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class PostRep {
    private val client = SupabaseClientProvider.client
    val posts = MutableStateFlow(emptyList<PostInfo>())

    suspend fun loadPosts(): List<PostInfo> {
        val response = client.functions.invoke("return_post_details")
        val data = response.body<List<PostInfo>>()
        return data
    }
}