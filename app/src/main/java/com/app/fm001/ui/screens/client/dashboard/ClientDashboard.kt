package com.app.fm001.ui.screens.client.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.fm001.navigation.ClientScreen
import com.app.fm001.ui.screens.client.dashboard.components.ClientBottomNav
import com.app.fm001.ui.screens.client.dashboard.components.BottomNavItem
import com.app.fm001.ui.screens.client.dashboard.screens.*
import com.app.fm001.ui.screens.shared.messages.MessagesScreen

@Composable
fun ClientDashboard() {
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
            composable(ClientScreen.Home.route) {
                ClientHomeScreen(navController)             }
            composable(ClientScreen.Search.route) {
                SearchPhotographersScreen()
            }
            composable(ClientScreen.Jobs.route) {
                ClientJobsScreen()
            }
            composable(ClientScreen.Profile.route) {
                ClientProfileScreen(
                    viewModel = profileViewModel,
                    onNavigateToPrivacySettings = {
                        navController.navigate(ClientScreen.PrivacySettings.route)
                    }
                )
            }
            composable(ClientScreen.PrivacySettings.route) {
                PrivacySettingsScreen(
                    viewModel = profileViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(ClientScreen.Messages.route) {
                MessagesScreen(
                    viewModel = viewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
} 