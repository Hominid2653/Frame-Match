package com.app.fm001.ui.screens.client.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.fm001.ui.screens.client.dashboard.components.ClientBottomNav
import com.app.fm001.ui.screens.client.dashboard.components.BottomNavItem
import com.app.fm001.ui.screens.client.dashboard.screens.*
import com.app.fm001.ui.screens.shared.messages.MessagesScreen

@Composable
fun ClientDashboard(loggedInUserId: String) {
    val navController = rememberNavController()
    val profileViewModel: ClientProfileViewModel = viewModel()

    Scaffold(
        bottomBar = {
            ClientBottomNav(
                navController = navController,
                items = listOf(
                    BottomNavItem(
                        screen = ClientScreen.Home,
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = "Home"
                    ),
                    BottomNavItem(
                        screen = ClientScreen.Search,
                        icon = { Icon(Icons.Default.Search, contentDescription = null) },
                        label = "Find"
                    ),
                    BottomNavItem(
                        screen = ClientScreen.Jobs,
                        icon = { Icon(Icons.Default.WorkHistory, contentDescription = null) },
                        label = "My Jobs"
                    ),
                    BottomNavItem(
                        screen = ClientScreen.Profile,
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = "Profile"
                    )
                )
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = ClientScreen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // ✅ Home Screen
            composable(ClientScreen.Home.route) {
                ClientHomeScreen(navController, viewModel())
            }




            // ✅ Search Screen - Navigates to Messages
            composable(ClientScreen.Search.route) {
                SearchPhotographersScreen(
                    loggedInUserId = loggedInUserId,
                    onNavigateToMessages = { senderId, receiverId ->
                        navController.navigate("client_messages/$senderId/$receiverId")
                    }
                )
            }
            composable("portfolio/{email}") { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email") ?: ""
                PortfolioProfileScreen(navController, email)
            }


            // ✅ Jobs Screen
            composable(ClientScreen.Jobs.route) {
                ClientJobsScreen()
            }

            // ✅ Profile Screen
            composable(ClientScreen.Profile.route) {
                ClientProfileScreen(
                    viewModel = profileViewModel,
                    onNavigateToPrivacySettings = {
                        navController.navigate(ClientScreen.PrivacySettings.route)
                    }
                )
            }

            // ✅ Privacy Settings
            composable(ClientScreen.PrivacySettings.route) {
                PrivacySettingsScreen(
                    viewModel = profileViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ✅ 📩 Messages Screen
            composable(
                "client_messages/{senderId}/{receiverId}",
                arguments = listOf(
                    navArgument("senderId") { type = NavType.StringType },
                    navArgument("receiverId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val senderId = backStackEntry.arguments?.getString("senderId") ?: ""
                val receiverId = backStackEntry.arguments?.getString("receiverId") ?: ""

                MessagesScreen(
                    senderId = senderId,  // ✅ Add senderId
                    receiverId = receiverId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

        }}}
