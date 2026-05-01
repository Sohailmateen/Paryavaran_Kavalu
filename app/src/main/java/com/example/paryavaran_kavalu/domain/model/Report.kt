package com.example.paryavaran_kavalu.domain.model

data class Report(
    val id: Long,
    val title: String,
    val description: String,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val imagePath: String?
)
