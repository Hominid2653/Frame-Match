package com.app.fm001

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.fm001.navigation.Screen
import com.app.fm001.ui.screens.auth.LoginScreen
import com.app.fm001.model.UserRole
import com.app.fm001.ui.screens.client.dashboard.ClientDashboard
import com.app.fm001.ui.screens.photographer.dashboard.PhotographerDashboard
import com.app.fm001.ui.theme.FM001Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FM001Theme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = { 
                    navController.navigate(Screen.SignUp.route)
                },
                onLoginSuccess = { role ->
                    when (role) {
                        UserRole.CLIENT -> {
                            navController.navigate(Screen.ClientHome.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        UserRole.PHOTOGRAPHER -> {
                            navController.navigate(Screen.PhotographerHome.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.SignUp.route) {
            // TODO: Implement SignUp screen
        }

        composable(Screen.ClientHome.route) {
            ClientDashboard()
        }

        composable(Screen.PhotographerHome.route) {
            PhotographerDashboard()
        }
    }
}