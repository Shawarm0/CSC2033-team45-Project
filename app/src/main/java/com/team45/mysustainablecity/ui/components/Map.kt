package com.team45.mysustainablecity.ui.components

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import com.team45.mysustainablecity.R
import com.team45.mysustainablecity.utils.CustomMapMarker
import com.team45.mysustainablecity.utils.LocationBottomSheet
import com.team45.mysustainablecity.utils.MapLocation
import kotlinx.coroutines.launch


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun DiscoverMap() {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val newcastle = LatLng(54.9783, -1.6178)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(newcastle, 13f)
    }

    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var selectedLocation by remember { mutableStateOf<MapLocation?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(userLatLng, 15f)
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
            onMapClick = {
                selectedLocation = null
            }
        ) {

            locations.forEach { location ->
                val zoom by remember {
                    derivedStateOf { cameraPositionState.position.zoom }
                }
                val isSelected = selectedLocation == location

                MarkerComposable(
                    keys = arrayOf(location.name, zoom > 15f, isSelected),
                    state = MarkerState(position = location.position),
                    onClick = {
                        selectedLocation = location
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(location.position, 16f)
                            )
                        }
                        true
                    },
                ) {
                    CustomMapMarker(
                        location = location,
                        cameraPositionState = cameraPositionState,
                        isSelected = isSelected
                    )
                }
            }
        }

        MapControlButton(
            icon = Icons.Default.MyLocation,
            modifier = Modifier
                .padding(end = 16.dp, bottom = 16.dp)
                .align(Alignment.BottomEnd)
        ) {
            if (locationPermissionState.status.isGranted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val userLatLng = LatLng(it.latitude, it.longitude)
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(userLatLng, 15f)
                            )
                        }
                    }
                }
            } else {
                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(newcastle, 16f)
                    )
                }
            }
        }
    }

    if (selectedLocation != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedLocation = null },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            LocationBottomSheet(location = selectedLocation!!)
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

val locations = listOf(
    MapLocation(
        name = "The Black Gate",
        position = LatLng(54.9690, -1.6160),
        color = Color(0xFF9C27B0),
        icon = Icons.Default.Castle,
        description = "The Black Gate is the northern entrance to Newcastle's ancient Roman fort, Pons Aelius.",
        imageRes = null
    )
)