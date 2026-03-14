package com.team45.mysustainablecity.data.classes

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName

@Serializable
data class Moderation(

    @SerialName("action_id")
    val actionId: String? = null, // PK

    @SerialName("post_id")
    val postId: String, // FK

    @SerialName("moderator_id")
    val moderatorId: String, // FK

    val action: String,
    val reason: String,

    @SerialName("action_at")
    val actionAt: Instant?
)