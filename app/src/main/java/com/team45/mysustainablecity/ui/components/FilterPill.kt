package com.team45.mysustainablecity.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team45.mysustainablecity.ui.theme.BottomBarColor
import com.team45.mysustainablecity.ui.theme.Primary

@Composable
fun FilterPill(
    text: String = "",
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    selectedColor: Color = Primary,
    shadow: Boolean = false
) {
    Surface(
        modifier = Modifier
            .wrapContentHeight()
            .wrapContentWidth(),
        shape = RoundedCornerShape(50),
        shadowElevation = if (shadow) if (isSelected) 2.dp else 6.dp else 0.dp,
        color = if (isSelected) selectedColor else BottomBarColor
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .clickable(
                    onClick = onClick,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                    ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = if (isSelected) Color.White else Color(0xFF141414),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )

        }
    }
}