package com.brijesh.smartscanner.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scan: ScanHistoryEntity): Long

    @Query("SELECT * FROM scan_history ORDER BY scannedAt DESC")
    fun observeAll(): Flow<List<ScanHistoryEntity>>

    @Query("DELETE FROM scan_history WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM scan_history")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM scan_history")
    fun observeCount(): Flow<Int>
}