package com.example.kotlin_project.ui.screens.plate

import com.example.kotlin_project.domain.model.CgmReading

sealed interface CgmSyncResult {

    data class Success(val reading: CgmReading) : CgmSyncResult

    data object NoData : CgmSyncResult

    data object Outdated : CgmSyncResult
}