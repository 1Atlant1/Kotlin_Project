package com.example.kotlin_project.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.example.kotlin_project.data.db.DishDao
import com.example.kotlin_project.ui.components.BottomNavItem
import com.example.kotlin_project.ui.screens.category.AddDishScreen
import com.example.kotlin_project.ui.screens.category.CategoryScreen
import com.example.kotlin_project.ui.screens.dish.DishScreen
import com.example.kotlin_project.ui.screens.home.HomeScreen
import com.example.kotlin_project.ui.screens.home.HomeViewModel
import com.example.kotlin_project.ui.screens.home.HomeViewModelFactory
import com.example.kotlin_project.ui.screens.plate.PlateScreen
import com.example.kotlin_project.ui.screens.plate.PlateViewModel
import com.example.kotlin_project.ui.screens.settings.CarbRatiosScreen
import com.example.kotlin_project.ui.screens.settings.MealTimesScreen
import com.example.kotlin_project.ui.screens.settings.SettingsScreen
import com.example.kotlin_project.ui.screens.settings.SettingsViewModel

@Composable
fun AppNavigation(dishDao: DishDao) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val plateViewModel: PlateViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()

    val showBottomBar = currentRoute == "home" || currentRoute == "plate" || currentRoute == "settings"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val items = listOf(BottomNavItem.Home, BottomNavItem.MyPlate, BottomNavItem.Settings)
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(stringResource(item.labelResId)) },
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(dishDao))
                HomeScreen(
                    viewModel = homeViewModel,
                    onCategoryClick = { navController.navigate("category/$it") },
                    onPlateClick = { navController.navigate("plate") },
                    onSettingsClick = { navController.navigate("settings") }
                )
            }
            composable("category/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                CategoryScreen(
                    dishDao = dishDao,
                    categoryName = category,
                    onBackClick = { navController.popBackStack() },
                    onDishClick = { navController.navigate("dish/$it") },
                    onAddDishClick = { navController.navigate("add_dish/$category") }
                )
            }
            composable("dish/{dishId}") { backStackEntry ->
                val dishId = (backStackEntry.arguments?.getString("dishId") ?: "0").toIntOrNull() ?: 0
                DishScreen(
                    dishDao = dishDao,
                    dishId = dishId,
                    onBackClick = { navController.popBackStack() },
                    onAddToPlate = { dish, weight ->
                        plateViewModel.addDishToPlate(dish, weight)
                        navController.popBackStack()
                    }
                )
            }
            composable("add_dish/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                AddDishScreen(
                    dishDao = dishDao,
                    defaultCategory = category,
                    onBackClick = { navController.popBackStack() },
                    onDishSaved = { navController.popBackStack() }
                )
            }
            composable("plate") {
                PlateScreen(
                    viewModel = plateViewModel,
                    settingsViewModel = settingsViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable("settings") {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onMealTimesClick = { navController.navigate("meal_times") },
                    onCarbRatiosClick = { navController.navigate("carb_ratios") },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable("meal_times") {
                MealTimesScreen(
                    viewModel = settingsViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable("carb_ratios") {
                CarbRatiosScreen(
                    viewModel = settingsViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}