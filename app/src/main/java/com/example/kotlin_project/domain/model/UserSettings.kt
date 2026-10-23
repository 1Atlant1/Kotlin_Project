package com.example.kotlin_project.domain.model

data class UserSettings(
    val targetGlucose: Double = 6.0,
    val insulinSensitivity: Double = 3.0,
    val carbRatios: Map<String, Double> = mapOf(
        "Breakfast" to 1.5,
        "Lunch" to 1.3,
        "Dinner" to 1.4,
        "Breakfast_sport" to 1.0,
        "Lunch_sport" to 1.0,
        "Dinner_sport" to 1.1
    ),
    val mealTimes: Map<String, Pair<String, String>> = mapOf(
        "Breakfast" to Pair("06:00", "11:00"),
        "Lunch" to Pair("13:00", "14:00"),
        "Dinner" to Pair("18:00", "19:00")
    ),
    val selectedInsulin: String = "NovoRapid",
    val availableInsulins: List<String> = listOf("NovoRapid", "Humalog", "Apidra"),
    val isDarkTheme: Boolean = false,
    val language: String = "RU"
)