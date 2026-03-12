package com.team45.mysustainablecity.ui.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.team45.mysustainablecity.Screen
import com.team45.mysustainablecity.ui.components.AppButton
import com.team45.mysustainablecity.ui.components.CustomTextField
import com.team45.mysustainablecity.ui.components.PasswordTextField
import com.team45.mysustainablecity.ui.theme.BottomBarColor
import com.team45.mysustainablecity.ui.theme.Primary
import com.team45.mysustainablecity.ui.theme.TextColor
import com.team45.mysustainablecity.viewmodel.AuthViewModel

private enum class SignUpStep { CREDENTIALS, USERNAME }

@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorState by authViewModel.errorState.collectAsState()
    val operationSuccess by authViewModel.operationSuccess.collectAsState()
    val focusManager = LocalFocusManager.current

    // Form state
    var email by remember { mutableStateOf("") }
    var confirmEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var confirmUsername by remember { mutableStateOf("") }

    // Step state — switches to USERNAME once authenticated
    var step by remember { mutableStateOf(SignUpStep.CREDENTIALS) }

    // Focus requesters
    val emailFocus = remember { FocusRequester() }
    val confirmEmailFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }
    val confirmUsernameFocus = remember { FocusRequester() }


    // When auth succeeds, move to username step AND clear operationSuccess
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated && step == SignUpStep.CREDENTIALS) {
            authViewModel.clearOperationSuccess()
            step = SignUpStep.USERNAME
        }
    }

    // When username is successfully saved, navigate home
    LaunchedEffect(operationSuccess) {
        if (operationSuccess == true && step == SignUpStep.USERNAME) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.SignUp.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BottomBarColor
    ) { innerPadding ->

        // Background decoration (unchanged)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val scaleX = size.width / 888f
            val scaleY = size.height / 412f
            val rectWidth = 808f * scaleX
            val rectHeight = 324f * scaleY
            val offsetX = -900f
            val offsetY = -800f
            rotate(
                degrees = -48f,
                pivot = Offset(offsetX + rectWidth / 2f, offsetY + rectHeight / 2f)
            ) {
                drawRect(
                    color = Primary,
                    topLeft = Offset(offsetX, offsetY),
                    size = Size(rectWidth, rectHeight)
                )
            }
        }

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
            ) {
                when (step) {
                    SignUpStep.CREDENTIALS -> {
                        // ── STEP 1: Email + Password ──────────────────────────
                        Text("Sign Up", style = MaterialTheme.typography.displayLarge)
                        Spacer(Modifier.height(5.dp))
                        Text(
                            "Enter email and password to proceed",
                            style = MaterialTheme.typography.bodyLarge.copy(color = TextColor)
                        )
                        Spacer(Modifier.height(20.dp))

                        CustomTextField(
                            modifier = Modifier.width(350.dp).focusRequester(emailFocus),
                            value = email,
                            placeholder = "testemail@gmail.com",
                            label = "Email",
                            errorMessage = errorState,
                            isError = errorState != null,
                            onValueChange = { email = it; authViewModel.clearError() },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { confirmEmailFocus.requestFocus() })
                        )
                        Spacer(Modifier.height(13.dp))

                        CustomTextField(
                            modifier = Modifier.width(350.dp).focusRequester(confirmEmailFocus),
                            value = confirmEmail,
                            placeholder = "testemail@gmail.com",
                            label = "Confirm Email",
                            isError = errorState != null,
                            onValueChange = { confirmEmail = it; authViewModel.clearError() },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { passwordFocus.requestFocus() })
                        )
                        Spacer(Modifier.height(13.dp))

                        PasswordTextField(
                            modifier = Modifier.width(350.dp).focusRequester(passwordFocus),
                            value = password,
                            onValueChange = { password = it },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            isError = errorState != null,
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                        )
                        Spacer(Modifier.height(20.dp))

                        AppButton(
                            modifier = Modifier.width(170.dp),
                            onClick = {
                                if (email.isBlank() || password.isBlank()) {
                                    authViewModel.errorState("Email and password cannot be blank")
                                    return@AppButton
                                }
                                if (email != confirmEmail) {
                                    authViewModel.errorState("Emails do not match")
                                    return@AppButton
                                }
                                authViewModel.register(email = email, password = password)
                                Log.d("SignUpScreen", "----- Register request sent -----")
                            },
                            text = if (isLoading) "Loading..." else "Sign Up",
                            symbol = Icons.Default.Check,
                        )
                    }

                    SignUpStep.USERNAME -> {
                        // ── STEP 2: Pick a username ───────────────────────────
                        Text("One more step!", style = MaterialTheme.typography.displayLarge)
                        Spacer(Modifier.height(5.dp))
                        Text(
                            "Choose a username for your account",
                            style = MaterialTheme.typography.bodyLarge.copy(color = TextColor)
                        )
                        Spacer(Modifier.height(20.dp))

                        CustomTextField(
                            modifier = Modifier.width(350.dp),
                            value = username,
                            placeholder = "cooluser123",
                            label = "Username",
                            errorMessage = errorState,
                            isError = errorState != null,
                            onValueChange = { username = it; authViewModel.clearError() },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { confirmUsernameFocus.requestFocus() })
                        )
                        Spacer(Modifier.height(13.dp))

                        CustomTextField(
                            modifier = Modifier.width(350.dp).focusRequester(confirmUsernameFocus),
                            value = confirmUsername,
                            placeholder = "cooluser123",
                            label = "Confirm Username",
                            isError = errorState != null,
                            onValueChange = { confirmUsername = it; authViewModel.clearError() },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                        )
                        Spacer(Modifier.height(20.dp))

                        AppButton(
                            modifier = Modifier.width(170.dp),
                            onClick = {
                                if (username.isBlank()) {
                                    authViewModel.errorState("Username cannot be blank")
                                    return@AppButton
                                }
                                if (username != confirmUsername) {
                                    authViewModel.errorState("Usernames do not match")
                                    return@AppButton
                                }
                                authViewModel.setUsername(username)
                            },
                            text = if (isLoading) "Saving..." else "Continue",
                            symbol = Icons.Default.Check,
                        )
                    }
                }
            }

            // Bottom nav hint (only shown on credentials step)
            if (step == SignUpStep.CREDENTIALS) {
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
                        Text("Already have an account?: ")
                        Text(
                            modifier = Modifier.clickable(
                                onClick = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.SignUp.route) { inclusive = true }
                                    }
                                },
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ),
                            text = "Login",
                            color = Color.Blue,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
            }
        }
    }
}