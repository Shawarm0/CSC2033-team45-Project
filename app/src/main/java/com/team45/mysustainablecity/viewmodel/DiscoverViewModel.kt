package com.team45.mysustainablecity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.team45.mysustainablecity.data.classes.Post
import com.team45.mysustainablecity.reps.PostRep
import com.team45.mysustainablecity.utils.Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DiscoverViewModel(private val postRep: PostRep) : ViewModel() {

    private val _activeFilters = MutableStateFlow<Set<Tag>>(emptySet())
    val activeFilters: StateFlow<Set<Tag>> = _activeFilters.asStateFlow()

    fun toggleFilter(tag: Tag) {
        _activeFilters.value = if (tag in _activeFilters.value) {
            _activeFilters.value - tag
        } else {
            _activeFilters.value + tag
        }
    }


    /**
     * Returns the filtered list of [Post]s for the feed.
     * Call inside `derivedStateOf` in the composable so it only recomputes on filter changes.
     */
    fun visiblePosts(activeFilters: Set<Tag> = _activeFilters.value): List<Post> {
        return if (activeFilters.isEmpty()) {
            postRep.uiPosts.value
        } else {
            postRep.uiPosts.value.filter { post ->
                activeFilters.all { it in post.tags }
            }
        }
    }


    // Just tracks +1 or -1 adjustments made by the user
    private val _likeAdjustments = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val _likedPosts = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    val likedPosts: StateFlow<Map<String, Boolean>> = _likedPosts.asStateFlow()

    fun getLikeCount(post: Post): Int {
        return post.likes + (_likeAdjustments.value[post.id] ?: 0)
    }

    fun toggleLike(postId: String) {
        val currentlyLiked = _likedPosts.value[postId] == true
        _likedPosts.value += (postId to !currentlyLiked)
        val currentAdjustment = _likeAdjustments.value[postId] ?: 0
        _likeAdjustments.value += (postId to currentAdjustment + if (currentlyLiked) -1 else 1)
    }

}