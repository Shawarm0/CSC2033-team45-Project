package com.team45.mysustainablecity.data.remote

import android.util.Log
import com.team45.mysustainablecity.data.classes.Channel
import com.team45.mysustainablecity.data.remote.SupabaseClientProvider.client
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

/**
 * Manages Supabase realtime channels for different data streams.
 *
 * Provides subscription and unsubscription functionality for:
 * - Students data channel ("students-listener")
 * - History data channel ("history-listener")
 *
 * Uses a shared [CoroutineScope] for managing channel coroutines.
 */
object ChannelManager {
    // Shared CoroutineScope for hosting flows and channel subscriptions.
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    // Channel listening for student-related realtime updates.
    private val postsChannel = Channel(client.channel("posts-listener"), "posts", managerScope)

    /**
     * Subscribes to the students channel.
     *
     * Before subscribing, unsubscribes from all other channels to avoid multiple active subscriptions.
     * Adds a small delay before subscribing to help with connection stability.
     *
     * @return A [Flow] emitting realtime [PostgresAction] events from the students channel.
     */
    suspend fun subscribeToPostsChannel(): Flow<PostgresAction> {
        postsChannel.subscribe()
        Log.i("ChannelManager", "Attempting to connect to posts channel")
        return postsChannel.getFlow()
    }

    /**
     * Unsubscribes from all active channels (students and history).
     *
     * Useful to clean up active connections before opening new ones.
     */
    suspend fun unsubscribeFromAllChannels() {
        Log.i("Database-ChannelManager", "Unsubscribed from all channels")
    }
}