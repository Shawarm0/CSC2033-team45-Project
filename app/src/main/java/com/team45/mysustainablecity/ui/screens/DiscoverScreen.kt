package com.team45.mysustainablecity.ui.screens

import android.annotation.SuppressLint
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.team45.mysustainablecity.viewmodel.AuthViewModel

// Colours
val DarkBackground = Color(0xFF1C1C1C)
val CardBackground = Color(0xFFEEF5EE)
val Green    = Color(0xFF2E7D32)
val FilterChipColor = Color(0xFFDDEEDD)

// Data class for post
data class DiscoverPost(
    val username: String,
    val timeAgo: String,
    val title: String,
    val body: String,
    val likes: Int,
    val comments: Int,
    val tag: String? = null,
    val hasImage: Boolean = false
)

// Main screen
@Composable
fun DiscoverScreen(authViewModel: AuthViewModel, padding: PaddingValues, navController: NavController) {

    val filterChips = listOf("All", "Parks", "Electric Chargers", "Issues", "Filter 1", "Filter 2")
    var selectedFilter by remember { mutableStateOf("All") }

    val posts = listOf(
        DiscoverPost(
            username = "username",
            timeAgo = "1 hr. ago",
            title = "Such a beautiful park",
            body = "Park near estate A. beautiful place to take your kids me and the kids went there last night and it was the most fun we had",
            likes = 400,
            comments = 239,
            hasImage = true
        ),
        DiscoverPost(
            username = "username",
            timeAgo = "2 hr. ago",
            title = "New Charger",
            body = "The council has put in a new charger for the EV folks!!",
            likes = 234,
            comments = 10
        ),
        DiscoverPost(
            username = "username",
            timeAgo = "2 hr. ago",
            title = "Pot Holes",
            body = "Can someone please go ahead and fix the pot holes",
            likes = 1030,
            comments = 576,
            tag = "Issue"
        ),
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = "Discover Screen",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterChips.forEach { chip ->
                    FilterChipItem(
                        label = chip,
                        selected = chip == selectedFilter,
                        onClick = { selectedFilter = chip }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    posts.forEachIndexed { index, post ->
                        PostCard(post = post)

                        // Divider
                        if (index < posts.lastIndex) {
                            HorizontalDivider(
                                color = Color(0xFFCCDDCC),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChipItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),   // pill shape
        color = if (selected) Green else FilterChipColor,
        border = BorderStroke(1.dp, if (selected) Green else Color.LightGray)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            fontSize = 13.sp,
            color = if (selected) Color.White else Color.Black,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun PostCard(post: DiscoverPost) {
    Column(modifier = Modifier.fillMaxWidth()) {

        // Header with icon, username, time and optional tag
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar",
                modifier = Modifier.size(28.dp),
                tint = Color.Black
            )
            Text(
                text = post.username,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
            Text(
                text = "• ${post.timeAgo}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            // Optional tag
            if (post.tag != null) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFFFCDD2)   // light red for bq
                ) {
                    Text(
                        text = post.tag,
                        fontSize = 11.sp,
                        color = Color(0xFFB71C1C),  // dark red for text
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Post title
        Text(
            text = post.title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Post body
        Text(
            text = post.body,
            fontSize = 13.sp,
            color = Color.DarkGray
        )

        // Image
        if (post.hasImage) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(width = 160.dp, height = 110.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF88BB88)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📷 Image",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Footer
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Likes
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Likes",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Text(text = "${post.likes}", fontSize = 12.sp, color = Color.Gray)
            }

            // Comments
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Comments",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Text(text = "${post.comments}", fontSize = 12.sp, color = Color.Gray)
            }

            // Share
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Text(text = "Share", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview
fun DiscoverPreview() {
    Text("Discover Screen Preview")
}