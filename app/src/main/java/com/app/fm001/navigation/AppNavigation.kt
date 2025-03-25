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
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.fm001.model.UserRole
import com.app.fm001.navigation.Screen
import com.app.fm001.ui.screens.auth.GreenScreen
import com.app.fm001.ui.screens.auth.LoginScreen
import com.app.fm001.ui.screens.auth.SignUpScreen
import com.app.fm001.ui.screens.client.dashboard.ClientDashboard
import com.app.fm001.ui.screens.client.dashboard.ClientProfileViewModel
import com.app.fm001.ui.screens.client.dashboard.screens.PortfolioProfileScreen
import com.app.fm001.ui.screens.client.dashboard.screens.SearchPhotographersScreen
import com.app.fm001.ui.screens.photographer.dashboard.screens.EditPortfolioScreen
import com.app.fm001.ui.screens.photographer.dashboard.screens.PortfolioScreen
import com.app.fm001.ui.screens.photographer.dashboard.screens.UserPostsScreen
import com.app.fm001.ui.screens.shared.messages.MessagesScreen
import com.app.fm001.ui.screens.shared.messages.MessagesViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun AppNavigation(loggedInUserId: String) {
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
                                UserRole.CLIENT -> navController.navigate(ClientScreen.Home.route)
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
                                UserRole.CLIENT -> navController.navigate(ClientScreen.Home.route)
                                UserRole.PHOTOGRAPHER -> navController.navigate(PhotographerScreen.Home.route)
                            }
                        }
                    )
                }

                // Client Screens
                composable(ClientScreen.Home.route) {
                    ClientDashboard(loggedInUserId = loggedInUserId) // Pass the loggedInUserId here
                }
                composable(ClientScreen.Search.route) {
                    SearchPhotographersScreen(
                        loggedInUserId = loggedInUserId,
                        onNavigateToMessages = { senderId, receiverId ->
                            navController.navigate("messages/$senderId/$receiverId")
                        }
                    )
                }
                composable(ClientScreen.Jobs.route) {
                    // ClientJobsScreen()
                }
                composable(ClientScreen.Profile.route) {
                    // ClientProfileScreen()
                }
                composable(ClientScreen.PrivacySettings.route) {
                    // PrivacySettingsScreen()
                }
                composable(ClientScreen.Messages.route) {
                    // ClientMessagesScreen()
                }

                // Photographer Screens
                composable(PhotographerScreen.Home.route) {
                    // PhotographerHomeScreen()
                }
                composable(PhotographerScreen.Bids.route) {
                    // PhotographerBidsScreen()
                }
                composable(PhotographerScreen.Post.route) {
                    // PhotographerPostScreen()
                }
                composable(PhotographerScreen.Portfolio.route) {
                    // PortfolioScreen()
                }
                composable(PhotographerScreen.EditPortfolio.route) {
                    // EditPortfolioScreen()
                }
                composable(PhotographerScreen.Messages.route) {
                    // PhotographerMessagesScreen()
                }

                // Portfolio Profile Screen
                composable(
                    "${ClientScreen.PortfolioProfile.route}/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    PortfolioProfileScreen(navController, userId)
                }

                // Messaging Screen
                composable(
                    "messages/{senderId}/{receiverId}",
                    arguments = listOf(
                        navArgument("senderId") { type = NavType.StringType },
                        navArgument("receiverId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val senderId = backStackEntry.arguments?.getString("senderId") ?: ""
                    val receiverId = backStackEntry.arguments?.getString("receiverId") ?: ""
                    @Composable
                    fun MessagesScreen(
                        viewModel: MessagesViewModel,
                        loggedInUserId: String, // Add loggedInUserId parameter
                        senderId: String,
                        receiverId: String,
                        onNavigateBack: () -> Unit
                    ) {
                        // Use the parameters as needed
                        Text("Messages Screen: Sender = $senderId, Receiver = $receiverId, LoggedInUser = $loggedInUserId")
                    }
                }
            }
        }
    }
}