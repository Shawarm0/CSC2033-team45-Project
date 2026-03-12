package com.team45.mysustainablecity.ui.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team45.mysustainablecity.Screen
import com.team45.mysustainablecity.ui.components.AppButton
import com.team45.mysustainablecity.ui.components.CustomTextField
import com.team45.mysustainablecity.ui.components.PasswordTextField
import com.team45.mysustainablecity.ui.theme.BottomBarColor
import com.team45.mysustainablecity.ui.theme.Primary
import com.team45.mysustainablecity.ui.theme.TextColor
import com.team45.mysustainablecity.viewmodel.AuthViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val focusManager = LocalFocusManager.current
    val passwordFocusRequester = remember { FocusRequester() }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val errorState by authViewModel.errorState.collectAsState()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()


    // When auth succeeds, move to username step AND clear operationSuccess
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BottomBarColor
    ) { innerPadding ->

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) { // Box Composable

            val figmaWidth = 888f
            val figmaHeight = 412f

            val scaleX = size.width / figmaWidth
            val scaleY = size.height / figmaHeight

            val rectWidth = 808f * scaleX
            val rectHeight = 324f * scaleY

            val offsetX = -900f
            val offsetY = -800f

            val pivotX = offsetX + rectWidth / 2f
            val pivotY = offsetY + rectHeight / 2f

            rotate(
                degrees = -48f,
                pivot = Offset(pivotX, pivotY)
            ) {
                drawRect(
                    color = Primary,
                    topLeft = Offset(offsetX, offsetY),
                    size = Size(rectWidth, rectHeight)
                )
            }
        }

        // CONTENT
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) { // Login Content

                // HEADER
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


                // CENTER CONTENT
                CustomTextField(
                    modifier = Modifier
                        .width(350.dp),
                    value = email,
                    placeholder = "testemail@gmail.com",
                    label = "Email",
                    onValueChange = {
                        email = it
                        authViewModel.clearError()  // Clear error when user types
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    errorMessage = errorState,
                    isError = errorState != null,      // ✅ Add this
                    keyboardActions = KeyboardActions(
                        onNext = {
                            passwordFocusRequester.requestFocus()
                        }
                    )
                )

                Spacer(modifier = Modifier.height(13.dp))

                PasswordTextField(
                    modifier = Modifier
                        .width(350.dp)
                        .focusRequester(passwordFocusRequester),
                    value = password,
                    onValueChange = { password = it },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    isError = errorState != null,      // ✅ Add this
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
                        if (email.isBlank() || password.isBlank()) {
                            authViewModel.errorState("Email and password cannot be blank")
                            return@AppButton
                        }

                        authViewModel.login(
                            email = email,
                            password = password,
                        )
                        Log.d("LoginScreen", "----- Login request sent -----\n\n")
                    },
                    text = "Login",
                    symbol = Icons.Default.Check,
                )
            }

            // BOTTOM CONTENT
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                HorizontalDivider(
                    modifier = Modifier.width(330.dp),
                    color = Color.Black,
                    thickness = 1.dp
                )

                Row {
                    Text("Need an account?: ")
                    Text(
                        modifier = Modifier.clickable(
                            onClick = {
                                navController.navigate(Screen.SignUp.route)
                            },
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ),
                        text = "Sign Up",
                        color = Color.Blue,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun LoginPreview() {
    val rootNavController = rememberNavController()

    LoginScreen(
        rootNavController
    )
}