package com.team45.mysustainablecity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.team45.mysustainablecity.ui.components.BottomBar
import com.team45.mysustainablecity.ui.screens.DiscoverScreen
import com.team45.mysustainablecity.ui.screens.HomeScreen
import com.team45.mysustainablecity.ui.screens.LoginScreen
import com.team45.mysustainablecity.ui.screens.PostScreen
import com.team45.mysustainablecity.ui.screens.SignUpScreen
import com.team45.mysustainablecity.ui.theme.Background
import com.team45.mysustainablecity.ui.theme.MySustainableCityTheme
import com.team45.mysustainablecity.utils.AppContainer
import com.team45.mysustainablecity.viewmodel.AuthViewModel

/**
 * The main entry point of the app
 */
class MainActivity : ComponentActivity() {

    val appContainer = AppContainer()
    val authViewModel = AuthViewModel(userRep = appContainer.userRepository)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            !authViewModel.isSessionReady.value
        }

        enableEdgeToEdge()

        setContent {
            MySustainableCityTheme {
                val rootNavController = rememberNavController()
                AppNavigation(rootNavController, authViewModel)
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
    object DiscoverPost : Screen("discover?locationName={locationName}") {
        fun createRoute(locationName: String) = "discover?locationName=$locationName"
    }
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Profile : Screen("profile")
}


@Composable
fun AppNavigation(
    rootNavController: NavHostController,
    authViewModel: AuthViewModel
) {
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val isSessionReady by authViewModel.isSessionReady.collectAsState()

    // Don't render anything until session is resolved — splash screen holds during this
    if (!isSessionReady) return

    val startDestination = if (isAuthenticated) Screen.Home.route else Screen.Login.route

    NavHost(
        navController = rootNavController,
        startDestination = startDestination,
        enterTransition = {
            val from = initialState.destination.route
            val to = targetState.destination.route

            when (from) {

                // Logout: Main → Login
                Screen.Discover.route,
                Screen.Home.route,
                Screen.Alerts.route -> {
                    if (to == Screen.Login.route) {
                        fadeIn(
                            animationSpec = tween(1000)
                        )
                    } else EnterTransition.None
                }

                // Forward: Login → SignUp or Main
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

                Screen.SignUp.route if to == Screen.Login.route -> {
                    EnterTransition.None
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
        composable(Screen.Login.route) { LoginScreen(rootNavController, authViewModel) }
        composable(Screen.SignUp.route) { SignUpScreen(rootNavController, authViewModel) }
        composable(Screen.Home.route) { MainScaffold(authViewModel) }
    }

}





@Composable
fun MainScaffold(
    authViewModel: AuthViewModel
) {
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
            enterTransition = {
                val to = targetState.destination.route
                if (to == Screen.DiscoverPost.route) {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(500)
                    )
                } else {
                    EnterTransition.None
                }
            },
            exitTransition = {
                val from = initialState.destination.route
                if (from == Screen.DiscoverPost.route) {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(500)
                    )
                } else {
                    ExitTransition.None
                }
            }

        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    innerNavController,
                    padding
                )
            }
            composable(Screen.Discover.route) {
                DiscoverScreen(
                    authViewModel,
                    padding
                )
            }
            composable(
                route = Screen.DiscoverPost.route,
                arguments = listOf(navArgument("locationName") { nullable = true })
            ) { backStackEntry ->
                val locationName = backStackEntry.arguments?.getString("locationName")
                PostScreen(authViewModel, innerNavController, locationName, padding)
            }
            composable(Screen.Alerts.route) {
                //AlertScreen(innerNavController)
            }
        }
    }
}