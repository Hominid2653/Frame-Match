package com.app.fm001.ui.screens.client.dashboard.components

import ClientScreen
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val screen: ClientScreen,
    val icon: @Composable () -> Unit,
    val label: String,
    val badgeCount: Int = 0,
    val badgeIcon: ImageVector? = null
)

@Composable
fun ClientBottomNav(
    navController: NavController,
    items: List<BottomNavItem>,
    unreadMessageCount: Int = 0
) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            val isMessagesTab = item.screen.route == "client_messages"
            val showBadge = isMessagesTab && unreadMessageCount > 0

            NavigationBarItem(
                icon = {
                    BadgedBox(
                        badge = {
                            when {
                                showBadge -> {
                                    Badge {
                                        Text(unreadMessageCount.toString())
                                    }
                                }
                                item.badgeCount > 0 -> {
                                    Badge {
                                        Text(item.badgeCount.toString())
                                    }
                                }
                                item.badgeIcon != null -> {
                                    Badge {
                                        Icon(
                                            item.badgeIcon,
                                            contentDescription = "Notification",
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    ) {
                        item.icon()
                    }
                },
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
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}