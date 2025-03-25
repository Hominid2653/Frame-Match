package com.app.fm001

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.fm001.navigation.Screen
import com.app.fm001.model.UserRole
import com.app.fm001.ui.screens.auth.LoginScreen
import com.app.fm001.ui.screens.auth.SignUpScreen
import com.app.fm001.ui.screens.client.dashboard.ClientDashboard
import com.app.fm001.ui.screens.photographer.dashboard.PhotographerDashboard
import com.app.fm001.ui.theme.FM001Theme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)

        setContent {
            FM001Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val loggedInUserId = auth.currentUser?.uid ?: ""
    val loggedInUserEmail = auth.currentUser?.email ?: ""

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onLoginSuccess = { userRole ->
                    when (userRole) {
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
            SignUpScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onSignUpSuccess = { userRole ->
                    when (userRole) {
                        UserRole.CLIENT -> {
                            navController.navigate(Screen.ClientHome.route) {
                                popUpTo(Screen.SignUp.route) { inclusive = true }
                            }
                        }
                        UserRole.PHOTOGRAPHER -> {
                            navController.navigate(Screen.PhotographerHome.route) {
                                popUpTo(Screen.SignUp.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.ClientHome.route) {
            ClientDashboard(loggedInUserId = loggedInUserId) // Pass the loggedInUserId
        }

        composable(Screen.PhotographerHome.route) {
            PhotographerDashboard(
                loggedInUserId = loggedInUserId,
                loggedInUserEmail = loggedInUserEmail // Pass the loggedInUserEmail
            )
        }
    }
}
