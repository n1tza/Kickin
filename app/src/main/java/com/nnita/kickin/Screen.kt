package com.nnita.kickin

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Standings : Screen("standings")
    data object Settings : Screen("settings")
    data object Info : Screen("info")
    data object Legal : Screen("legal")
    data object MatchDetail : Screen("match_detail/{fixtureId}") {
        fun createRoute(fixtureId: Int) = "match_detail/$fixtureId"
    }
}
