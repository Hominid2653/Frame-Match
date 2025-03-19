package com.app.fm001.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.app.fm001.model.UserRole
import com.app.fm001.navigation.Screen
import com.app.fm001.ui.screens.auth.GreenScreen
import com.app.fm001.ui.screens.auth.LoginScreen
import com.app.fm001.ui.screens.auth.SignUpScreen
import com.app.fm001.ui.screens.photographer.dashboard.screens.EditPortfolioScreen
import com.app.fm001.ui.screens.photographer.dashboard.screens.PortfolioScreen
import com.app.fm001.ui.screens.photographer.dashboard.screens.PostScreen
import com.app.fm001.ui.screens.photographer.dashboard.screens.UserPostsScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Login.route // Default start destination
            ) {
                // Authentication Screens
                composable(Screen.Login.route) {
                    LoginScreen(
                        onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                        onLoginSuccess = { userRole ->
                            when (userRole) {
                                UserRole.CLIENT -> navController.navigate(Screen.ClientHome.route)
                                UserRole.PHOTOGRAPHER -> navController.navigate(PhotographerScreen.Home.route)
                            }
                        }
                    )
                }
                composable(Screen.SignUp.route) {
                    SignUpScreen(
                        onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                        onSignUpSuccess = { userRole ->
                            when (userRole) {
                                UserRole.CLIENT -> navController.navigate(Screen.ClientHome.route)
                                UserRole.PHOTOGRAPHER -> navController.navigate(PhotographerScreen.Home.route)
                            }
                        }
                    )
                }

                // Photographer Screens
                composable(PhotographerScreen.Home.route) { /* Photographer Home Screen */ }
                composable(PhotographerScreen.Bids.route) { /* Photographer Bids Screen */ }
                composable(PhotographerScreen.Post.route) { /* Photographer Post Screen */ }
                composable(PhotographerScreen.Portfolio.route) { PortfolioScreen(navController) }
                composable(PhotographerScreen.EditPortfolio.route) { EditPortfolioScreen() }
                composable(PhotographerScreen.Messages.route) { /* Photographer Messages Screen */ }
            }
        }
    }
}
