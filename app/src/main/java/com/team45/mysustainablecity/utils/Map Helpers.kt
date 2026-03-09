package com.team45.mysustainablecity.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.team45.mysustainablecity.Screen

@Composable
fun LocationBottomSheet(
    location: MapLocation,
    navController: NavController,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            location.imageRes?.let { imageRes ->
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = location.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(location.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = location.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    modifier = Modifier.size(30.dp),
                    shape = CircleShape,
                    color = location.color,
                    shadowElevation = 4.dp
                ) {
                    IconButton(onClick = {
                        onDismiss()
                        navController.navigate(Screen.DiscoverPost.createRoute(location.name))
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Go to location",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = location.description,
                fontSize = 15.sp,
                color = Color.Gray,
                lineHeight = 22.sp
            )
        }

    }
}

@Composable
fun CustomMapMarker(
    location: MapLocation,
    cameraPositionState: CameraPositionState,
    isSelected: Boolean
) {
    val zoom by remember {
        derivedStateOf { cameraPositionState.position.zoom }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.width(IntrinsicSize.Max) // keeps row stable
    ) {
        Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
            CustomPin(
                color = if (isSelected) Color(0xFFE53935) else location.color,
                icon = location.icon
            )
        }

        if (zoom > 15f) {
            Spacer(Modifier.width(6.dp))
            Text(
                text = location.name,
                color = if (isSelected) Color(0xFFE53935) else location.color,
                fontWeight = FontWeight.Medium
            )
        } else {
            // Invisible placeholder to keep the pin anchored
            Spacer(Modifier.width(0.dp))
        }
    }
}

@Composable
fun CustomPin(
    color: Color,
    icon: ImageVector
) {

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color)
            .border(width= 3.dp, color = Color.White, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
    }
}

data class MapLocation(
    val name: String,
    val position: LatLng,
    val color: Color,
    val icon: ImageVector,
    val description: String,
    val imageRes: Int? = null
)