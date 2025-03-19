package com.app.fm001.navigation

sealed class Screen(val route: String) {
    object Green : Screen("green")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ClientHome : Screen("client_home")
    object PhotographerHome : Screen("photographer_home")
    object EditPortfolio : Screen("edit_portfolio")
}
