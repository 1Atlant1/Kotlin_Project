package com.example.kotlin_project

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_project.data.db.AppDatabase
import com.example.kotlin_project.ui.AppNavigation
import com.example.kotlin_project.ui.screens.settings.SettingsViewModel
import com.example.kotlin_project.ui.theme.Kotlin_projectTheme

class MainActivity : AppCompatActivity() {

    private val database by lazy { AppDatabase.getDatabase(applicationContext) }
    private val dishDao by lazy { database.dishDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settings by settingsViewModel.settings.collectAsState()

            Kotlin_projectTheme(darkTheme = settings.isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(dishDao = dishDao)
                }
            }
        }
    }
}