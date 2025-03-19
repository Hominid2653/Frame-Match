package com.app.fm001.ui.screens.photographer.dashboard


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.fm001.navigation.PhotographerScreen
import com.app.fm001.ui.screens.photographer.dashboard.screens.*
import com.app.fm001.ui.screens.photographer.dashboard.components.BottomNavItem
import com.app.fm001.ui.screens.photographer.dashboard.components.PhotographerBottomNav
import com.app.fm001.ui.screens.shared.messages.MessagesScreen

@Composable
fun PhotographerDashboard() {
    val navController = rememberNavController()

    PhotographerDashboardContent(
        navController = navController
    )
}

@Composable
private fun PhotographerDashboardContent(
    navController: NavHostController
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
                        navController.navigate(PhotographerScreen.Messages.route)
                    }
                )
            }
            composable(PhotographerScreen.Bids.route) {
                BidsScreen(
                    onNavigateToMessages = { clientId ->
                        navController.navigate(PhotographerScreen.Messages.route)
                    }
                )
            }
            composable(PhotographerScreen.Post.route) {
                PostScreen() // ✅ Pass the navController
            }
            composable(PhotographerScreen.Portfolio.route) {
                PortfolioScreen(navController) // ✅ Pass the navController
            }
            composable(PhotographerScreen.EditPortfolio.route) { EditPortfolioScreen() }



            composable(PhotographerScreen.Messages.route) {
                MessagesScreen(
                    viewModel = viewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}