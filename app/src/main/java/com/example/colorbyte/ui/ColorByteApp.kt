package com.example.colorbyte.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.colorbyte.ui.navigation.Screen
import com.example.colorbyte.ui.screens.AboutScreen
import com.example.colorbyte.ui.screens.ConverterScreen
import com.example.colorbyte.ui.screens.HistoryScreen
import com.example.colorbyte.ui.screens.SettingsScreen
import com.example.colorbyte.ui.screens.SplashScreen
import com.example.colorbyte.ui.viewmodel.ColorByteViewModel

@Composable
fun ColorByteApp(
    viewModel: ColorByteViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val shouldShowBottomBar = currentRoute != null && currentRoute != Screen.Splash.route

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Screen.bottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                screen.icon?.let { icon ->
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = screen.title
                                    )
                                }
                            },
                            label = { Text(screen.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.semantics {
                                contentDescription = "${screen.title} screen tab"
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(Screen.Converter.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Converter.route) {
                ConverterScreen(
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState,
                    onNavigateToConverter = {
                        navController.navigate(Screen.Converter.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState
                )
            }

            composable(Screen.About.route) {
                AboutScreen()
            }
        }
    }
}
