package com.example.kotlin_project.ui.screens.plate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_project.data.cgm.CgmRepository
import com.example.kotlin_project.data.db.Dish
import com.example.kotlin_project.domain.calculator.DoseResult
import com.example.kotlin_project.domain.calculator.InsulinCalculator
import com.example.kotlin_project.domain.model.Glucose
import com.example.kotlin_project.domain.model.PlateItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlateViewModel(
    private val cgmRepository: CgmRepository
) : ViewModel() {

    private val _plateItems = MutableStateFlow<List<PlateItem>>(emptyList())
    val plateItems: StateFlow<List<PlateItem>> = _plateItems.asStateFlow()

    private val _glucose = MutableStateFlow(GlucoseInput())
    val glucose: StateFlow<GlucoseInput> = _glucose.asStateFlow()

    private val _cgmSyncResult = MutableStateFlow<CgmSyncResult?>(null)
    val cgmSyncResult: StateFlow<CgmSyncResult?> = _cgmSyncResult.asStateFlow()

    private val _manualMealPeriod = MutableStateFlow<String?>(null)
    val manualMealPeriod: StateFlow<String?> = _manualMealPeriod.asStateFlow()

    private val _isSportsMode = MutableStateFlow(false)
    val isSportsMode: StateFlow<Boolean> = _isSportsMode.asStateFlow()

    fun addDishToPlate(dish: Dish, weightGrams: Int) {
        _plateItems.update { currentItems ->
            currentItems + PlateItem(dish = dish, weightGrams = weightGrams)
        }
    }

    fun removeDishFromPlate(index: Int) {
        _plateItems.update { currentItems ->
            currentItems.toMutableList().apply { removeAt(index) }
        }
    }

    fun updateGlucose(text: String) {
        if (Glucose.isAllowedInput(text)) {
            _glucose.value = GlucoseInput(text)
        }
    }

    fun syncGlucoseFromCgm() {
        viewModelScope.launch {
            val reading = cgmRepository.latestReading.first()
            val now = System.currentTimeMillis()

            _cgmSyncResult.value = when {
                reading == null -> CgmSyncResult.NoData
                !reading.isFresh(now) -> CgmSyncResult.Outdated
                else -> {
                    _glucose.value = GlucoseInput(Glucose.format(reading.glucoseMmol))
                    CgmSyncResult.Success(reading)
                }
            }
        }
    }

    fun onCgmSyncResultShown() {
        _cgmSyncResult.value = null
    }

    fun updateMealPeriod(period: String) {
        _manualMealPeriod.value = period
    }

    fun resetMealPeriod() {
        _manualMealPeriod.value = null
    }

    fun toggleSportsMode() {
        _isSportsMode.update { !it }
    }

    fun calculateDose(
        targetGlucose: Double,
        insulinSensitivity: Double,
        carbRatio: Double,
        lowGlucoseWarning: String,
        borderlineWarning: String
    ): DoseResult? {
        val currentGlucose = _glucose.value.valueMmol ?: return null

        return InsulinCalculator.calculateDose(
            plateItems = _plateItems.value,
            currentGlucose = currentGlucose,
            carbRatio = carbRatio,
            isSportsMode = _isSportsMode.value,
            targetGlucose = targetGlucose,
            insulinSensitivity = insulinSensitivity,
            lowGlucoseWarning = lowGlucoseWarning,
            borderlineWarning = borderlineWarning
        )
    }

    fun clearPlate() {
        _plateItems.value = emptyList()
        _manualMealPeriod.value = null
    }
}