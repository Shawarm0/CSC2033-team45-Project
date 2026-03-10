package com.team45.mysustainablecity.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.team45.mysustainablecity.ui.components.DiscoverMap

@Composable
fun HomeScreen(
    navController: NavController,
    paddingValues: PaddingValues
) {
    DiscoverMap(
        navController = navController,
        paddingValues
    )
}