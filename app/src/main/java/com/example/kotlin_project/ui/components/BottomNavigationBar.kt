package com.example.kotlin_project.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.kotlin_project.R

sealed class BottomNavItem(
    val route: String,
    val labelResId: Int,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", R.string.nav_home, Icons.Default.Home)
    object MyPlate : BottomNavItem("plate", R.string.nav_my_plate, Icons.Default.Restaurant)
    object Settings : BottomNavItem("settings", R.string.nav_settings, Icons.Default.Settings)
}