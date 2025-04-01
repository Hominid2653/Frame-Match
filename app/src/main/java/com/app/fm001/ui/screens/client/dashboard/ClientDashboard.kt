package com.app.fm001.ui.screens.client.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.fm001.ui.screens.client.dashboard.components.*
import com.app.fm001.ui.screens.client.dashboard.screens.*
import com.app.fm001.ui.screens.shared.messages.MessagesScreen
import com.app.fm001.ui.screens.shared.messages.MessagesViewModel

@Composable
fun ClientDashboard(loggedInUserId: String) {
    val navController = rememberNavController()
    val profileViewModel: ClientProfileViewModel = viewModel()
    val messagesViewModel: MessagesViewModel = viewModel()

    // Track unread messages
    val unreadMessageCount by messagesViewModel.unreadMessageCount.collectAsState()

    // Navigation items with badge support
    val navItems = remember {
        listOf(
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
                label = "My Jobs",
                badgeCount = 3 // Example job notifications
            ),
            BottomNavItem(
                screen = ClientScreen.Profile,
                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                label = "Profile",
                badgeIcon = Icons.Default.Notifications // Example notification icon
            )
        )
    }

    Scaffold(
        bottomBar = {
            ClientBottomNav(
                navController = navController,
                items = navItems,
                unreadMessageCount = unreadMessageCount
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = ClientScreen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(ClientScreen.Home.route) {
                ClientHomeScreen(navController, viewModel())
            }

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

            composable(ClientScreen.Jobs.route) {
                ClientJobsScreen()
            }

            composable(ClientScreen.Profile.route) {
                ClientProfileScreen(
                    viewModel = profileViewModel,
                    navController = navController,

                )
            }

            composable(ClientScreen.PrivacySettings.route) {
                PrivacySettingsScreen(
                    viewModel = profileViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

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
                    senderId = senderId,
                    receiverId = receiverId,
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = messagesViewModel
                )
            }
        }
    }
}