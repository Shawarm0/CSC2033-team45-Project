package com.team45.mysustainablecity.data.classes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val createdAt: String? = null
)