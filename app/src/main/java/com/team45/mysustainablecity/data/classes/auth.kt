package com.team45.mysustainablecity.data.classes

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@Serializable
data class User @OptIn(ExperimentalUuidApi::class) constructor(
    val userId: Uuid,
    val email: String,
    val role: String,
    val createdAt: Instant,
    val isActive: Boolean,
    val lastLoginAt: Instant? = null
)
