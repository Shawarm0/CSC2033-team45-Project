package com.team45.mysustainablecity.data.classes

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi


@Serializable
data class User @OptIn(ExperimentalUuidApi::class) constructor(
    @SerialName("user_id") val userID: String,
    val email: String,
    val role: String,
    @SerialName("created_at") val createdAt: Instant,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("last_login_at") val lastLoginAt: Instant? = null
)
