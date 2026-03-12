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

    // -------------------------------------------------------------------------
    // Filters
    // -------------------------------------------------------------------------

    private val _activeFilters = MutableStateFlow<Set<Tag>>(emptySet())
    val activeFilters: StateFlow<Set<Tag>> = _activeFilters.asStateFlow()

    fun toggleFilter(tag: Tag) {
        _activeFilters.value = if (tag in _activeFilters.value) {
            _activeFilters.value - tag
        } else {
            _activeFilters.value + tag
        }
    }

    // -------------------------------------------------------------------------
    // Visible posts — derived from filters
    // -------------------------------------------------------------------------

    /**
     * Returns the filtered list of [Post]s for the feed.
     * Call inside `derivedStateOf` in the composable so it only recomputes on filter changes.
     */
    fun visiblePosts(activeFilters: Set<Tag> = _activeFilters.value): List<Post> {
        return if (activeFilters.isEmpty()) {
            postRep.allPosts
        } else {
            postRep.allPosts.filter { post ->
                activeFilters.all { it in post.tags }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Like state
    // -------------------------------------------------------------------------

    // Keyed by post id — tracks which posts the current user has liked
    private val _likedPosts = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val likedPosts: StateFlow<Map<String, Boolean>> = _likedPosts.asStateFlow()

    // Keyed by post id — mutable like counts (seeded from the post data)
    private val _likeCounts = MutableStateFlow<Map<String, Int>>(
        postRep.allPosts.associate { it.id to it.likes }
    )
    val likeCounts: StateFlow<Map<String, Int>> = _likeCounts.asStateFlow()

    fun toggleLike(postId: String) {
        val currentlyLiked = _likedPosts.value[postId] == true
        _likedPosts.value = _likedPosts.value.toMutableMap().apply {
            put(postId, !currentlyLiked)
        }
        val currentCount = _likeCounts.value[postId]
            ?: postRep.allPosts.find { it.id == postId }?.likes
            ?: 0
        _likeCounts.value = _likeCounts.value.toMutableMap().apply {
            put(postId, currentCount + if (currentlyLiked) -1 else 1)
        }
    }

}