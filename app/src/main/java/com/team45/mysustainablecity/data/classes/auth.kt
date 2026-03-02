package com.team45.mysustainablecity.data.classes

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi


@Serializable
data class User @OptIn(ExperimentalUuidApi::class) constructor(
    val userID: String,
    val email: String,
    val role: String,
    val createdAt: Instant,
    val isActive: Boolean,
    val passwordHash: String,
    val lastLoginAt: Instant? = null
)
