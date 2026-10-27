package com.brijesh.smartscanner.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val content: String,

    val contentType: String,

    val scannedAt: Long = System.currentTimeMillis()
)