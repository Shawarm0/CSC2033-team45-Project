package com.team45.mysustainablecity.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.team45.mysustainablecity.Screen
import com.team45.mysustainablecity.ui.components.AppButton
import com.team45.mysustainablecity.ui.components.CustomTextField
import com.team45.mysustainablecity.ui.components.PasswordTextField
import com.team45.mysustainablecity.ui.theme.BottomBarColor
import com.team45.mysustainablecity.ui.theme.TextColor

@Composable
fun LoginScreen(
    navController: NavController
) {
    val focusManager = LocalFocusManager.current
    val passwordFocusRequester = remember { FocusRequester() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BottomBarColor
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Login",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Enter email and password to proceed",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextColor
                )
            )



            Spacer(modifier = Modifier.height(20.dp))



            var email by remember { mutableStateOf("") }

            CustomTextField(
                modifier = Modifier
                    .width(350.dp),
                value = email,
                placeholder = "testemail@gmail.com",
                label = "Email",
                onValueChange = { email = it },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        passwordFocusRequester.requestFocus()
                    }
                )
            )
            Spacer(modifier = Modifier.height(13.dp))

            var password by remember { mutableStateOf("") }

            PasswordTextField(
                modifier = Modifier
                    .width(350.dp)
                    .focusRequester(passwordFocusRequester),
                value = password,
                onValueChange = { password = it },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus() // Hides keyboard
                    }
                )
            )


            Spacer(modifier = Modifier.height(20.dp))

            AppButton(
                modifier = Modifier.width(170.dp),
                onClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                text = "Login",
                symbol = Icons.Default.Check,
            )
        }
    }
}