package com.example.kotlin_project.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.kotlin_project.R
import com.example.kotlin_project.data.db.Dish
import java.util.Locale

fun getCurrentLanguage(): String {
    return Locale.getDefault().language
}

@Composable
fun Dish.getDisplayName(): String {
    val lang = getCurrentLanguage()
    return if (lang == "ru" && nameRu.isNotBlank()) {
        nameRu
    } else {
        name
    }
}

@Composable
fun getCategoryDisplayName(category: String): String {
    return when (category) {
        "Soups" -> stringResource(R.string.category_soups)
        "Hot dishes" -> stringResource(R.string.category_hot_dishes)
        "Sweets" -> stringResource(R.string.category_sweets)
        "Drinks" -> stringResource(R.string.category_drinks)
        "Fruits" -> stringResource(R.string.category_fruits)
        "Vegetables" -> stringResource(R.string.category_vegetables)
        "Dairy" -> stringResource(R.string.category_dairy)
        "Bread & grains" -> stringResource(R.string.category_bread_grains)
        else -> category
    }
}