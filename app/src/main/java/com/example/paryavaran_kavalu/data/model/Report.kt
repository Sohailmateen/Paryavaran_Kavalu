package com.example.paryavaran_kavalu.data.model

import com.google.firebase.Timestamp

data class Report(
    val id: String = "",
    val wasteType: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val status: String = "Pending",
    val timestamp: Timestamp? = null,
    val userRole: String = "",
    val userId: String = "",
    val cleanedImageUrl: String = "",
    val cleanedBy: String = ""
)
