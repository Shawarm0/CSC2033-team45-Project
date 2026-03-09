package com.team45.mysustainablecity.utils

import android.graphics.drawable.Icon
import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import kotlin.math.*

// ---------------------------------------------------------------------------
// Data models
// ---------------------------------------------------------------------------

enum class Tag {
    GREEN_SPACE,
    ELECTRIC_CHARGER,
    BIKE_RACK,
    ISSUE,
    TEMPORARY,
    AWAITING_APPROVAL,
    APPROVED;

    val displayName: String get() = when (this) {
        TEMPORARY         -> "Temporary"
        AWAITING_APPROVAL -> "Awaiting Approval"
        APPROVED          -> "Approved"
        ELECTRIC_CHARGER  -> "Electric Charger"
        BIKE_RACK         -> "Bike Rack"
        GREEN_SPACE       -> "Green Space"
        ISSUE             -> "Issue"
    }

    val color: Color get() = when (this) {
        TEMPORARY         -> Color(0xFFF57C00)
        AWAITING_APPROVAL -> Color(0xFF9C27B0)
        APPROVED          -> Color(0xFF388E3C)
        ELECTRIC_CHARGER  -> Color(0xFF1976D2)
        BIKE_RACK         -> Color(0xFF00897B)
        GREEN_SPACE       -> Color(0xFF1E8449)
        ISSUE             -> Color(0xFFE53935)
    }

    val icon: ImageVector get() = when (this) {
        TEMPORARY         -> Icons.Default.Schedule
        AWAITING_APPROVAL -> Icons.Default.HourglassEmpty
        APPROVED          -> Icons.Default.CheckCircle
        ELECTRIC_CHARGER  -> Icons.Default.EvStation
        BIKE_RACK         -> Icons.AutoMirrored.Filled.DirectionsBike
        GREEN_SPACE       -> Icons.Default.Park
        ISSUE             -> Icons.Default.Warning
    }

}
data class MapLocation(
    val name: String,
    val position: LatLng,
    val tags: List<Tag> = emptyList(),
    val description: String,
    val imageRes: Int? = null
) {
    val primaryTag: Tag? get() = tags.firstOrNull()
    val color: Color get() = primaryTag?.color ?: Color(0xFF2196F3)
    val icon: ImageVector get() = primaryTag?.icon ?: Icons.Default.Place

    // Returns the color based on which filter is active
    fun colorForFilter(activeFilters: Set<Tag>): Color {
        if (activeFilters.isEmpty()) return color
        val matchingTag = tags.firstOrNull { it in activeFilters }
        return matchingTag?.color ?: color
    }
}

/** A cluster groups one or more MapLocations that are close together. */
data class LocationCluster(
    val locations: List<MapLocation>,
    val centroid: LatLng
) {
    val isSingle: Boolean get() = locations.size == 1
    val single: MapLocation get() = locations.first()

    fun color(activeFilters: Set<Tag>): Color {
        return if (isSingle) single.colorForFilter(activeFilters) else Color(0xFF546E7A)
    }
}

// ---------------------------------------------------------------------------
// Clustering algorithm
// ---------------------------------------------------------------------------

/**
 * Groups [locations] into clusters so that no two locations in the same cluster
 * are farther than [thresholdMeters] apart (greedy single-linkage).
 */
fun clusterLocations(
    locations: List<MapLocation>,
    thresholdMeters: Double = 300.0
): List<LocationCluster> {
    val assigned = BooleanArray(locations.size) { false }
    val clusters = mutableListOf<LocationCluster>()

    for (i in locations.indices) {
        if (assigned[i]) continue
        val group = mutableListOf(locations[i])
        assigned[i] = true

        for (j in (i + 1) until locations.size) {
            if (assigned[j]) continue
            if (group.any { haversineMeters(it.position, locations[j].position) <= thresholdMeters }) {
                group.add(locations[j])
                assigned[j] = true
            }
        }

        clusters.add(LocationCluster(
            locations = group,
            centroid = centroidOf(group.map { it.position }),
        ))
    }

    return clusters
}

private fun haversineMeters(a: LatLng, b: LatLng): Double {
    val r = 6_371_000.0
    val dLat = Math.toRadians(b.latitude - a.latitude)
    val dLon = Math.toRadians(b.longitude - a.longitude)
    val lat1 = Math.toRadians(a.latitude)
    val lat2 = Math.toRadians(b.latitude)
    val h = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
    return 2 * r * asin(sqrt(h))
}

private fun centroidOf(positions: List<LatLng>): LatLng {
    val lat = positions.map { it.latitude }.average()
    val lng = positions.map { it.longitude }.average()
    return LatLng(lat, lng)
}

// ---------------------------------------------------------------------------
// Composables — single location bottom sheet (unchanged API)
// ---------------------------------------------------------------------------

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

            // Show all tags below the name
            if (location.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    location.tags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = tag.color.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = tag.displayName,
                                fontSize = 11.sp,
                                color = tag.color,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
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

// ---------------------------------------------------------------------------
// Composables — cluster bottom sheet (NEW)
// ---------------------------------------------------------------------------

/**
 * Bottom sheet displayed when a cluster of multiple locations is tapped.
 * Shows each location with its image (if any), name, description and a nav button,
 * separated by dividers.
 */
@Composable
fun ClusterBottomSheet(
    cluster: LocationCluster,
    navController: NavController,
    onDismiss: () -> Unit
) {
    // Group locations by tag, nulls get their own group at the end
    val grouped: Map<Tag?, List<MapLocation>> = cluster.locations
        .groupBy { it.primaryTag }
        .toSortedMap(compareBy { it?.displayName ?: "zzz" })

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "${cluster.locations.size} Nearby Posts",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        grouped.entries.forEachIndexed { groupIndex, (tag, tagLocations) ->

            // Tag header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(tag?.color ?: Color(0xFF9E9E9E))
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = tag?.displayName ?: "Untagged",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = tag?.color ?: Color(0xFF9E9E9E),
                    letterSpacing = 0.8.sp
                )
            }

            // Locations under this tag
            tagLocations.forEachIndexed { index, location ->
                ClusterLocationRow(
                    location = location,
                    navController = navController,
                    onDismiss = onDismiss
                )
                if (index < tagLocations.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 1.dp,
                        color = Color(0xFFEEEEEE)
                    )
                }
            }

            // Divider between tag groups (thicker/darker)
            if (groupIndex < grouped.entries.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    thickness = 2.dp,
                    color = Color(0xFFDDDDDD)
                )
            }
        }
    }
}

@Composable
private fun ClusterLocationRow(
    location: MapLocation,
    navController: NavController,
    onDismiss: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Optional image
        location.imageRes?.let { imageRes ->
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = location.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Name row with color dot + nav button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(location.color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = location.name,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            Surface(
                modifier = Modifier.size(28.dp),
                shape = CircleShape,
                color = location.color,
                shadowElevation = 3.dp
            ) {
                IconButton(onClick = {
                    onDismiss()
                    navController.navigate(Screen.DiscoverPost.createRoute(location.name))
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go to ${location.name}",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        Text(
            text = location.description,
            fontSize = 14.sp,
            color = Color.Gray,
            lineHeight = 20.sp
        )
    }
}

// ---------------------------------------------------------------------------
// Map marker composables
// ---------------------------------------------------------------------------

@Composable
fun CustomMapMarker(
    location: MapLocation,
    cameraPositionState: CameraPositionState,
    isSelected: Boolean,
    activeFilters: Set<Tag> = emptySet()
) {
    val zoom by remember { derivedStateOf { cameraPositionState.position.zoom } }
    val color = if (isSelected) Color(0xFFE53935) else location.colorForFilter(activeFilters)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.width(IntrinsicSize.Max)
    ) {
        Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
            CustomPin(color = color, icon = location.icon)
        }
        if (zoom > 15f) {
            Spacer(Modifier.width(6.dp))
            Text(text = location.name, color = color, fontWeight = FontWeight.Medium)
        } else {
            Spacer(Modifier.width(0.dp))
        }
    }
}

@Composable
fun ClusterMapMarker(
    cluster: LocationCluster,
    cameraPositionState: CameraPositionState,
    isSelected: Boolean,
    activeFilters: Set<Tag> = emptySet()
) {
    val zoom by remember { derivedStateOf { cameraPositionState.position.zoom } }
    val pinColor = if (isSelected) Color(0xFFE53935) else cluster.color(activeFilters)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.width(IntrinsicSize.Max)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(pinColor)
                    .border(width = 3.dp, color = Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cluster.locations.size.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
        if (zoom > 15f) {
            Spacer(Modifier.width(6.dp))
            Text(text = "${cluster.locations.size} posts", color = pinColor, fontWeight = FontWeight.Medium)
        } else {
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
            .border(width = 3.dp, color = Color.White, shape = CircleShape),
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