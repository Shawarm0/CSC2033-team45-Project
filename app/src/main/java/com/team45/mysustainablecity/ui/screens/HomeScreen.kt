package com.team45.mysustainablecity.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.team45.mysustainablecity.ui.components.CustomTextField
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