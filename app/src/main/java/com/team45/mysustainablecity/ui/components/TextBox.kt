package com.team45.mysustainablecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team45.mysustainablecity.ui.theme.LightBoxBackground
import kotlinx.coroutines.launch

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,

    label: String? = null,
    placeholder: String = "Text",

    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,

    isError: Boolean = false,
    errorMessage: String? = null,

    enabled: Boolean = true,
    readOnly: Boolean = false,

    singleLine: Boolean = true,
    maxLines: Int = 1,

    clearButton: Boolean = false,

    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,

    visualTransformation: VisualTransformation = VisualTransformation.None,

    floatsOnly: Boolean = false
) {

    var isFocused by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val coroutineScope = rememberCoroutineScope()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    // ORIGINAL COLOUR LOGIC RESTORED
    val borderColor = if (isFocused && isError) {
        Color.Red
    } else if (isFocused) {
        Color.Black.copy(alpha = 0.7f)
    } else if (isError) {
        Color(0xFFF19191)
    } else {
        Color(0xFFDCDEDD)
    }

    val iconColor =
        if (isFocused) Color.Black.copy(alpha = 0.7f)
        else Color.Gray

    val textColor =
        if (isFocused) Color.Black.copy(alpha = 0.7f)
        else Color.Gray

    Column(
        modifier = modifier.wrapContentSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {

        // Label + Error message row (ORIGINAL STYLE)
        Row {
            if (label != null) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(end = 5.dp)
                )
            }

            if (isError && errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Red
                    )
                )
            }
        }

        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                if (floatsOnly) {
                    if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                        onValueChange(newValue)
                    }
                } else {
                    onValueChange(newValue)
                }
            },
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = textColor
            ),
            cursorBrush = SolidColor(Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .padding(0.dp)
                .bringIntoViewRequester(bringIntoViewRequester)
                .onFocusChanged {
                    isFocused = it.isFocused
                    if (it.isFocused) {
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                },
            decorationBox = { innerTextField ->

                Row(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(50)
                        )
                        .background(
                            color = LightBoxBackground,
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Leading slot
                    if (leadingContent != null) {
                        leadingContent()
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = if (!isFocused) Color.Gray else Color.Black,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        innerTextField()
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Clear button (original sizing restored)
                    if (clearButton && value.isNotEmpty() && enabled && !readOnly) {
                        IconButton(
                            onClick = { onValueChange("") },
                            modifier = Modifier
                                .padding(0.dp)
                                .width(30.dp)
                                .height(20.dp),
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear Text",
                                tint = iconColor
                            )
                        }
                    }

                    // Trailing slot
                    if (trailingContent != null) {
                        trailingContent()
                    }
                }
            }
        )
    }
}




@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Password",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var passwordVisible by remember { mutableStateOf(false) }

    CustomTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = "••••••••",
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation =
            if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
        trailingContent = {
            IconButton(
                onClick = { passwordVisible = !passwordVisible },
                modifier = Modifier
                    .padding(0.dp)
                    .width(30.dp)
                    .height(20.dp),
            ) {
                Icon(
                    imageVector = if (passwordVisible)
                        Icons.Default.Visibility
                    else
                        Icons.Default.VisibilityOff,
                    contentDescription = null
                )
            }
        }
    )
}