package com.team45.mysustainablecity.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.team45.mysustainablecity.Screen
import com.team45.mysustainablecity.viewmodel.AuthViewModel
import kotlinx.datetime.format.Padding

@Composable
fun DiscoverScreen(
    authViewModel: AuthViewModel,
    paddingValues: PaddingValues,
    navController: NavHostController
) {
    val isLoggedOut by authViewModel.isLoggedOut.collectAsState()

    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut) {
            authViewModel.clearLoggedOut()
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Button(
        modifier = Modifier.padding(paddingValues),
        onClick = {
            authViewModel.logout()
            Log.d("DiscoverScreen", "Logout request sent\n\n")
        }
    ) {
        Text("LogOut")
    }
}