package com.app.fm001.ui.screens.photographer.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.fm001.ui.screens.photographer.dashboard.screens.*
import com.app.fm001.ui.screens.photographer.dashboard.components.BottomNavItem
import com.app.fm001.ui.screens.photographer.dashboard.components.PhotographerBottomNav
import com.app.fm001.ui.screens.shared.messages.MessagesScreen

@Composable
fun PhotographerDashboard(loggedInUserId: String) { // Add loggedInUserId parameter
    val navController = rememberNavController()

    PhotographerDashboardContent(
        navController = navController,
        loggedInUserId = loggedInUserId // Pass loggedInUserId to the content
    )
}

@Composable
private fun PhotographerDashboardContent(
    navController: NavHostController,
    loggedInUserId: String // Add loggedInUserId parameter
) {
    val navItems = remember {
        listOf(
            BottomNavItem(
                screen = PhotographerScreen.Home,
                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                label = "Home"
            ),
            BottomNavItem(
                screen = PhotographerScreen.Bids,
                icon = { Icon(Icons.Default.WorkHistory, contentDescription = null) },
                label = "Bids"
            ),
            BottomNavItem(
                screen = PhotographerScreen.Post,
                icon = { Icon(Icons.Default.AddCircle, contentDescription = null) },
                label = "Post"
            ),
            BottomNavItem(
                screen = PhotographerScreen.Portfolio,
                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                label = "Portfolio"
            )
        )
    }

    Scaffold(
        bottomBar = {
            PhotographerBottomNav(
                navController = navController,
                items = navItems
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = PhotographerScreen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(PhotographerScreen.Home.route) {
                HomeScreen(
                    onNavigateToMessages = { clientId ->
                        // Navigate to Messages screen with senderId and receiverId
                        navController.navigate("messages/$loggedInUserId/$clientId")
                    }
                )
            }
            composable(PhotographerScreen.Bids.route) {
                BidsScreen(
                    loggedInUserId = loggedInUserId, // Pass the loggedInUserId to BidsScreen
                    onNavigateToMessages = { photographerId, clientId ->
                        // Navigate to Messages screen with senderId and receiverId
                        navController.navigate("messages/$photographerId/$clientId")
                    }
                )
            }
            composable(PhotographerScreen.Post.route) {
                PostScreen() // ✅ Pass the navController if needed
            }
            composable(PhotographerScreen.Portfolio.route) {
                PortfolioScreen(navController) // ✅ Pass the navController
            }
            composable(PhotographerScreen.EditPortfolio.route) {
                EditPortfolioScreen()
            }

            // Messages Screen with senderId and receiverId as arguments
            composable(
                "messages/{senderId}/{receiverId}",
                arguments = listOf(
                    navArgument("senderId") { type = NavType.StringType },
                    navArgument("receiverId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val senderId = backStackEntry.arguments?.getString("senderId") ?: ""
                val receiverId = backStackEntry.arguments?.getString("receiverId") ?: ""
                MessagesScreen(
                    loggedInUserId = loggedInUserId, // Pass loggedInUserId
                    senderId = senderId,
                    receiverId = receiverId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToConversation = { sender, receiver ->
                        navController.navigate("messages/$sender/$receiver")
                    }
                )
            }
        }
    }
}
