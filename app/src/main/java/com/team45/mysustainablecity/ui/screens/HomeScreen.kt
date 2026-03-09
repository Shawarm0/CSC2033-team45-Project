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
    navController: NavController
) {

    var text by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // Map background
        DiscoverMap(
            navController = navController
        )

        // Search bar overlay
        CustomTextField(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp)
                .width(380.dp),
            value = text,
            onValueChange = { text = it },
            placeholder = "Search",
            clearButton = true,
            shadow = true,

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
                IconButton(
                    onClick = {},
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
        )
    }
}