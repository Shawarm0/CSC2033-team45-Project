package com.team45.mysustainablecity.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.team45.mysustainablecity.viewmodel.AuthViewModel
import kotlinx.datetime.format.Padding

@Composable
fun DiscoverScreen(
    authViewModel: AuthViewModel,
    paddingValues: PaddingValues
) {
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