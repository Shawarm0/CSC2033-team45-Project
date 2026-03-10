package com.team45.mysustainablecity.ui.components

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.team45.mysustainablecity.R
import com.team45.mysustainablecity.ui.screens.ProfileScreen
import com.team45.mysustainablecity.ui.theme.BottomBarColor
import com.team45.mysustainablecity.utils.ClusterBottomSheet
import com.team45.mysustainablecity.utils.ClusterMapMarker
import com.team45.mysustainablecity.utils.CompassButton
import com.team45.mysustainablecity.utils.CustomMapMarker
import com.team45.mysustainablecity.utils.LocationBottomSheet
import com.team45.mysustainablecity.utils.LocationCluster
import com.team45.mysustainablecity.utils.MapLocation
import com.team45.mysustainablecity.utils.Tag
import com.team45.mysustainablecity.utils.clusterLocations
import kotlinx.coroutines.launch

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
                zoom >= 19f -> 10.0
                zoom >= 18f -> 20.0
                zoom >= 17f -> 35.0
                zoom >= 16.5f -> 55.0
                zoom >= 16f -> 80.0
                zoom >= 15.5f -> 110.0
                zoom >= 15f -> 150.0
                zoom >= 14.5f -> 200.0
                zoom >= 14f -> 260.0
                zoom >= 13.5f -> 340.0
                zoom >= 13f -> 430.0
                zoom >= 12.5f -> 550.0
                zoom >= 12f -> 700.0
                zoom >= 11.5f -> 900.0
                zoom >= 11f -> 1150.0
                zoom >= 10.5f -> 1500.0
                zoom >= 10f -> 2000.0
                else -> 3000.0
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
            if (zoom > 11f) {
                clusters.forEach { cluster ->
                    val isSelected = selectedCluster == cluster
                    MarkerComposable(
                        keys = arrayOf(cluster.centroid, zoom, isSelected),
                        state = MarkerState(position = cluster.centroid),
                        onClick = {
                            selectedCluster = cluster
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(cluster.centroid, cameraPositionState.position. zoom)                                )
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
                    contentPadding = PaddingValues(horizontal = 4.dp),
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
                            shadow = true,
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

// In DiscoverMap, update the MapControlButton for MyLocation:
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
                                CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.Builder()
                                        .target(LatLng(it.latitude, it.longitude))
                                        .zoom(17f)
                                        .bearing(0f) // reset to north
                                        .tilt(0f)
                                        .build()
                                )
                            )
                        }
                    }
                }
            } else {
                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder()
                                .target(newcastle)
                                .zoom(17f)
                                .bearing(0f)
                                .tilt(0f)
                                .build()
                        )
                    )
                }
            }
        }

        // Compass button — bottom start, same height
        val bearing by remember { derivedStateOf { cameraPositionState.position.bearing } }

// Only show compass when map is rotated away from north
        if (bearing != 0f) {
            CompassButton(
                bearing = bearing,
                modifier = Modifier
                    .padding(start = 16.dp, bottom = paddingValues.calculateBottomPadding() + 16.dp)
                    .align(Alignment.BottomStart)
            ) {
                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder()
                                .target(cameraPositionState.position.target)
                                .zoom(cameraPositionState.position.zoom)
                                .bearing(0f)
                                .tilt(0f)
                                .build()
                        )
                    )
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

    // --- Quayside Bike Racks ---
    MapLocation(
        name = "Quayside Bike Rack A",
        position = LatLng(54.9686, -1.6094),
        tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
        description = "Bike rack near the Quayside restaurants, 12 spaces available.",
    ),
    MapLocation(
        name = "Quayside Bike Rack B",
        position = LatLng(54.9688, -1.6088),
        tags = listOf(Tag.BIKE_RACK, Tag.AWAITING_APPROVAL),
        description = "Bike rack outside the Pitcher & Piano, 8 spaces. Pending council sign-off.",
    ),
    MapLocation(
        name = "Quayside Bike Rack C",
        position = LatLng(54.9684, -1.6099),
        tags = listOf(Tag.BIKE_RACK, Tag.TEMPORARY),
        description = "Temporary covered bike rack near the law courts, 20 spaces. Installed for summer.",
    ),
    MapLocation(
        name = "Quayside Bike Rack D",
        position = LatLng(54.9690, -1.6102),
        tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
        description = "Bike rack near the Sage car park entrance, 16 spaces.",
    ),
    MapLocation(
        name = "Millennium Bridge Bike Rack",
        position = LatLng(54.9682, -1.6012),
        tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
        description = "Bike rack at the north end of the Millennium Bridge, 10 spaces.",
    ),

    // --- Quayside EV Chargers ---
    MapLocation(
        name = "Quayside EV Charger 1",
        position = LatLng(54.9685, -1.6091),
        tags = listOf(Tag.ELECTRIC_CHARGER, Tag.APPROVED),
        description = "Fast EV charging point, 2 bays, 50kW.",
    ),
    MapLocation(
        name = "Quayside EV Charger 2",
        position = LatLng(54.9683, -1.6096),
        tags = listOf(Tag.ELECTRIC_CHARGER, Tag.AWAITING_APPROVAL),
        description = "Proposed standard EV charging point, 4 bays, 22kW. Awaiting planning permission.",
    ),
    MapLocation(
        name = "Quayside EV Charger 3",
        position = LatLng(54.9687, -1.6080),
        tags = listOf(Tag.ELECTRIC_CHARGER, Tag.APPROVED),
        description = "Rapid EV charger near the Malmaison hotel, 2 bays, 150kW.",
    ),
    MapLocation(
        name = "BALTIC Car Park EV",
        position = LatLng(54.9678, -1.6005),
        tags = listOf(Tag.ELECTRIC_CHARGER, Tag.APPROVED),
        description = "EV charging in the BALTIC car park, 6 bays, 22kW.",
    ),

    // --- Quayside Issues ---
    MapLocation(
        name = "Broken Cobblestones",
        position = LatLng(54.9689, -1.6075),
        tags = listOf(Tag.ISSUE),
        description = "Several cobblestones loose near the Quayside walkway, trip hazard for pedestrians.",
    ),
    MapLocation(
        name = "Overflowing Bin",
        position = LatLng(54.9686, -1.6082),
        tags = listOf(Tag.ISSUE),
        description = "Public bin overflowing near the Quayside bar strip, reported multiple times.",
    ),

    // --- City Centre Issues ---
    MapLocation(
        name = "Pothole on Grey Street",
        position = LatLng(54.9748, -1.6140),
        tags = listOf(Tag.ISSUE),
        description = "Large pothole causing hazard for cyclists near the Theatre Royal.",
    ),
    MapLocation(
        name = "Broken Street Light",
        position = LatLng(54.9751, -1.6135),
        tags = listOf(Tag.ISSUE, Tag.TEMPORARY),
        description = "Street light out on Grey Street junction. Temporary fix in place, permanent repair pending.",
    ),
    MapLocation(
        name = "Fly Tipping",
        position = LatLng(54.9745, -1.6148),
        tags = listOf(Tag.ISSUE),
        description = "Illegally dumped rubbish behind Grainger Market.",
    ),
    MapLocation(
        name = "Cracked Pavement",
        position = LatLng(54.9742, -1.6161),
        tags = listOf(Tag.ISSUE, Tag.AWAITING_APPROVAL),
        description = "Cracked and uneven pavement slabs outside Grainger Market entrance, repair approved in principle.",

    ),
    MapLocation(
        name = "Graffiti on Monument",
        position = LatLng(54.9752, -1.6130),
        tags = listOf(Tag.ISSUE),
        description = "Graffiti on the base of Grey's Monument plinth. Cleaning scheduled.",
    ),
    MapLocation(
        name = "Blocked Drain",
        position = LatLng(54.9746, -1.6143),
        tags = listOf(Tag.ISSUE),
        description = "Storm drain blocked with debris on Grey Street causing pooling after rain.",
    ),

    // --- City Centre EV Chargers ---
    MapLocation(
        name = "Eldon Square EV Hub",
        position = LatLng(54.9757, -1.6162),
        tags = listOf(Tag.ELECTRIC_CHARGER, Tag.APPROVED),
        description = "EV charging hub in the Eldon Square car park, 8 bays, 50kW.",
    ),
    MapLocation(
        name = "John Dobson St EV",
        position = LatLng(54.9754, -1.6145),
        tags = listOf(Tag.ELECTRIC_CHARGER, Tag.AWAITING_APPROVAL),
        description = "Proposed on-street EV chargers on John Dobson Street, 4 bays.",
    ),

    // --- City Centre Bike Racks ---
    MapLocation(
        name = "Monument Bike Rack",
        position = LatLng(54.9753, -1.6133),
        tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
        description = "Sheffield stand bike rack near Grey's Monument, 20 spaces.",
    ),
    MapLocation(
        name = "Eldon Square Bike Rack",
        position = LatLng(54.9758, -1.6155),
        tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
        description = "Covered bike rack outside Eldon Square's Percy Street entrance, 30 spaces.",
    ),
    MapLocation(
        name = "Grainger Street Bike Rack",
        position = LatLng(54.9740, -1.6158),
        tags = listOf(Tag.BIKE_RACK, Tag.AWAITING_APPROVAL),
        description = "Proposed new bike rack on Grainger Street. Awaiting highway approval.",
    ),

    // --- Leazes / St James' area ---
    MapLocation(
        name = "Broken Fence",
        position = LatLng(54.9791, -1.6220),
        tags = listOf(Tag.ISSUE, Tag.AWAITING_APPROVAL),
        description = "Fence along Leazes Park perimeter is broken. Repair request submitted.",
    ),
    MapLocation(
        name = "Leazes Park",
        position = LatLng(54.9789, -1.6218),
        tags = listOf(Tag.GREEN_SPACE, Tag.APPROVED),
        description = "A beautiful Victorian park close to the city centre, perfect for a relaxing walk.",
    ),
    MapLocation(
        name = "Leazes Park Pond",
        position = LatLng(54.9793, -1.6211),
        tags = listOf(Tag.GREEN_SPACE, Tag.AWAITING_APPROVAL),
        description = "Proposed wildlife conservation area around the pond. Awaiting council approval.",
    ),
    MapLocation(
        name = "Leazes Park Bike Rack",
        position = LatLng(54.9787, -1.6224),
        tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
        description = "Bike rack at the main entrance to Leazes Park, 14 spaces.",
    ),
    MapLocation(
        name = "Leazes Park EV Bay",
        position = LatLng(54.9785, -1.6228),
        tags = listOf(Tag.ELECTRIC_CHARGER, Tag.TEMPORARY),
        description = "Temporary EV charging bay installed near Leazes Park for the football season.",
    ),
    MapLocation(
        name = "New Cycle Lane",
        position = LatLng(54.9756, -1.6218),
        tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
        description = "Approved protected cycle lane running alongside St James' Park.",
    ),
    MapLocation(
        name = "St James' EV Bays",
        position = LatLng(54.9758, -1.6214),
        tags = listOf(Tag.ELECTRIC_CHARGER, Tag.AWAITING_APPROVAL),
        description = "Proposed EV charging bays in the St James' car park. Community consultation ongoing.",
    ),
    MapLocation(
        name = "St James' Park Litter",
        position = LatLng(54.9760, -1.6221),
        tags = listOf(Tag.ISSUE),
        description = "Persistent litter problem on the approach to St James' Park on match days.",
    ),
    MapLocation(
        name = "Gallowgate Green Space",
        position = LatLng(54.9762, -1.6210),
        tags = listOf(Tag.GREEN_SPACE, Tag.AWAITING_APPROVAL),
        description = "Proposed pocket park on unused land off Gallowgate. Planning application submitted.",
    ),

    // --- Central Station area ---
    MapLocation(
        name = "Central Station EV Hub",
        position = LatLng(54.9684, -1.6178),
        tags = listOf(Tag.ELECTRIC_CHARGER, Tag.APPROVED),
        description = "Approved EV charging hub outside Central Station, 10 bays.",
    ),
    MapLocation(
        name = "Central Station Bike Shelter",
        position = LatLng(54.9687, -1.6171),
        tags = listOf(Tag.BIKE_RACK, Tag.AWAITING_APPROVAL),
        description = "Proposed covered bike shelter for 50 bikes outside the station.",
    ),
    MapLocation(
        name = "Station Road Roadworks",
        position = LatLng(54.9682, -1.6183),
        tags = listOf(Tag.ISSUE, Tag.TEMPORARY),
        description = "Temporary road closure outside Central Station for utility works. Expected until end of month.",
    ),
    MapLocation(
        name = "Station Taxi Rank Issue",
        position = LatLng(54.9680, -1.6175),
        tags = listOf(Tag.ISSUE),
        description = "Taxi rank blocking the pavement outside the station entrance, accessibility concern.",
    ),
    MapLocation(
        name = "Neville Street Bike Rack",
        position = LatLng(54.9683, -1.6168),
        tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
        description = "Bike rack on Neville Street near the station exit, 18 spaces.",
    ),

    // --- Ouseburn / East End ---
    MapLocation(
        name = "Ouseburn Valley Green Space",
        position = LatLng(54.9712, -1.5981),
        tags = listOf(Tag.GREEN_SPACE, Tag.APPROVED),
        description = "Community green space in the Ouseburn Valley, managed by the Ouseburn Trust.",
    ),
    MapLocation(
        name = "Ouseburn Bike Rack",
        position = LatLng(54.9715, -1.5976),
        tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
        description = "Bike rack outside the Cluny music venue, 8 spaces.",
    ),
    MapLocation(
        name = "Ouseburn Fly Tipping",
        position = LatLng(54.9708, -1.5990),
        tags = listOf(Tag.ISSUE),
        description = "Dumped mattresses and furniture near the Ouseburn river path.",
    ),
    MapLocation(
        name = "Ouseburn EV Charger",
        position = LatLng(54.9710, -1.5985),
        tags = listOf(Tag.ELECTRIC_CHARGER, Tag.AWAITING_APPROVAL),
        description = "Proposed EV charger for the Ouseburn creative quarter car park, 4 bays.",
    ),

    // --- Jesmond ---
    MapLocation(
        name = "Jesmond Dene Green Space",
        position = LatLng(54.9881, -1.5986),
        tags = listOf(Tag.GREEN_SPACE, Tag.APPROVED),
        description = "A beloved wooded dene running through Jesmond, maintained by the city council.",
    ),
    MapLocation(
        name = "Jesmond Road EV Charger",
        position = LatLng(54.9842, -1.6041),
        tags = listOf(Tag.ELECTRIC_CHARGER, Tag.APPROVED),
        description = "On-street EV charger on Jesmond Road, 2 bays, 7kW.",
    ),
    MapLocation(
        name = "Jesmond Metro Bike Rack",
        position = LatLng(54.9839, -1.6038),
        tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
        description = "Bike rack at Jesmond Metro station, 24 spaces, covered.",
    ),
    MapLocation(
        name = "Osborne Road Pothole",
        position = LatLng(54.9845, -1.6052),
        tags = listOf(Tag.ISSUE),
        description = "Deep pothole on Osborne Road near the restaurant strip, reported by multiple residents.",
    ),

    // --- Untagged landmarks ---
    MapLocation(
        name = "Tyne Bridge",
        position = LatLng(54.9679, -1.6051),
        tags = emptyList(),
        description = "The iconic green arch bridge spanning the River Tyne.",
    ),
    MapLocation(
        name = "Grey's Monument",
        position = LatLng(54.9751, -1.6131),
        tags = emptyList(),
        description = "A tall column commemorating Earl Grey, the former Prime Minister.",
    ),
    MapLocation(
        name = "Newcastle Cathedral",
        position = LatLng(54.9726, -1.6154),
        tags = emptyList(),
        description = "The Cathedral Church of St Nicholas, a stunning medieval building in the heart of Newcastle.",
    ),
    MapLocation(
        name = "Grainger Market",
        position = LatLng(54.9742, -1.6155),
        tags = emptyList(),
        description = "A covered Victorian market in the city centre, one of the oldest in Europe. A covered Victorian market in the city centre, one of the oldest in Europe. A covered Victorian market in the city centre, one of the oldest in Europe.",
    ),
)