package com.example.paryavaran_kavalu.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.paryavaran_kavalu.data.local.entity.ReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Query("SELECT * FROM reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Update
    suspend fun updateReport(report: ReportEntity)

    @Query("SELECT * FROM reports WHERE id = :id")
    suspend fun getReportById(id: Int): ReportEntity?
}
