package com.brijesh.smartscanner.data.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ScanHistoryViewModelFactory(
    private val repository: ScanHistoryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(ScanHistoryViewModel::class.java)) {
            return ScanHistoryViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}