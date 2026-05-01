package com.example.paryavaran_kavalu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val imagePath: String? = null
)
