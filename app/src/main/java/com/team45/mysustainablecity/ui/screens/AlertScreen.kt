package com.team45.mysustainablecity.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

data class AlertItem(
    val message: String,
    val timeAgo: String
)
@Composable
fun AlertScreen(
    navController: NavController
) {
    val lastHourAlerts = listOf(
        AlertItem("user 1 liked your post", "5 mins ago"),
        AlertItem("user 2 commented on your post - \"This is sick...", "10 mins ago"),
        AlertItem("user 3 liked your post", "15 mins ago"),
        AlertItem("user 4 commented on your post - \"Yeah the po...", "6 mins ago"),
    )

    val last30DaysAlerts = listOf(
        AlertItem("user 1 liked your post", "5 mins ago"),
        AlertItem("user 2 commented on your post - \"This is sick...", "10 mins ago"),
        AlertItem("user 3 liked your post", "15 mins ago"),
        AlertItem("user 2 commented on your post - \"This is sick...", "10 mins ago"),
        AlertItem("user 4 commented on your post - \"Yeah the po...", "6 mins ago"),
        AlertItem("user 2 commented on your post - \"This is sick...", "10 mins ago"),
        AlertItem("user 3 liked your post", "15 mins ago"),
        AlertItem("user 3 liked your post", "15 mins ago"),
        AlertItem("user 4 commented on your post - \"Yeah the po...", "6 mins ago"),
    )


    Text("Alert Screen")
}