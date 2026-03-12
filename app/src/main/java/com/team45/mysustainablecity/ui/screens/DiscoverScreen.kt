package com.team45.mysustainablecity.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.team45.mysustainablecity.Screen
import com.team45.mysustainablecity.reps.PostRep
import com.team45.mysustainablecity.ui.components.FilterPill
import com.team45.mysustainablecity.utils.Tag
import com.team45.mysustainablecity.viewmodel.AuthViewModel
import com.team45.mysustainablecity.viewmodel.DiscoverViewModel

@Composable
fun DiscoverScreen(
    authViewModel: AuthViewModel,
    padding: PaddingValues,
    navController: NavController,
    discoverViewModel: DiscoverViewModel
) {
    val activeFilters by discoverViewModel.activeFilters.collectAsState()
    val likedPosts by discoverViewModel.likedPosts.collectAsState()
    val likeCounts by discoverViewModel.likeCounts.collectAsState()

    val visiblePosts by remember(activeFilters) {
        derivedStateOf { discoverViewModel.visiblePosts(activeFilters) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = padding)
            .padding(16.dp)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp),
        ) {
            items(Tag.entries) { tag ->
                val isSelected = tag in activeFilters
                FilterPill(
                    text = tag.displayName,
                    isSelected = isSelected,
                    onClick = { discoverViewModel.toggleFilter(tag) },
                    shadow = false,
                    icon = {
                        Icon(
                            imageVector = tag.icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else Color(0xFF141414),
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    selectedColor = tag.color
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (visiblePosts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No posts match the selected filters.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(modifier = Modifier.padding(top = 4.dp)) {
                itemsIndexed(visiblePosts) { index, post ->
                    PostCard(
                        post = post,
                        isLiked = likedPosts[post.id] == true,
                        likeCount = likeCounts[post.id] ?: post.likes,
                        onLike = { discoverViewModel.toggleLike(post.id) },
                        onClick = {
                            navController.navigate(Screen.DiscoverPost.createRoute(post.id))
                        }
                    )

                    if (index < visiblePosts.lastIndex) {
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

@Composable
fun PostCard(
    post: Post,
    isLiked: Boolean,
    likeCount: Int,
    onLike: () -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        // Header row
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
            post.tags.forEach { tag ->
                Surface(
                    shape = RoundedCornerShape(50),
                    color = tag.color.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = tag.displayName,
                        fontSize = 10.sp,
                        color = tag.color,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = post.title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = post.body,
            fontSize = 13.sp,
            color = Color.DarkGray
        )

        if (post.hasImage) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(width = 160.dp, height = 110.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF88BB88)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📷 Image", color = Color.White, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action row
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Like
            Row(
                modifier = Modifier.clickable(onClick = onLike),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp,
                    contentDescription = "Like",
                    modifier = Modifier.size(16.dp),
                    tint = if (isLiked) Color(0xFF388E3C) else Color.Gray
                )
                Text(
                    text = likeCount.toString(),
                    fontSize = 12.sp,
                    color = if (isLiked) Color(0xFF388E3C) else Color.Gray
                )
            }

            // Comments
            Row(
                modifier = Modifier.clickable(onClick = onClick),
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
                modifier = Modifier.clickable {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, post.title)
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "${post.title}\n\n${post.body}\n\n— Shared via My Sustainable City"
                        )
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share post via…"))
                },
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