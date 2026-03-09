package com.team45.mysustainablecity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.team45.mysustainablecity.ui.theme.Background
import com.team45.mysustainablecity.viewmodel.AuthViewModel

@Composable
fun PostScreen(
    authViewModel: AuthViewModel,
    innerNavController: NavHostController,
    locationName: String?,
    paddingValues: PaddingValues
) {

    Column(
        modifier = Modifier.fillMaxSize().background(Background).padding(paddingValues),
    ) {
        if (locationName != null) {
            Text(text = locationName)
        }
    }

}