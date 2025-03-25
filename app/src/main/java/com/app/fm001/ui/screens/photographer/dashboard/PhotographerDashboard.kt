package com.app.fm001.ui.screens.photographer.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
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
fun PhotographerDashboard(loggedInUserId: String, loggedInUserEmail: String) {
    val navController = rememberNavController()

    PhotographerDashboardContent(
        navController = navController,
        loggedInUserId = loggedInUserId,
        loggedInUserEmail = loggedInUserEmail // Pass email correctly
    )
}

@Composable
private fun PhotographerDashboardContent(
    navController: NavHostController,
    loggedInUserId: String,
    loggedInUserEmail: String // Accept email here
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
                        navController.navigate("messages/$loggedInUserId/$clientId")
                    }
                )
            }
            composable(PhotographerScreen.Bids.route) {
                BidsScreen(
                    loggedInUserId = loggedInUserId,
                    loggedInUserEmail = loggedInUserEmail, // Now correctly passed
                    onNavigateToMessages = { photographerEmail, clientEmail ->
                        navController.navigate("messages/$photographerEmail/$clientEmail")
                    }
                )
            }
            composable(PhotographerScreen.Post.route) {
                PostScreen()
            }

            composable(PhotographerScreen.Portfolio.route) {
                PortfolioScreen(navController)
            }
            composable(PhotographerScreen.EditPortfolio.route) {
                EditPortfolioScreen()
            }
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
                    senderId = senderId,
                    receiverId = receiverId,
                    onNavigateBack = { navController.popBackStack() } // ✅ Correct function type
                )
            }

        }
    }
}

// Extract senderId and receiverId from the navigation arguments
private fun extractIds(backStackEntry: NavBackStackEntry): Pair<String, String> {
    val senderId = backStackEntry.arguments?.getString("senderId").orEmpty()
    val receiverId = backStackEntry.arguments?.getString("receiverId").orEmpty()
    return senderId to receiverId
}
