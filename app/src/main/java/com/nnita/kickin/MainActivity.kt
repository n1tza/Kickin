package com.nnita.kickin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nnita.kickin.ui.home.HomeScreen
import com.nnita.kickin.ui.home.HomeViewModel
import com.nnita.kickin.ui.home.MatchMode
import com.nnita.kickin.ui.info.InfoScreen
import com.nnita.kickin.ui.legal.LegalScreen
import com.nnita.kickin.ui.matchdetail.MatchDetailScreen
import com.nnita.kickin.ui.settings.SettingsScreen
import com.nnita.kickin.ui.settings.SettingsViewModel
import com.nnita.kickin.ui.standings.StandingsScreen
import com.nnita.kickin.ui.theme.KickinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KickinTheme {
                KickinApp()
            }
        }
    }
}

private data class NavItem(
    val screen: Screen,
    val labelRes: Int,
    val icon: @Composable () -> Unit
)

@Composable
fun KickinApp() {
    val navController = rememberNavController()
    val navItems = listOf(
        NavItem(Screen.Home, R.string.nav_home) {
            Icon(Icons.Default.Home, contentDescription = stringResource(R.string.nav_home))
        },
        NavItem(Screen.Standings, R.string.nav_standings) {
            Icon(Icons.Default.List, contentDescription = stringResource(R.string.nav_standings))
        },
        NavItem(Screen.Settings, R.string.nav_settings) {
            Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.nav_settings))
        },
        NavItem(Screen.Info, R.string.nav_info) {
            Icon(Icons.Default.Info, contentDescription = stringResource(R.string.nav_info))
        },
        NavItem(Screen.Legal, R.string.nav_legal) {
            Icon(Icons.Default.Shield, contentDescription = stringResource(R.string.nav_legal))
        }
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = navItems.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    navItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.screen.route
                        } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = item.icon,
                            label = { Text(text = stringResource(item.labelRes)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel()
                val leagues by homeViewModel.leagues.collectAsState()
                val selectedLeague by homeViewModel.selectedLeague.collectAsState()
                val matchMode by homeViewModel.matchMode.collectAsState()
                val selectedDate by homeViewModel.selectedDate.collectAsState()
                val liveFixtures by homeViewModel.liveFixtures.collectAsState()
                val otherFixtures by homeViewModel.otherFixtures.collectAsState()
                val timeFormat by homeViewModel.timeFormat.collectAsState()
                HomeScreen(
                    leagues = leagues,
                    selectedLeague = selectedLeague,
                    matchMode = matchMode,
                    selectedDate = selectedDate,
                    liveFixtures = liveFixtures,
                    otherFixtures = otherFixtures,
                    onLeagueSelected = homeViewModel::selectLeague,
                    onModeChanged = homeViewModel::selectMode,
                    onDateSelected = homeViewModel::selectDate,
                    onFixtureClick = { id ->
                        navController.navigate(Screen.MatchDetail.createRoute(id))
                    },
                    timeFormat = timeFormat
                )
            }
            composable(Screen.Standings.route) {
                StandingsScreen()
            }
            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = viewModel()
                val selectedLeagueId by settingsViewModel.selectedLeagueId.collectAsState()
                val displayMode by settingsViewModel.displayMode.collectAsState()
                val timeFormat by settingsViewModel.timeFormat.collectAsState()
                val dataSource by settingsViewModel.dataSource.collectAsState()
                SettingsScreen(
                    selectedLeagueId = selectedLeagueId,
                    displayMode = displayMode,
                    timeFormat = timeFormat,
                    dataSource = dataSource,
                    onLeagueSelected = settingsViewModel::selectLeague,
                    onDisplayModeSelected = settingsViewModel::selectDisplayMode,
                    onTimeFormatChanged = settingsViewModel::setTimeFormat,
                    onDataSourceChanged = settingsViewModel::setDataSource
                )
            }
            composable(Screen.Info.route) {
                InfoScreen()
            }
            composable(Screen.Legal.route) {
                LegalScreen()
            }
            composable(Screen.MatchDetail.route) { backStackEntry ->
                val fixtureId = backStackEntry.arguments
                    ?.getString("fixtureId")?.toIntOrNull() ?: return@composable
                MatchDetailScreen(
                    fixtureId = fixtureId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
