package com.example.kotlin_project.domain.model

import java.util.Locale
import kotlin.math.roundToInt

object Glucose {

    const val MGDL_PER_MMOL = 18.0182

    const val MIN_MMOL = 1.0

    const val MAX_MMOL = 33.3

    private val INPUT_PATTERN = Regex("""\d{0,2}([.,]\d?)?""")

    fun isAllowedInput(text: String): Boolean = INPUT_PATTERN.matches(text)

    fun parse(text: String): Double? =
        text.replace(',', '.')
            .toDoubleOrNull()
            ?.takeIf { it in MIN_MMOL..MAX_MMOL }

    fun format(mmol: Double): String = String.format(Locale.ROOT, "%.1f", mmol)

    fun formatThreshold(mmol: Double): String =
        if (mmol % 1.0 == 0.0) mmol.roundToInt().toString() else format(mmol)

    fun mgdlToMmol(mgdl: Double): Double = (mgdl / MGDL_PER_MMOL * 10).roundToInt() / 10.0
}