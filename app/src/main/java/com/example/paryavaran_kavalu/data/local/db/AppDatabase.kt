package com.example.paryavaran_kavalu.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.paryavaran_kavalu.data.local.entity.ReportEntity

@Database(entities = [ReportEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reportDao(): ReportDao
}
