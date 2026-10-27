package com.brijesh.smartscanner.data.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brijesh.smartscanner.data.local.ScanHistoryEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScanHistoryViewModel(
    private val repository: ScanHistoryRepository
) : ViewModel() {

    val history = repository.observeHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val count = repository.observeCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )

    fun saveScan(content: String, contentType: String) {
        viewModelScope.launch {
            repository.saveScan(content, contentType)
        }
    }

    fun deleteScan(scan: ScanHistoryEntity) {
        viewModelScope.launch {
            repository.deleteScan(scan.id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}