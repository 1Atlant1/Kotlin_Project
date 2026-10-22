package com.example.kotlin_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.kotlin_project.data.db.AppDatabase
import com.example.kotlin_project.ui.screens.home.HomeScreen
import com.example.kotlin_project.ui.screens.home.HomeViewModel
import com.example.kotlin_project.ui.theme.Kotlin_projectTheme

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(applicationContext) }
    private val dishDao by lazy { database.dishDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = HomeViewModel(dishDao)

        setContent {
            Kotlin_projectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(
                        viewModel = viewModel,
                        onCategoryClick = { category ->
                            println("Clicked on category: $category")
                        }
                    )
                }
            }
        }
    }
}