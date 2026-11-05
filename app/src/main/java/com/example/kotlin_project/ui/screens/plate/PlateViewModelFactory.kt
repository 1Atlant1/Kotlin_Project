package com.example.kotlin_project.ui.screens.plate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kotlin_project.data.cgm.CgmRepository

class PlateViewModelFactory(
    private val cgmRepository: CgmRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlateViewModel::class.java)) {
            return PlateViewModel(cgmRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}