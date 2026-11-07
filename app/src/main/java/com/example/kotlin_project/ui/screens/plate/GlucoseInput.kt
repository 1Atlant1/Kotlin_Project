package com.example.kotlin_project.ui.screens.plate

import com.example.kotlin_project.domain.model.Glucose
import com.example.kotlin_project.domain.model.GlucoseLevel

data class GlucoseInput(val text: String = "") {

    val valueMmol: Double? = Glucose.parse(text)

    val level: GlucoseLevel? = valueMmol?.let(GlucoseLevel::fromMmol)

    val isError: Boolean = text.isNotEmpty() && valueMmol == null
}