package com.app.fm001.ui.screens.photographer

import PhotographerScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
data class BottomNavItem(
    val screen: PhotographerScreen,
    val icon: @Composable () -> Unit,
    val label: String
)

@Composable
fun PhotographerMainScreen() {
    val navController = rememberNavController()
    
    val navItems = listOf(
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

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navItems.forEach { item ->
                    NavigationBarItem(
                        icon = item.icon,
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { 
                            it.route == item.screen.route 
                        } == true,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = PhotographerScreen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(PhotographerScreen.Home.route) {
                PhotographerHomeScreen()
            }
            composable(PhotographerScreen.Bids.route) {
                PhotographerBidsScreen()
            }
            composable(PhotographerScreen.Post.route) {
                PhotographerPostScreen()
            }
            composable(PhotographerScreen.Portfolio.route) {
                PhotographerPortfolioScreen()
            }
        }
    }
} 