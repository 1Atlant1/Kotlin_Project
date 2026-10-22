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
    private const val TARGET_GLUCOSE = 6.0 // ммоль/л

    private const val INSULIN_SENSITIVITY = 3.0 // ммоль/л на 1 единицу инсулина

    fun calculateDose(
        plateItems: List<PlateItem>,
        currentGlucose: Double,
        carbRatio: Double,  // УК (углеводный коэффициент): сколько единиц инсулина на 1 XE
        isSportsMode: Boolean = false
    ): DoseResult {

        if (currentGlucose < 3.9) {
            return DoseResult(
                mealBolus = 0.0,
                correction = 0.0,
                totalDose = 0.0,
                isLowGlucose = true,
                warning = "Glucose below 3.9 mmol/L. Eat 1-2 XE of fast carbs, wait 15 minutes and recalculate."
            )
        }

        // Считаем общие XE
        val totalXE = plateItems.sumOf { it.xe }

        // Болюс на еду
        val mealBolus = totalXE * carbRatio


        val correction = if (currentGlucose > TARGET_GLUCOSE) {
            (currentGlucose - TARGET_GLUCOSE) / INSULIN_SENSITIVITY
        } else {
            0.0
        }


        val totalDose = mealBolus + correction


        val warning = if (currentGlucose in 3.9..5.0) {
            "Glucose is on the lower side. Consider reducing dose by 30%."
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