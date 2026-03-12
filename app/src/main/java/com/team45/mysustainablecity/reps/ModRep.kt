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
            .from("moderation_actions")
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
            .from("moderation_actions")
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
            .from("posts")
            .update(
                buildJsonObject {
                    put("status", "rejected")
                }
            ) {
                filter {
                    eq("post_id", moderation.postId)
                }
            }

        // Insert moderation record
        client
            .from("moderation_actions")
            .insert(
                buildJsonObject {
                    put("post_id", moderation.postId)
                    put("moderator_id", moderation.moderatorId)
                    put("action", moderation.action)
                    put("reason", moderation.reason)
                }
            )
    }

    suspend fun approvePost(moderation: Moderation) {

        // Update post approval
        client
            .from("posts")
            .update(
                buildJsonObject {
                    put("status", "approved")
                }
            ) {
                filter {
                    eq("post_id", moderation.postId)
                }
            }

        // Insert moderation record
        client
            .from("moderation_actions")
            .insert(
                buildJsonObject {
                    put("post_id", moderation.postId)
                    put("moderator_id", moderation.moderatorId)
                    put("action", moderation.action)
                    put("reason", moderation.reason)
                }
            )
    }

    suspend fun getUnapprovedPosts(): StateFlow<List<Post>> {

        val posts = client
            .from("posts")
            .select {
                filter {
                    eq("status", "awaiting approval")
                }
            }
            .decodeList<Post>()

        _unapprovedPosts.value = posts

        return unapprovedPosts
    }
}