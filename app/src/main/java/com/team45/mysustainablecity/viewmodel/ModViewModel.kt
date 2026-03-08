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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error


    /**
     * Load unapproved posts
     */
    fun loadUnapprovedPosts() {
        viewModelScope.launch {

            _isLoading.value = true

            try {

                val postsFlow = modRep.getUnapprovedPosts()

                postsFlow.collect { posts ->
                    _unapprovedPosts.value = posts
                    Log.d("ModViewModel", "Unapproved Posts Refreshed ${posts.toString()}")

                }

            } catch (e: Exception) {

                Log.e("ModViewModel", "Failed loading posts: ${e.message}")
                _error.value = e.message

            } finally {
                _isLoading.value = false
            }
        }
    }


    /**
     * Approve a post
     */
    fun approvePost(moderation: Moderation) {
        viewModelScope.launch {

            try {

                modRep.approvePost(moderation)

                // refresh list after approval
                loadUnapprovedPosts()
                Log.d("ModViewModel", "Post Approved")


            } catch (e: Exception) {

                Log.e("ModViewModel", "Approve failed: ${e.message}")
                _error.value = e.message
            }
        }
    }


    /**
     * Reject a post
     */
    fun rejectPost(moderation: Moderation) {
        viewModelScope.launch {

            try {

                modRep.rejectPost(moderation)

                // refresh list after rejection
                loadUnapprovedPosts()
                Log.d("ModViewModel", "Post Rejected")

            } catch (e: Exception) {

                Log.e("ModViewModel", "Reject failed: ${e.message}")
                _error.value = e.message
            }
        }
    }
}