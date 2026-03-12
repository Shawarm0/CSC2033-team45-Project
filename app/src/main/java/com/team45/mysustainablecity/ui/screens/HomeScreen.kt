package com.team45.mysustainablecity.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import com.team45.mysustainablecity.ui.components.DiscoverMap
import com.team45.mysustainablecity.viewmodel.AuthViewModel
import com.team45.mysustainablecity.viewmodel.MapViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    rootNavController: NavHostController,
    paddingValues: PaddingValues,
    authViewModel: AuthViewModel,
    mapViewModel: MapViewModel
) {
    DiscoverMap(
        navController = navController,
        rootNavController = rootNavController,
        paddingValues,
        authViewModel,
        mapViewModel
    )
}