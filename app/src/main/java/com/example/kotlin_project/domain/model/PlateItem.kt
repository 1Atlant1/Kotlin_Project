package com.example.kotlin_project.domain.model

import com.example.kotlin_project.data.db.Dish

data class PlateItem(
    val dish: Dish,
    val weightGrams: Int
) {
    // Углеводы в порции
    val carbsInPortion: Double
        get() = dish.carbsPer100g * weightGrams / 100.0

    // Хлебные единицы (1 XE = 12 г углеводов)
    val xe: Double
        get() = carbsInPortion / 12.0
}