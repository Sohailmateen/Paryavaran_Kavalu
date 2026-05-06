package com.example.paryavaran_kavalu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val wasteType: String,
    val description: String,
    val imageUri: String,
    val latitude: Double,
    val longitude: Double,
    val status: String, // "Pending" or "Cleaned"
    val timestamp: Long,
    val cleanedImageUri: String = ""
)
