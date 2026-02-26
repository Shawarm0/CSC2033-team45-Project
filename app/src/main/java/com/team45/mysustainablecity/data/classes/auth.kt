package com.team45.mysustainablecity.data.classes

import io.github.jan.supabase.auth.OtpType
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import java.util.Date
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@Serializable
data class User @OptIn(ExperimentalUuidApi::class) constructor(
    val userId: Uuid,
    val email: String,
    val passwordHash: String,
    val role: String,
    val createdAt: LocalDateTime,
    val isActive: Boolean,
    val lastLoginAt: LocalDateTime?
)
