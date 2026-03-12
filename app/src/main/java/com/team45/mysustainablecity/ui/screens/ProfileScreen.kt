package com.team45.mysustainablecity.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.jan.supabase.auth.providers.invoke
import io.github.jan.supabase.realtime.Column

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.team45.mysustainablecity.Screen
import com.team45.mysustainablecity.ui.theme.BottomBarColor
import com.team45.mysustainablecity.ui.theme.Primary
import com.team45.mysustainablecity.ui.theme.TextColor

data class ProfileMenuItem(
    val label: String,
    val icon: ImageVector,
    val onClick:() -> Unit = {}
)


@Composable
fun ProfileScreen(
    navController: NavController,
    username: String = "Username"
) {
    val menuItems = listOf(
        ProfileMenuItem("Account", Icons.Default.AccountBox) {},
        ProfileMenuItem("Posts", Icons.Default.AccountBox) {},
        ProfileMenuItem("Something else", Icons.Default.AccountBox) {},
        ProfileMenuItem("Something else", Icons.Default.AccountBox) {},
        ProfileMenuItem("Something else", Icons.Default.AccountBox) {},
        ProfileMenuItem("Something else", Icons.Default.AccountBox) {},
        ProfileMenuItem("Something else", Icons.Default.AccountBox) {},
        ProfileMenuItem("Final thing", Icons.Default.AccountBox) {},
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BottomBarColor
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Map part (green placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color(0xFFDDEEDD))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "Profile picture",
                    modifier = Modifier.size(60.dp),
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Username
            Text(
                text = username,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Menu card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
            ) {
                menuItems.forEachIndexed { index, item ->
                    ProfileMenuRow(item = item)
                    if (index < menuItems.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = Color(0xFFEEEEEE),
                            thickness = 1.dp
                        )
                    }
                }
            }

        }
    }
}
@Composable
fun ProfileMenuRow(item: ProfileMenuItem) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource()},
                indication = null,
                onClick = item.onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            modifier = Modifier.size(24.dp),
            tint = Color.Black
        )
        Text(text = item.label,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


@Composable
@Preview
fun ProfilePreview() {
    val rootNavController = rememberNavController()

    ProfileScreen(
        rootNavController
    )
}