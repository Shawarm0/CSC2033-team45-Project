package com.team45.mysustainablecity.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.team45.mysustainablecity.ui.theme.BottomBarColor

data class AlertItem(
    val message: String,
    val timeAgo: String
)
@Composable
fun AlertsScreen(
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF1C1C1C)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {



            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF5EE))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    AlertSectionHeader(title = "Last Hour")
                    Spacer(modifier = Modifier.height(8.dp))
                    lastHourAlerts.forEach { alert ->
                        AlertRow(alert = alert)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    AlertSectionHeader(title = "Last 30 days")
                    Spacer(modifier = Modifier.height(8.dp))
                    last30DaysAlerts.forEach { alert ->
                        AlertRow(alert = alert)
                    }
                }
                }
            }
        }
    }

@Composable
fun AlertSectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Color.Black
    )
}

@Composable
fun AlertRow(alert: AlertItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "User avatar",
            modifier = Modifier.size(32.dp),
            tint = Color.Black
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = alert.message,
            fontSize = 13.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = alert.timeAgo,
            fontSize = 11.sp,
            color = Color.Gray
        )

    }
}

@Composable
@Preview
fun AlertsPreview() {
    AlertsScreen(navController = rememberNavController())
}





