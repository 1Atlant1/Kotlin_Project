package com.example.kotlin_project.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dishes")
data class Dish(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val nameRu: String = "",
    val category: String,
    val carbsPer100g: Double,
    val proteinPer100g: Double = 0.0,
    val fatPer100g: Double = 0.0,
    val caloriesPer100g: Double = 0.0,
    val isDrink: Boolean = false,
    val isCustom: Boolean = false,
    val imagePath: String? = null
)