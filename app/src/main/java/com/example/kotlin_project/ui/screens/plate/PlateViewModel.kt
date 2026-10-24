package com.example.kotlin_project.ui.screens.plate

import androidx.lifecycle.ViewModel
import com.example.kotlin_project.data.db.Dish
import com.example.kotlin_project.domain.calculator.DoseResult
import com.example.kotlin_project.domain.calculator.InsulinCalculator
import com.example.kotlin_project.domain.model.PlateItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PlateViewModel : ViewModel() {

    private val _plateItems = MutableStateFlow<List<PlateItem>>(emptyList())
    val plateItems: StateFlow<List<PlateItem>> = _plateItems.asStateFlow()

    private val _glucose = MutableStateFlow("")
    val glucose: StateFlow<String> = _glucose.asStateFlow()

    private val _mealPeriod = MutableStateFlow("Lunch")
    val mealPeriod: StateFlow<String> = _mealPeriod.asStateFlow()

    private val _isSportsMode = MutableStateFlow(false)
    val isSportsMode: StateFlow<Boolean> = _isSportsMode.asStateFlow()

    private val _selectedInsulin = MutableStateFlow("NovoRapid")
    val selectedInsulin: StateFlow<String> = _selectedInsulin.asStateFlow()

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

    fun updateGlucose(value: String) {
        _glucose.value = value
    }

    fun updateMealPeriod(period: String) {
        _mealPeriod.value = period
    }

    fun toggleSportsMode() {
        _isSportsMode.update { !it }
    }

    fun updateSelectedInsulin(insulin: String) {
        _selectedInsulin.value = insulin
    }

    fun calculateDose(
        targetGlucose: Double,
        insulinSensitivity: Double,
        carbRatio: Double,
        lowGlucoseWarning: String,
        borderlineWarning: String
    ): DoseResult {
        val currentGlucose = _glucose.value.toDoubleOrNull() ?: 0.0

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
    }
}