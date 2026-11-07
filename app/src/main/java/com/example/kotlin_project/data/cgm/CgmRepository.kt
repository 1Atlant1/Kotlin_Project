package com.example.kotlin_project.data.cgm

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.kotlin_project.domain.model.CgmReading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.cgmDataStore: DataStore<Preferences> by preferencesDataStore(name = "cgm_reading")

class CgmRepository(context: Context) {

    private val dataStore = context.applicationContext.cgmDataStore

    val latestReading: Flow<CgmReading?> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences ->
            val glucoseMmol = preferences[KEY_GLUCOSE_MMOL]
            val timestampMillis = preferences[KEY_TIMESTAMP_MILLIS]
            if (glucoseMmol != null && timestampMillis != null) {
                CgmReading(glucoseMmol, timestampMillis)
            } else {
                null
            }
        }

    suspend fun saveReading(reading: CgmReading) {
        dataStore.edit { preferences ->
            preferences[KEY_GLUCOSE_MMOL] = reading.glucoseMmol
            preferences[KEY_TIMESTAMP_MILLIS] = reading.timestampMillis
        }
    }

    private companion object {
        val KEY_GLUCOSE_MMOL = doublePreferencesKey("glucose_mmol")
        val KEY_TIMESTAMP_MILLIS = longPreferencesKey("timestamp_millis")
    }
}