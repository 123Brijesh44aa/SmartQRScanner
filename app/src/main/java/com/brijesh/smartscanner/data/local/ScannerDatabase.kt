package com.brijesh.smartscanner.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ScanHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ScannerDatabase : RoomDatabase() {

    abstract fun scanHistoryDao(): ScanHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: ScannerDatabase? = null

        fun getInstance(context: Context): ScannerDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ScannerDatabase::class.java,
                    "smart_scanner_database"
                )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}