package com.example.kotlin_project.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.kotlin_project.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    constructor(context: Context) : this(context.applicationContext.settingsDataStore)

    val settings: Flow<UserSettings> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences -> preferences.toUserSettings() }

    suspend fun save(settings: UserSettings) {
        dataStore.edit { preferences ->
            preferences[KEY_TARGET_GLUCOSE] = settings.targetGlucose
            preferences[KEY_INSULIN_SENSITIVITY] = settings.insulinSensitivity
            settings.carbRatios.forEach { (meal, ratio) ->
                preferences[carbRatioKey(meal)] = ratio
            }
            settings.mealTimes.forEach { (meal, time) ->
                preferences[mealStartKey(meal)] = time.first
                preferences[mealEndKey(meal)] = time.second
            }
            preferences[KEY_SELECTED_INSULIN] = settings.selectedInsulin.withoutSeparator()
            preferences[KEY_AVAILABLE_INSULINS] = settings.availableInsulins
                .joinToString(INSULIN_SEPARATOR) { it.withoutSeparator() }
            preferences[KEY_DARK_THEME] = settings.isDarkTheme
        }
    }

    private fun Preferences.toUserSettings(): UserSettings {
        val defaults = UserSettings()
        return defaults.copy(
            targetGlucose = this[KEY_TARGET_GLUCOSE] ?: defaults.targetGlucose,
            insulinSensitivity = this[KEY_INSULIN_SENSITIVITY] ?: defaults.insulinSensitivity,
            carbRatios = defaults.carbRatios.mapValues { (meal, defaultRatio) ->
                this[carbRatioKey(meal)] ?: defaultRatio
            },
            mealTimes = defaults.mealTimes.mapValues { (meal, defaultTime) ->
                Pair(
                    this[mealStartKey(meal)] ?: defaultTime.first,
                    this[mealEndKey(meal)] ?: defaultTime.second
                )
            },
            selectedInsulin = this[KEY_SELECTED_INSULIN] ?: defaults.selectedInsulin,
            availableInsulins = this[KEY_AVAILABLE_INSULINS]
                ?.split(INSULIN_SEPARATOR)
                ?.filter { it.isNotBlank() }
                ?.takeIf { it.isNotEmpty() }
                ?: defaults.availableInsulins,
            isDarkTheme = this[KEY_DARK_THEME] ?: defaults.isDarkTheme
        )
    }

    private fun String.withoutSeparator(): String = replace(INSULIN_SEPARATOR, " ")

    private companion object {
        const val INSULIN_SEPARATOR = "\n"

        val KEY_TARGET_GLUCOSE = doublePreferencesKey("target_glucose")
        val KEY_INSULIN_SENSITIVITY = doublePreferencesKey("insulin_sensitivity")
        val KEY_SELECTED_INSULIN = stringPreferencesKey("selected_insulin")
        val KEY_AVAILABLE_INSULINS = stringPreferencesKey("available_insulins")
        val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")

        fun carbRatioKey(meal: String) = doublePreferencesKey("carb_ratio_$meal")
        fun mealStartKey(meal: String) = stringPreferencesKey("meal_start_$meal")
        fun mealEndKey(meal: String) = stringPreferencesKey("meal_end_$meal")
    }
}