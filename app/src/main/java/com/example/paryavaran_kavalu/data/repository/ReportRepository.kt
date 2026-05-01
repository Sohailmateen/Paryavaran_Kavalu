package com.example.paryavaran_kavalu.data.repository

import com.example.paryavaran_kavalu.data.local.db.ReportDao
import com.example.paryavaran_kavalu.data.local.entity.ReportEntity
import kotlinx.coroutines.flow.Flow

class ReportRepository(private val reportDao: ReportDao) {
    val allReports: Flow<List<ReportEntity>> = reportDao.getAllReports()

    suspend fun insert(report: ReportEntity) {
        reportDao.insertReport(report)
    }

    suspend fun getReportById(id: Long): ReportEntity? {
        return reportDao.getReportById(id)
    }
}
