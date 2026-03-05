package com.team45.mysustainablecity.data.classes

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class Moderation (
    val actionId: String? = null,// PK
    val postId: String,// FK
    val moderatorId: String,// FK
    val action: String,
    val reason: String,
    val actionAt: Instant
)