package com.team45.mysustainablecity.ui.screens

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

@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val focusManager = LocalFocusManager.current
    val emailFocusRequester = remember { FocusRequester() }
    val confirmEmailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BottomBarColor
    ) { innerPadding ->


        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            val figmaWidth = 888f
            val figmaHeight = 412f

            val scaleX = size.width / figmaWidth
            val scaleY = size.height / figmaHeight

            val rectWidth = 808f * scaleX
            val rectHeight = 324f * scaleY

            // Position (change these to move rectangle)
            val offsetX = -900f
            val offsetY = -800f

            // Calculate center of rectangle
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

                Text(
                    text = "Sign Up",
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
                        .width(350.dp)
                        .focusRequester(emailFocusRequester),
                    value = email,
                    placeholder = "testemail@gmail.com",
                    label = "Email",
                    onValueChange = { email = it },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            confirmEmailFocusRequester.requestFocus()
                        }
                    )
                )
                Spacer(modifier = Modifier.height(13.dp))

                var confirmEmail by remember { mutableStateOf("") }

                CustomTextField(
                    modifier = Modifier
                        .width(350.dp)
                        .focusRequester(confirmEmailFocusRequester),
                    value = confirmEmail,
                    placeholder = "testemail@gmail.com",
                    label = "Confirm Email",
                    onValueChange = { confirmEmail = it },
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
                            focusManager.clearFocus()
                        }
                    )
                )


                Spacer(modifier = Modifier.height(20.dp))

                AppButton(
                    modifier = Modifier.width(170.dp),
                    onClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    },
                    text = "Sign Up",
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