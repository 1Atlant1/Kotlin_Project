package com.example.kotlin_project

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.kotlin_project.data.cgm.CgmRepository
import com.example.kotlin_project.data.db.AppDatabase
import com.example.kotlin_project.data.settings.SettingsRepository
import com.example.kotlin_project.ui.AppNavigation
import com.example.kotlin_project.ui.screens.settings.SettingsViewModel
import com.example.kotlin_project.ui.screens.settings.SettingsViewModelFactory
import com.example.kotlin_project.ui.theme.Kotlin_projectTheme

class MainActivity : AppCompatActivity() {

    private val database by lazy { AppDatabase.getDatabase(applicationContext) }
    private val dishDao by lazy { database.dishDao() }
    private val cgmRepository by lazy { CgmRepository(applicationContext) }
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(SettingsRepository(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsViewModel.refreshLanguage()

        setContent {
            val settings by settingsViewModel.settings.collectAsState()
            val isLoaded by settingsViewModel.isLoaded.collectAsState()
            if (isLoaded) {
                Kotlin_projectTheme(darkTheme = settings.isDarkTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation(
                            dishDao = dishDao,
                            cgmRepository = cgmRepository,
                            settingsViewModel = settingsViewModel
                        )
                    }
                }
            }
        }
    }
}