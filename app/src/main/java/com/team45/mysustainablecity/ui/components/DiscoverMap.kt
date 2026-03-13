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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
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
import com.team45.mysustainablecity.reps.PostRep
import com.team45.mysustainablecity.ui.screens.ProfileScreen
import com.team45.mysustainablecity.ui.theme.BottomBarColor
import com.team45.mysustainablecity.utils.ClusterBottomSheet
import com.team45.mysustainablecity.utils.ClusterMapMarker
import com.team45.mysustainablecity.utils.CompassButton
import com.team45.mysustainablecity.utils.CustomMapMarker
import com.team45.mysustainablecity.utils.LocationBottomSheet
import com.team45.mysustainablecity.viewmodel.AuthViewModel
import com.team45.mysustainablecity.viewmodel.MapViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun DiscoverMap(
    navController: NavController,
    rootNavController: NavHostController,
    paddingValues: PaddingValues,
    authViewModel: AuthViewModel,
    mapViewModel: MapViewModel
) {
    // ── VM state ────────────────────────────────────────────────────────────
    val searchText by mapViewModel.searchText.collectAsState()
    val activeFilters by mapViewModel.activeFilters.collectAsState()
    val selectedCluster by mapViewModel.selectedCluster.collectAsState()
    val isSearching = searchText.isNotBlank()

    // ── Local UI state (pure UI concerns, not business logic) ───────────────
    val showProfile = remember { mutableStateOf(false) }
    val profileSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val newcastle = LatLng(54.9783, -1.6178)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(newcastle, 13f)
    }

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // ── Derived map data (depends on camera zoom, so lives here) ────────────
    val zoom by remember { derivedStateOf { cameraPositionState.position.zoom } }
    val bearing by remember { derivedStateOf { cameraPositionState.position.bearing } }

// Add this
    val liveLocations by mapViewModel.postRep.liveMapLocations.collectAsState()

// Replace the plain function call with derivedStateOf
    val visibleLocations by remember(liveLocations, searchText, activeFilters) {
        derivedStateOf { mapViewModel.visibleLocations(searchText, activeFilters) }
    }

    val topResult by remember(visibleLocations, isSearching) {
        derivedStateOf { mapViewModel.topSearchResult(searchText, visibleLocations) }
    }

    val clusters by remember(zoom, visibleLocations) {
        derivedStateOf { mapViewModel.buildClusters(visibleLocations, zoom) }
    }

    // ── Effects ─────────────────────────────────────────────────────────────
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

    // Move camera to top search result as user types
    LaunchedEffect(topResult) {
        topResult?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(it.position, 16f)
            )
        }
    }

    // ── UI ──────────────────────────────────────────────────────────────────
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
            onMapClick = { mapViewModel.clearSelectedCluster() }
        ) {
            if (zoom > 11f) {
                clusters.forEach { cluster ->
                    val isSelected = selectedCluster == cluster
                    MarkerComposable(
                        keys = arrayOf(cluster.centroid, zoom, isSelected),
                        state = MarkerState(position = cluster.centroid),
                        onClick = {
                            mapViewModel.selectCluster(cluster)
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(
                                        cluster.centroid,
                                        cameraPositionState.position.zoom
                                    )
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

        // Search bar + filter pills
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = paddingValues.calculateTopPadding(), start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomTextField(
                modifier = Modifier,
                value = searchText,
                onValueChange = { mapViewModel.onSearchTextChange(it) },
                placeholder = "Search",
                clearButton = true,
                shadow = true,
                onClear = { mapViewModel.clearSearch() },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        mapViewModel.onSearchCommit()
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        modifier = Modifier
                            .width(30.dp)
                            .height(20.dp),
                        contentDescription = null
                    )
                },
                trailingContent = {
                    if (!isSearching) {
                        IconButton(
                            onClick = { showProfile.value = true },
                            modifier = Modifier
                                .width(30.dp)
                                .height(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(20.dp),
                                contentDescription = null
                            )
                        }
                    }
                }
            )

            // Hide filter pills while searching
            if (!isSearching) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp),
                ) {
                    items(com.team45.mysustainablecity.utils.Tag.entries) { tag ->
                        val isSelected = tag in activeFilters
                        FilterPill(
                            text = tag.displayName,
                            isSelected = isSelected,
                            onClick = { mapViewModel.toggleFilter(tag) },
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

        // My Location button
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
                                        .bearing(0f)
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

        // Compass button — only visible when map is rotated
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

    // Location / cluster bottom sheet
    selectedCluster?.let { cluster ->
        ModalBottomSheet(
            onDismissRequest = { mapViewModel.clearSelectedCluster() },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            if (cluster.isSingle) {
                LocationBottomSheet(
                    location = cluster.single,
                    navController = navController,
                    onDismiss = { mapViewModel.clearSelectedCluster() }
                )
            } else {
                ClusterBottomSheet(
                    cluster = cluster,
                    navController = navController,
                    onDismiss = { mapViewModel.clearSelectedCluster() }
                )
            }
        }
    }

    // Profile bottom sheet
    if (showProfile.value) {
        ModalBottomSheet(
            onDismissRequest = { showProfile.value = false },
            sheetState = profileSheetState,
            containerColor = BottomBarColor,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            contentWindowInsets = { WindowInsets.statusBars },
            modifier = Modifier.fillMaxHeight()
        ) {
            ProfileScreen(
                rootNavController,
                authViewModel = authViewModel,
                onLogOut = { showProfile.value = false }
            )
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