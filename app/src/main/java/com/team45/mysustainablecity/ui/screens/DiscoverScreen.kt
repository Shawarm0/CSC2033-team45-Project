package com.team45.mysustainablecity.ui.screens

import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.team45.mysustainablecity.viewmodel.AuthViewModel

@Composable
fun DiscoverScreen(
    authViewModel: AuthViewModel,
) {
    Button(
        onClick = {
            authViewModel.logout()
            Log.d("DiscoverScreen", "Logout request sent\n\n")
        }
    ) {
        Text("LogOut")
    }
}