package com.app.fm001.navigation

sealed class ClientScreen(val route: String) {
    object Home : ClientScreen("client_home")
    object Search : ClientScreen("search_photographers")
    object Jobs : ClientScreen("client_jobs")
    object Profile : ClientScreen("client_profile")
    object PrivacySettings : ClientScreen("privacy_settings")
    object Messages : ClientScreen("client_messages")
} 