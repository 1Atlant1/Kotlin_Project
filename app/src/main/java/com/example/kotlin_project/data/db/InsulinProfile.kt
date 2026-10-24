package com.example.kotlin_project.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "insulin_profiles")
data class InsulinProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: String
)