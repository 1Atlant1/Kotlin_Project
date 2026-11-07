package com.example.kotlin_project.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_project.data.db.AppDatabase
import com.example.kotlin_project.data.db.DishDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val dishDao: DishDao) : ViewModel() {

    val categories: StateFlow<List<String>> = dishDao.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getDishCount(category: String): StateFlow<Int> {
        return dishDao.getDishCountByCategory(category)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )
    }
}