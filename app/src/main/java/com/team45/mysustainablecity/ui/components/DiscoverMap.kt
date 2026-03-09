package com.team45.mysustainablecity.ui.components

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import com.team45.mysustainablecity.R
import com.team45.mysustainablecity.utils.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import com.team45.mysustainablecity.ui.screens.ProfileScreen
import com.team45.mysustainablecity.ui.theme.BottomBarColor

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun DiscoverMap(
    navController: NavController,
    paddingValues: PaddingValues
) {

    var text by remember { mutableStateOf("") }
    var activeFilters by remember { mutableStateOf<Set<Tag>>(emptySet()) }
    var isSearchCommitted by remember { mutableStateOf(false) }

    // Derived: are we actively searching?
    val isSearching = text.isNotBlank()
    val showProfile = remember { mutableStateOf(false) }
    val profileSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    // The locations to show on the map:
    // - If searching and committed (Enter pressed): search results, filters ignored
    // - If searching and not committed (typing): search results, filters ignored
    // - If not searching: apply filters (or all if no filters)
    val visibleLocations by remember(text, activeFilters, isSearchCommitted) {
        derivedStateOf {
            when {
                isSearching -> locations.filter {
                    it.name.contains(text.trim(), ignoreCase = true)
                }
                activeFilters.isEmpty() -> locations
                else -> locations.filter { location ->
                    location.tags.any { it in activeFilters }
                }
            }
        }
    }

    // Top search result — camera moves here
    val topResult by remember(visibleLocations, isSearching) {
        derivedStateOf { if (isSearching) visibleLocations.firstOrNull() else null }
    }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val newcastle = LatLng(54.9783, -1.6178)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(newcastle, 13f)
    }

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val zoom by remember { derivedStateOf { cameraPositionState.position.zoom } }

    val clusters by remember(zoom, visibleLocations) {
        derivedStateOf {
            val thresholdMeters = when {
                zoom >= 16f -> 80.0
                zoom >= 15f -> 150.0
                zoom >= 14f -> 250.0
                else        -> 400.0
            }
            clusterLocations(visibleLocations, thresholdMeters)
        }
    }

    // Move camera to top result as user types
    LaunchedEffect(topResult) {
        topResult?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(it.position, 16f)
            )
        }
    }

    var selectedCluster by remember { mutableStateOf<LocationCluster?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) { locationPermissionState.launchPermissionRequest() }

    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
                        )
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = locationPermissionState.status.isGranted,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            ),
            onMapClick = { selectedCluster = null }
        ) {
            if (zoom > 14f) {
                clusters.forEach { cluster ->
                    val isSelected = selectedCluster == cluster
                    MarkerComposable(
                        keys = arrayOf(cluster.centroid, zoom, isSelected),
                        state = MarkerState(position = cluster.centroid),
                        onClick = {
                            selectedCluster = cluster
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(cluster.centroid, 16f)
                                )
                            }
                            true
                        }
                    ) {
                        if (cluster.isSingle) {
                            CustomMapMarker(
                                location = cluster.single,
                                cameraPositionState = cameraPositionState,
                                isSelected = isSelected
                            )
                        } else {
                            ClusterMapMarker(
                                cluster = cluster,
                                cameraPositionState = cameraPositionState,
                                isSelected = isSelected
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = paddingValues.calculateTopPadding(), start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomTextField(
                modifier = Modifier,
                value = text,
                onValueChange = {
                    text = it
                    // If user edits after committing, un-commit so filters are still ignored while typing
                    if (isSearchCommitted) isSearchCommitted = false
                },
                placeholder = "Search",
                clearButton = true,
                shadow = true,
                // Clear button resets search entirely → filters resume
                onClear = {
                    text = ""
                    isSearchCommitted = false
                    selectedCluster = null
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        // Commit the search — filters stay ignored, keyboard dismissed
                        isSearchCommitted = true
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        modifier = Modifier.width(30.dp).height(20.dp),
                        contentDescription = null
                    )
                },
                trailingContent = {
                    if (!isSearching) {
                        IconButton(
                            onClick = {
                                showProfile.value = true
                            },
                            modifier = Modifier.width(30.dp).height(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                modifier = Modifier.width(30.dp).height(20.dp),
                                contentDescription = null
                            )
                        }
                    }
                }
            )

            // Hide filter pills while search is active
            if (!isSearching) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(Tag.entries) { tag ->
                        val isSelected = tag in activeFilters
                        FilterPill(
                            text = tag.displayName,
                            isSelected = isSelected,
                            onClick = {
                                activeFilters = if (isSelected) activeFilters - tag else activeFilters + tag
                                selectedCluster = null
                            },
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
            }
        }

        MapControlButton(
            icon = Icons.Default.MyLocation,
            modifier = Modifier
                .padding(end = 16.dp, bottom = paddingValues.calculateBottomPadding() + 16.dp)
                .align(Alignment.BottomEnd)
        ) {
            if (locationPermissionState.status.isGranted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
                            )
                        }
                    }
                }
            } else {
                coroutineScope.launch {
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(newcastle, 16f))
                }
            }
        }
    }

    selectedCluster?.let { cluster ->
        ModalBottomSheet(
            onDismissRequest = { selectedCluster = null },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            if (cluster.isSingle) {
                LocationBottomSheet(
                    location = cluster.single,
                    navController = navController,
                    onDismiss = { selectedCluster = null }
                )
            } else {
                ClusterBottomSheet(
                    cluster = cluster,
                    navController = navController,
                    onDismiss = { selectedCluster = null }
                )
            }
        }
    }

    if (showProfile.value) {
        ModalBottomSheet(
            onDismissRequest = { showProfile.value = false },
            sheetState = profileSheetState,
            containerColor = BottomBarColor,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            contentWindowInsets = { WindowInsets.statusBars },
            modifier = Modifier.fillMaxHeight()
        ) {
            ProfileScreen(navController)
        }
    }
}

@Composable
fun MapControlButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.size(60.dp),
        shape = CircleShape,
        shadowElevation = 6.dp,
        color = Color.White
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                modifier = Modifier.size(30.dp),
                contentDescription = null,
                tint = Color.Black
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Location data (unchanged)
// ---------------------------------------------------------------------------

val locations = listOf(

    // Quayside Bike Racks — type: BIKE_RACK, status varies
    MapLocation(
        name = "Quayside Bike Rack A",
        position = LatLng(54.9686, -1.6094),
        tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
        description = "Bike rack near the Quayside restaurants, 12 spaces available.",
        imageRes = null
    ),
    MapLocation(
        name = "Quayside Bike Rack B",
        position = LatLng(54.9688, -1.6088),
        tags = listOf(Tag.BIKE_RACK, Tag.AWAITING_APPROVAL),
        description = "Bike rack outside the Pitcher & Piano, 8 spaces. Pending council sign-off.",
        imageRes = null
    ),
    MapLocation(
        name = "Quayside Bike Rack C",
        position = LatLng(54.9684, -1.6099),
        tags = listOf(Tag.BIKE_RACK, Tag.TEMPORARY),
        description = "Temporary covered bike rack near the law courts, 20 spaces. Installed for summer.",
        imageRes = null
    ),

    // Quayside EV Chargers — type: ELECTRIC_CHARGER, status varies
    MapLocation(
        name = "Quayside EV Charger 1",
        position = LatLng(54.9685, -1.6091),
        tags = listOf(Tag.ELECTRIC_CHARGER, Tag.APPROVED),
        description = "Fast EV charging point, 2 bays, 50kW.",
        imageRes = null
    ),
    MapLocation(
        name = "Quayside EV Charger 2",
        position = LatLng(54.9683, -1.6096),
        tags = listOf(Tag.ELECTRIC_CHARGER, Tag.AWAITING_APPROVAL),
        description = "Proposed standard EV charging point, 4 bays, 22kW. Awaiting planning permission.",
        imageRes = null
    ),

    // City centre issues
    MapLocation(
        name = "Pothole on Grey Street",
        position = LatLng(54.9748, -1.6140),
        tags = listOf(Tag.ISSUE),
        description = "Large pothole causing hazard for cyclists near the Theatre Royal.",
        imageRes = null
    ),
    MapLocation(
        name = "Broken Street Light",
        position = LatLng(54.9751, -1.6135),
        tags = listOf(Tag.ISSUE, Tag.TEMPORARY),
        description = "Street light out on Grey Street junction. Temporary fix in place, permanent repair pending.",
        imageRes = null
    ),
    MapLocation(
        name = "Fly Tipping",
        position = LatLng(54.9745, -1.6148),
        tags = listOf(Tag.ISSUE),
        description = "Illegally dumped rubbish behind Grainger Market.",
        imageRes = null
    ),

    // Leazes area
    MapLocation(
        name = "Broken Fence",
        position = LatLng(54.9791, -1.6220),
        tags = listOf(Tag.ISSUE, Tag.AWAITING_APPROVAL),
        description = "Fence along Leazes Park perimeter is broken. Repair request submitted.",
        imageRes = null
    ),
    MapLocation(
        name = "Leazes Park",
        position = LatLng(54.9789, -1.6218),
        tags = listOf(Tag.GREEN_SPACE, Tag.APPROVED),
        description = "A beautiful Victorian park close to the city centre, perfect for a relaxing walk.",
        imageRes = null
    ),
    MapLocation(
        name = "Leazes Park Pond",
        position = LatLng(54.9793, -1.6211),
        tags = listOf(Tag.GREEN_SPACE, Tag.AWAITING_APPROVAL),
        description = "Proposed wildlife conservation area around the pond. Awaiting council approval.",
        imageRes = null
    ),

    // St James' area
    MapLocation(
        name = "New Cycle Lane",
        position = LatLng(54.9756, -1.6218),
        tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
        description = "Approved protected cycle lane running alongside St James' Park.",
        imageRes = null
    ),
    MapLocation(
        name = "St James' EV Bays",
        position = LatLng(54.9758, -1.6214),
        tags = listOf(Tag.ELECTRIC_CHARGER, Tag.AWAITING_APPROVAL),
        description = "Proposed EV charging bays in the St James' car park. Community consultation ongoing.",
        imageRes = null
    ),

    // Central Station area
    MapLocation(
        name = "Central Station EV Hub",
        position = LatLng(54.9684, -1.6178),
        tags = listOf(Tag.ELECTRIC_CHARGER, Tag.APPROVED),
        description = "Approved EV charging hub outside Central Station, 10 bays.",
        imageRes = null
    ),
    MapLocation(
        name = "Central Station Bike Shelter",
        position = LatLng(54.9687, -1.6171),
        tags = listOf(Tag.BIKE_RACK, Tag.AWAITING_APPROVAL),
        description = "Proposed covered bike shelter for 50 bikes outside the station.",
        imageRes = null
    ),
    MapLocation(
        name = "Station Road Roadworks",
        position = LatLng(54.9682, -1.6183),
        tags = listOf(Tag.ISSUE, Tag.TEMPORARY),
        description = "Temporary road closure outside Central Station for utility works. Expected until end of month.",
        imageRes = null
    ),

    // Untagged landmarks — no status, just points of interest
    MapLocation(
        name = "Tyne Bridge",
        position = LatLng(54.9679, -1.6051),
        tags = emptyList(),
        description = "The iconic green arch bridge spanning the River Tyne.",
        imageRes = null
    ),
    MapLocation(
        name = "Grey's Monument",
        position = LatLng(54.9751, -1.6131),
        tags = emptyList(),
        description = "A tall column commemorating Earl Grey, the former Prime Minister.",
        imageRes = null
    ),
)