package com.team45.mysustainablecity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.team45.mysustainablecity.ui.components.BottomBar
import com.team45.mysustainablecity.ui.screens.AlertScreen
import com.team45.mysustainablecity.ui.screens.DiscoverScreen
import com.team45.mysustainablecity.ui.screens.HomeScreen
import com.team45.mysustainablecity.ui.screens.LoginScreen
import com.team45.mysustainablecity.ui.screens.SignUpScreen
import com.team45.mysustainablecity.ui.theme.Background
import com.team45.mysustainablecity.ui.theme.MySustainableCityTheme

/**
 * The main entry point of the app
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate() called")

        enableEdgeToEdge()
        setContent {
            MySustainableCityTheme {
                val rootNavController = rememberNavController()
                AppNavigation(rootNavController)
            }
        }
    }

    /**
     * Called when the activity is becoming visible to the user.
     * This is followed by [onResume] if the activity comes to the foreground.
     */
    override fun onStart() {
        super.onStart()
    }

    /**
     * Called when the activity starts interacting with the user.
     * This is a good place to start animations or resume resources.
     */
    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume() called")
    }

    /**
     * Called when the system is about to put the activity into the background.
     * Use this to pause animations or save UI-related data.
     */
    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause() called")
    }

    /**
     * Called when the activity is no longer visible to the user.
     * Stop any processes that shouldn't run in the background here.
     */
    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop() called")

    }

    /**
     * Called before the activity is destroyed.
     * Use this for final cleanup like releasing resources.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy() called")

    }

    /**
     * Called after the activity has been stopped and is restarting again.
     * This is followed by [onStart].
     */
    override fun onRestart() {
        super.onRestart()
        Log.d("MainActivity", "onRestart() called")
    }
}

/**
 * Represents all the screens/routes used in the application.
 *
 * Each screen is defined as a sealed object with,
 * - [route] a unique string,
 * - [filledIcon] which is the icon when selected.
 * - [outlinedIcon] which is the icon when not selected.
 */
sealed class Screen(
    val route: String,
    val filledIcon: ImageVector? = null,
    val outlinedIcon: ImageVector? = null
) {
    object Home : Screen("home", Icons.Filled.Place, Icons.Outlined.Place)
    object Discover : Screen("discover", Icons.Filled.Explore, Icons.Outlined.Explore)
    object Alerts : Screen ("alerts", Icons.Filled.Notifications, Icons.Outlined.Notifications)
    object Post : Screen("post")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Profile : Screen("profile")
}


@Composable
fun AppNavigation(
    rootNavController: NavHostController
) {
    NavHost(
        navController = rootNavController,
        startDestination = Screen.Login.route,
        enterTransition = {
            val from = initialState.destination.route
            val to = targetState.destination.route

            when (// Forward: Login → SignUp or Main
                from) {
                Screen.Login.route if to in listOf(
                    Screen.SignUp.route,
                    Screen.Home.route,
                    Screen.Discover.route,
                    Screen.Alerts.route
                ) -> {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    )
                }

                // Back: SignUp → Login
                Screen.SignUp.route if to == Screen.Login.route -> {
                    EnterTransition.None   // 🔥 important
                }

                else -> EnterTransition.None
            }
        },

        exitTransition = {
            val from = initialState.destination.route
            val to = targetState.destination.route

            when (// Forward: Login → SignUp or Main
                from) {
                Screen.Login.route if to in listOf(
                    Screen.SignUp.route,
                    Screen.Home.route,
                    Screen.Discover.route,
                    Screen.Alerts.route
                ) -> {
                    slideOutHorizontally(
                        targetOffsetX = { -it / 4 },
                        animationSpec = tween(300)
                    )
                }

                // Back: SignUp → Login
                Screen.SignUp.route if to == Screen.Login.route -> {
                    slideOutHorizontally(
                        targetOffsetX = { it },   // 👉 move SignUp to right
                        animationSpec = tween(300)
                    )
                }

                else -> ExitTransition.None
            }
        }
    ) {
        composable(Screen.Login.route) { LoginScreen(rootNavController) }
        composable(Screen.SignUp.route) { SignUpScreen(rootNavController) }
        composable(Screen.Home.route) { MainScaffold() }
    }
}





@Composable
fun MainScaffold() {
    val innerNavController = rememberNavController()
    val currentBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Discover.route,
                    Screen.Alerts.route
                )
            ) {
                BottomBar(
                    selectedScreen = currentRoute ?: Screen.Home.route,
                    onScreenSelected = { screen ->
                        // Prevent recomposition on clicking the same screen
                        if (currentRoute != screen.route) {
                            innerNavController.navigate(screen.route) {
                                popUpTo(innerNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { padding ->

        NavHost(
            navController = innerNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding),
            enterTransition = {
                EnterTransition.None
            },
            exitTransition = {
                ExitTransition.None
            }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(innerNavController)
            }
            composable(Screen.Discover.route) {
                DiscoverScreen(innerNavController)
            }
            composable(Screen.Alerts.route) {
                AlertScreen(innerNavController)
            }
        }
    }
}