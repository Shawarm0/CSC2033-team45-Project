package com.team45.mysustainablecity.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.team45.mysustainablecity.ui.theme.Primary

/**
 * A reusable rounded Material 3 button used throughout the app
 * to provide consistent styling and behaviour.
 *
 * This composable wraps a Material 3 [androidx.compose.material3.Button] and applies:
 * - Rounded pill shape
 * - Custom primary colour styling
 * - Optional leading icon
 * - Consistent height and horizontal padding
 * - Styled disabled state colours
 *
 * @param modifier Modifier applied to the button.
 * @param text The text displayed inside the button.
 * @param symbol Optional [ImageVector] displayed before the text.
 * @param onClick Callback invoked when the button is pressed.
 * @param color Background colour of the button. Defaults to [Primary].
 * @param enabled Controls whether the button is clickable.
 */
@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    text: String,
    symbol: ImageVector? = null,
    onClick: () -> Unit,
    color: Color = Primary,
    enabled: Boolean = true,
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(30.dp),
        shape = RoundedCornerShape(percent = 50),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White,
            disabledContainerColor = color.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        ),
        contentPadding = PaddingValues(horizontal = 15.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            if (symbol != null) {
                Icon(
                    imageVector = symbol,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}