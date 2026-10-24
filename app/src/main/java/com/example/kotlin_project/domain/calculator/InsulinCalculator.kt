package com.example.kotlin_project.domain.calculator

import com.example.kotlin_project.domain.model.PlateItem

data class DoseResult(
    val mealBolus: Double,
    val correction: Double,
    val totalDose: Double,
    val isLowGlucose: Boolean,
    val warning: String?
)

object InsulinCalculator {

    fun calculateDose(
        plateItems: List<PlateItem>,
        currentGlucose: Double,
        carbRatio: Double,
        isSportsMode: Boolean = false,
        targetGlucose: Double = 6.0,
        insulinSensitivity: Double = 3.0,
        lowGlucoseWarning: String = "Glucose below 3.9 mmol/L. Eat 1-2 XE of fast carbs, wait 15 minutes and recalculate.",
        borderlineWarning: String = "Glucose is on the lower side. Consider reducing dose by 30%."
    ): DoseResult {
        if (currentGlucose < 3.9) {
            return DoseResult(
                mealBolus = 0.0,
                correction = 0.0,
                totalDose = 0.0,
                isLowGlucose = true,
                warning = lowGlucoseWarning
            )
        }

        val totalXE = plateItems.sumOf { it.xe }
        val mealBolus = totalXE * carbRatio

        val correction = if (currentGlucose > targetGlucose) {
            (currentGlucose - targetGlucose) / insulinSensitivity
        } else {
            0.0
        }

        val totalDose = mealBolus + correction

        val warning = if (currentGlucose in 3.9..5.0) {
            borderlineWarning
        } else null

        return DoseResult(
            mealBolus = mealBolus,
            correction = correction,
            totalDose = totalDose,
            isLowGlucose = false,
            warning = warning
        )
    }
}