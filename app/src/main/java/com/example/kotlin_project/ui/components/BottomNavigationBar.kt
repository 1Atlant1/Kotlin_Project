package com.example.kotlin_project.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object MyPlate : BottomNavItem("plate", "My Plate", Icons.Default.Restaurant)
    object Settings : BottomNavItem("settings", "Settings", Icons.Default.Settings)
}