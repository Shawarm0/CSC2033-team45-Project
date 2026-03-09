package com.team45.mysustainablecity.ui.screens

import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.team45.mysustainablecity.viewmodel.AuthViewModel

@Composable
fun PostScreen(
    authViewModel: AuthViewModel,
    innerNavController: NavHostController,
    locationName: String?,
) {
    if (locationName != null) {
        Text(locationName)
    }

}