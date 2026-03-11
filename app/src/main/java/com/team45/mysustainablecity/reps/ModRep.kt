package com.team45.mysustainablecity.reps

import com.team45.mysustainablecity.data.classes.Moderation
import com.team45.mysustainablecity.data.classes.Post
import com.team45.mysustainablecity.data.remote.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class ModRep(
    private val client: SupabaseClient = SupabaseClientProvider.client
){
    private val _unapprovedPosts = MutableStateFlow<List<Post>>(emptyList())
    val unapprovedPosts: StateFlow<List<Post>> = _unapprovedPosts
    private val _moderationHistory = MutableStateFlow<List<Moderation>>(emptyList())
    val moderationHistory: StateFlow<List<Moderation>> = _moderationHistory


    suspend fun loadModerationHistoryFromModUser(modId: String) {
        val history = client
            .from("moderation")
            .select {
                filter {
                    eq("moderator_id", modId)
                }
            }
            .decodeList<Moderation>()

        _moderationHistory.value = history
    }


    suspend fun getModerationHistoryFromPost(postId: String): StateFlow<List<Moderation>>{
        val result = client
            .from("moderation")
            .select {
                filter { eq("post_id", postId) }
            }
            .decodeList<Moderation>()

        _moderationHistory.value = result
        return moderationHistory
    }

    suspend fun rejectPost(moderation: Moderation) {

        // Update post status
        client
            .from("post")
            .update(
                buildJsonObject {
                    put("status", "REJECTED")
                    put("is_approved", false)
                }
            ) {
                filter {
                    eq("post_id", moderation.postId)
                }
            }

        // Insert moderation record
        client
            .from("moderation")
            .insert(moderation)
    }

    suspend fun approvePost(moderation: Moderation) {

        // Update post approval
        client
            .from("post")
            .update(
                buildJsonObject {
                    put("is_approved", true)
                    put("status", "ACTIVE")
                }
            ) {
                filter {
                    eq("post_id", moderation.postId)
                }
            }

        // Insert moderation record
        client
            .from("moderation")
            .insert(moderation)
    }

    suspend fun getUnapprovedPosts(): StateFlow<List<Post>> {

        val posts = client
            .from("post")
            .select {
                filter {
                    eq("is_approved", false)
                }
            }
            .decodeList<Post>()

        _unapprovedPosts.value = posts

        return unapprovedPosts
    }
}