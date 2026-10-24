package com.example.kotlin_project.ui.screens.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import com.example.kotlin_project.domain.model.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel : ViewModel() {

    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    fun updateTargetGlucose(value: Double) {
        _settings.update { it.copy(targetGlucose = value) }
    }

    fun updateInsulinSensitivity(value: Double) {
        _settings.update { it.copy(insulinSensitivity = value) }
    }

    fun updateCarbRatio(meal: String, value: Double) {
        _settings.update { current ->
            val newRatios = current.carbRatios.toMutableMap()
            newRatios[meal] = value
            current.copy(carbRatios = newRatios)
        }
    }

    fun updateMealTime(meal: String, start: String, end: String) {
        _settings.update { current ->
            val newTimes = current.mealTimes.toMutableMap()
            newTimes[meal] = Pair(start, end)
            current.copy(mealTimes = newTimes)
        }
    }

    fun updateSelectedInsulin(insulin: String) {
        _settings.update { it.copy(selectedInsulin = insulin) }
    }

    fun addCustomInsulin(name: String) {
        if (name.isNotBlank() && !_settings.value.availableInsulins.contains(name)) {
            _settings.update { current ->
                current.copy(
                    availableInsulins = current.availableInsulins + name,
                    selectedInsulin = name
                )
            }
        }
    }

    fun setDarkTheme(isDark: Boolean) {
        _settings.update { it.copy(isDarkTheme = isDark) }
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }


    fun updateLanguage(lang: String) {
        _settings.update { it.copy(language = lang) }
        val localeList = LocaleListCompat.forLanguageTags(if (lang == "RU") "ru" else "en")
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    fun saveSettings() {
        println("Settings saved: ${_settings.value}")
    }
}