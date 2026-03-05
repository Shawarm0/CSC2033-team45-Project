package com.team45.mysustainablecity.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.team45.mysustainablecity.viewmodel.AuthViewModel

@Composable
fun DiscoverScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    Text("Discover Screen")

    Button(
        onClick = {
           authViewModel.logout()
        }
    ) {
        Text("LogOut")
    }
}