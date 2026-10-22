package com.example.kotlin_project.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kotlin_project.data.db.DishDao
import com.example.kotlin_project.ui.screens.category.CategoryScreen
import com.example.kotlin_project.ui.screens.home.HomeScreen
import com.example.kotlin_project.ui.screens.home.HomeViewModel
import com.example.kotlin_project.ui.screens.home.HomeViewModelFactory

@Composable
fun AppNavigation(dishDao: DishDao) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {


        composable("home") {

            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(dishDao)
            )
            HomeScreen(
                viewModel = homeViewModel,
                onCategoryClick = { category ->
                    navController.navigate("category/$category")
                }
            )
        }

        composable("category/{category}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            CategoryScreen(
                dishDao = dishDao,
                categoryName = category,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}