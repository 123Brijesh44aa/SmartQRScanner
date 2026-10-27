package com.brijesh.smartscanner.data.repository

import com.brijesh.smartscanner.data.local.ScanHistoryDao
import com.brijesh.smartscanner.data.local.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

class ScanHistoryRepository(
    private val dao: ScanHistoryDao
) {
    fun observeHistory(): Flow<List<ScanHistoryEntity>> {
        return dao.observeAll()
    }

    fun observeCount(): Flow<Int> {
        return dao.observeCount()
    }

    suspend fun saveScan(
        content: String,
        contentType: String
    ) {
        dao.insert(
            ScanHistoryEntity(
                content = content,
                contentType = contentType,
                scannedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteScan(id: Int) {
        dao.deleteById(id)
    }

    suspend fun clearHistory() {
        dao.clearAll()
    }
}