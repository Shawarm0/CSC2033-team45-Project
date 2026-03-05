package com.team45.mysustainablecity.data.classes

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Post(

    @SerialName("post_id")
    val postId: String? = null,

    @SerialName("author_id")
    val authorId: String,

    val title: String,
    val content: String,
    val status: String,

    @SerialName("created_at")
    val createdAt: Instant? = null,

    @SerialName("expires_at")
    val expiresAt: Instant? = null,

    @SerialName("updated_at")
    val updatedAt: Instant? = null,

    @SerialName("is_approved")
    val isApproved: Boolean
)
