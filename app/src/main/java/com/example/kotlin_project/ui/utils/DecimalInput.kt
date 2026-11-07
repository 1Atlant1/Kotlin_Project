package com.example.kotlin_project.ui.utils

fun String.filterDecimalInput(): String = filter { it.isDigit() || it == '.' || it == ',' }

fun String.toDecimalOrNull(): Double? = replace(',', '.').toDoubleOrNull()