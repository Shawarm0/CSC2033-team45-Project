package com.team45.mysustainablecity.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.team45.mysustainablecity.Screen
import com.team45.mysustainablecity.ui.theme.BottomBarColor
import com.team45.mysustainablecity.ui.theme.LightHighlight
import com.team45.mysustainablecity.ui.theme.Primary

/**
 * Displays the main bottom navigation bar for the application.
 *
 * This composable renders a horizontal row of navigation items
 * defined in [Screen]. It visually highlights the currently
 * selected screen and notifies the parent when a new screen
 * is selected.
 *
 * @param selectedScreen The route string of the currently selected screen.
 * @param onScreenSelected Callback triggered when a navigation item is clicked.
 *                         Returns the selected [Screen].
 */
@Composable
fun BottomBar(
    selectedScreen: String,
    onScreenSelected: (Screen) -> Unit
) {

    val bottomBarScreens = listOf(
        Screen.Home,
        Screen.Discover,
        Screen.Alerts,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(
                color = BottomBarColor,
                shape = RoundedCornerShape(15.dp)
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        bottomBarScreens.forEach { screen ->
            BottomBarItem(
                icon = if (selectedScreen == screen.route) screen.filledIcon else screen.outlinedIcon,
                text = screen.route.replaceFirstChar { it.uppercaseChar() }.replace("_", " "),
                isSelected = selectedScreen == screen.route,
                onClick = { onScreenSelected(screen) }
            )
        }
    }
}

/**
 * Represents a single item inside the [BottomBar].
 *
 * Each item consists of:
 * - An icon (filled when selected, outlined when not selected)
 * - A text label
 * - An animated highlight "pill" behind the icon
 *
 * When selected:
 * - The background pill expands horizontally with animation
 * - The icon slightly scales up
 * - The icon tint changes to the primary theme color
 *
 * This composable is stateless and relies entirely on the
 * provided [isSelected] state to determine its visual appearance.
 *
 * @param icon The icon to display. Can be null for screens without icons.
 * @param text The label shown below the icon.
 * @param isSelected Whether this item is currently selected.
 * @param onClick Callback triggered when the item is tapped.
 */
@Composable
fun BottomBarItem(
    icon: ImageVector?,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    // 🔥 Animated pill width
    val animatedWidth by animateDpAsState(
        targetValue = if (isSelected) 75.dp else 45.dp,
        animationSpec = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing
        ),
        label = "pill_width"
    )

    // 🔥 Subtle icon scale
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(250),
        label = "icon_scale"
    )

    Box(
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        Column(
            modifier = Modifier
                .width(75.dp)
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ✨ Animated expanding pill
            Box(
                modifier = Modifier
                    .width(animatedWidth)
                    .height(40.dp)
                    .background(
                        color = if (isSelected) LightHighlight else Color.Transparent,
                        shape = RoundedCornerShape(100.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (icon != null) {
                    Icon(
                        modifier = Modifier
                            .size(28.dp)
                            .graphicsLayer {
                                scaleX = iconScale
                                scaleY = iconScale
                            },
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Primary else Color.Black,
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = text,
                color = Color.Black,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}


