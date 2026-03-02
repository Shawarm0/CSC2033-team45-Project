package com.team45.mysustainablecity.reps

import com.team45.mysustainablecity.data.remote.SupabaseClientProvider
import kotlinx.coroutines.flow.StateFlow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ModRep {
    private val client = SupabaseClientProvider.client

    suspend fun makeModerator(userId: Uuid) {
        TODO()
    }

    suspend fun deactivateModUser(userId: Uuid, moderator: Uuid) {
        TODO()
    }

    fun getModerationHistoryFromModUser(modId: Int): StateFlow<List<Moderation>> {
        TODO("Waiting on Moderation data class to be created")
    }

    fun getModerationHistoryFromPost(postId: Int): StateFlow<List<Moderation>> {
        TODO("Waiting on Moderation data class to be created")
    }

    suspend fun rejectPost(moderation: Moderation) {
        TODO("Waiting on Moderation data class to be created")
    }

    suspend fun approvePost(moderation: Moderation) {
        TODO("Waiting on Moderation data class to be created")
    }

    fun getUnapprovedPosts(): StateFlow<List<Post>> {
        TODO("Waiting on Post data class to be created")
    }
}