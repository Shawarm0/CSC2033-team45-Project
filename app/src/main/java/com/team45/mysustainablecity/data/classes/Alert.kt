package com.team45.mysustainablecity.data.classes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class Alert(

    @SerialName("alert_id")
    val alertId: String? = null,

    @SerialName("user_id")
    val userId: String,

    val title: String,

    val message: String,

    val type: String = "info",

    @SerialName("is_read")
    val isRead: Boolean = false,

    @SerialName("created_at")
    val createdAt: Instant? = null,

    @SerialName("post_id")
    val postId: String? = null,

    @SerialName("comment_id")
    val commentId: String? = null,


)