package com.app.fm001.navigation

sealed class PhotographerScreen(val route: String) {
    object Home : PhotographerScreen("photographer_home")
    object Bids : PhotographerScreen("photographer_bids")
    object Post : PhotographerScreen("photographer_post")
    object Portfolio : PhotographerScreen("photographer_portfolio")
    object EditPortfolio : PhotographerScreen("edit_portfolio") // Added Edit Portfolio
    object Messages : PhotographerScreen("photographer_messages")
}
