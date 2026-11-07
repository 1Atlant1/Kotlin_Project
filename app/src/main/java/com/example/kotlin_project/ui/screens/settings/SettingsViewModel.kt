package com.example.kotlin_project.ui.screens.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_project.data.settings.SettingsRepository
import com.example.kotlin_project.domain.model.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import java.util.Locale

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    init {
        viewModelScope.launch {
            val savedSettings = repository.settings.first()
            _settings.value = savedSettings.copy(language = currentAppLanguage())
            _isLoaded.value = true
        }
    }

    fun updateTargetGlucose(value: Double) {
        updateAndSave { it.copy(targetGlucose = value) }
    }

    fun updateInsulinSensitivity(value: Double) {
        updateAndSave { it.copy(insulinSensitivity = value) }
    }

    fun updateCarbRatio(meal: String, value: Double) {
        updateAndSave { current ->
            val newRatios = current.carbRatios.toMutableMap()
            newRatios[meal] = value
            current.copy(carbRatios = newRatios)
        }
    }

    fun updateMealTime(meal: String, start: String, end: String) {
        updateAndSave { current ->
            val newTimes = current.mealTimes.toMutableMap()
            newTimes[meal] = Pair(start, end)
            current.copy(mealTimes = newTimes)
        }
    }

    fun updateSelectedInsulin(insulin: String) {
        updateAndSave { it.copy(selectedInsulin = insulin) }
    }

    fun addCustomInsulin(name: String) {
        if (name.isNotBlank() && !_settings.value.availableInsulins.contains(name)) {
            updateAndSave { current ->
                current.copy(
                    availableInsulins = current.availableInsulins + name,
                    selectedInsulin = name
                )
            }
        }
    }

    fun setDarkTheme(isDark: Boolean) {
        updateAndSave { it.copy(isDarkTheme = isDark) }
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun updateLanguage(lang: String) {
        _settings.update { it.copy(language = lang) }
        val localeList = LocaleListCompat.forLanguageTags(if (lang == "RU") "ru" else "en")
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    fun refreshLanguage() {
        _settings.update { it.copy(language = currentAppLanguage()) }
    }

    fun saveSettings() {
        val currentSettings = _settings.value
        viewModelScope.launch { repository.save(currentSettings) }
    }

    private fun updateAndSave(transform: (UserSettings) -> UserSettings) {
        val newSettings = _settings.updateAndGet(transform)
        viewModelScope.launch { repository.save(newSettings) }
    }

    private fun currentAppLanguage(): String {
        val locale = AppCompatDelegate.getApplicationLocales()[0] ?: Locale.getDefault()
        return if (locale.language == "ru") "RU" else "EN"
    }
}