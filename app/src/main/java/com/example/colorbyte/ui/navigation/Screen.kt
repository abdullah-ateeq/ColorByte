package com.example.colorbyte.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    object Splash : Screen("splash", "Splash")
    object Converter : Screen("converter", "Converter", Icons.Default.Palette)
    object History : Screen("history", "History", Icons.Default.History)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object About : Screen("about", "About", Icons.Default.Info)

    companion object {
        val bottomNavItems = listOf(
            Converter,
            History,
            Settings,
            About
        )
    }
}
