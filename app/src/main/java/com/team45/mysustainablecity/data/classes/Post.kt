package com.team45.mysustainablecity.data.classes

import com.google.android.gms.maps.model.LatLng
import com.team45.mysustainablecity.utils.Tag
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PostInfo(
    val post: PostDetails,
    @SerialName("like_count") val likeCount: Int,
    val tags: List<PostTag>,
    val images: List<Image>,
    val location: Location,
    val username: Username
)

@Serializable
data class PostDetails(
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

// Add SerialNames and link it to database please this is how the ui uses it
data class Post(
    val id: String,
    val username: String,
    val timeAgo: String,
    val title: String,
    val body: String,
    val likes: Int,
    val comments: Int,
    val tags: List<Tag> = emptyList(),
    val hasImage: Boolean = false,
    val imageUrl: String? = null,
    val position: LatLng? = null,
    val description: String = body,
)


@Serializable
data class PostTag (
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


fun PostInfo.toPost(): Post {
    return Post(
        id = post.postId,
        username = username.username,
        timeAgo = post.createdAt, // you could format this nicely later
        title = post.title,
        body = post.content,
        likes = likeCount,
        comments = 0, // not in PostInfo, add later if needed
        tags = tags.mapNotNull { postTag ->
            Tag.entries.find { it.name.equals(postTag.name, ignoreCase = true) }
        },
        hasImage = images.isNotEmpty(),
        imageUrl = images.firstOrNull()?.url,
        position = LatLng(location.latitude, location.longitude),
    )
}