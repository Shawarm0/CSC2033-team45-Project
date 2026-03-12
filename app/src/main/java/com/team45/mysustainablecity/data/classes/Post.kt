package com.team45.mysustainablecity.data.classes

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PostInfo(
    val post: Post,
    @SerialName("like_count") val likeCount: Int,
    val tags: List<Tag>,
    val images: List<Image>,
    val location: Location,
    val username: Username

)

@Serializable
data class Post (
    @SerialName("post_id") val postId: String,
    @SerialName("author_id") val authorId: String,
    val title: String,
    val content: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("expires_at") val expiresAt: String?,
    @SerialName("updated_at") val updatedAt: String?,
    @SerialName("has_location") val hasLocation: Boolean,
    val status: String
)

@Serializable
data class Tag (
    val name: String
)

@Serializable
data class Image (
    val url: String,
    @SerialName("uploaded_at") val uploadedAt: Instant,
    val caption: String
)

@Serializable
data class Location (
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class Username (
    val username: String
)
