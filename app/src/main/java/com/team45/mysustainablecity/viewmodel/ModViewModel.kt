package com.team45.mysustainablecity.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team45.mysustainablecity.data.classes.Moderation
import com.team45.mysustainablecity.data.classes.Post
import com.team45.mysustainablecity.reps.ModRep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



class ModViewModel(
    private val modRep: ModRep
) : ViewModel() {

    private val _unapprovedPosts = MutableStateFlow<List<Post>>(emptyList())
    val unapprovedPosts: StateFlow<List<Post>> = _unapprovedPosts

    private val _moderationHistory = MutableStateFlow<List<Moderation>>(emptyList())
    val moderationHistory: StateFlow<List<Moderation>> = _moderationHistory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error


    init {
        observeUnapprovedPosts()
        observeModerationHistory()
    }


    /**
     * Observe unapproved posts
     */
    private fun observeUnapprovedPosts() {
        viewModelScope.launch {

            try {

                modRep.unapprovedPosts.collect { posts ->

                    _unapprovedPosts.value = posts

                    Log.d(
                        "ModViewModel",
                        "Unapproved posts updated: ${posts.size}"
                    )
                }

            } catch (e: Exception) {

                Log.e("ModViewModel", "Error observing posts: ${e.message}")
                _error.value = e.message
            }
        }
    }


    /**
     * Observe moderation history
     */
    private fun observeModerationHistory() {
        viewModelScope.launch {

            try {

                modRep.moderationHistory.collect { history ->

                    _moderationHistory.value = history

                    Log.d(
                        "ModViewModel",
                        "Moderation history updated: ${history.size}"
                    )
                }

            } catch (e: Exception) {

                Log.e("ModViewModel", "Error observing history: ${e.message}")
                _error.value = e.message
            }
        }
    }


    /**
     * Load unapproved posts
     */
    fun loadUnapprovedPosts() {
        viewModelScope.launch {

            _isLoading.value = true

            try {

                val posts = modRep.getUnapprovedPosts()
                Log.e("ModViewModel", "loadedUnapprovedPosts")

            } catch (e: Exception) {

                Log.e("ModViewModel", "Failed loading posts: ${e.message}")
                _error.value = e.message

            } finally {

                _isLoading.value = false
            }
        }
    }


    /**
     * Approve post
     */
    fun approvePost(moderation: Moderation) {
        viewModelScope.launch {

            try {

                modRep.approvePost(moderation)

                modRep.getUnapprovedPosts()

                Log.d("ModViewModel", "Post approved")

            } catch (e: Exception) {

                Log.e("ModViewModel", "Approve failed: ${e.message}")
                _error.value = e.message
            }
        }
    }


    /**
     * Reject post
     */
    fun rejectPost(moderation: Moderation) {
        viewModelScope.launch {

            try {

                modRep.rejectPost(moderation)

                // refresh list
                modRep.getUnapprovedPosts()

                Log.d("ModViewModel", "Post rejected")

            } catch (e: Exception) {

                Log.e("ModViewModel", "Reject failed: ${e.message}")
                _error.value = e.message
            }
        }
    }


    /**
     * Load moderation history for moderator
     */
    fun loadModerationHistoryFromModUser(modId: String) {
        viewModelScope.launch {

            _isLoading.value = true

            try {

                val mods = modRep.loadModerationHistoryFromModUser(modId)
                Log.e("ModViewModel", "loadedModHistoryByUserId:")


            } catch (e: Exception) {

                Log.e("ModViewModel", "Failed loading mod history: ${e.message}")
                _error.value = e.message

            } finally {

                _isLoading.value = false
            }
        }
    }


    /**
     * Load moderation history for post
     */
    fun loadModerationHistoryFromPost(postId: String) {
        viewModelScope.launch {

            _isLoading.value = true

            try {

                val mods = modRep.getModerationHistoryFromPost(postId)
                Log.e("ModViewModel", "loadedModHistoryByPostId:")


            } catch (e: Exception) {

                Log.e("ModViewModel", "Failed loading post history: ${e.message}")
                _error.value = e.message

            } finally {

                _isLoading.value = false
            }
        }
    }
}