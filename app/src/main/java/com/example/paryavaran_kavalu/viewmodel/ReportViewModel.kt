package com.example.paryavaran_kavalu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.paryavaran_kavalu.ParyavaranApp
import com.example.paryavaran_kavalu.data.local.entity.ReportEntity
import com.example.paryavaran_kavalu.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class UserRole { CITIZEN, VOLUNTEER }

enum class WasteType(val label: String) {
    GENERAL("General Waste"),
    PLASTIC("Plastic / Recyclable"),
    ORGANIC("Bio-degradable"),
    EWASTE("E-Waste"),
    HAZARDOUS("Hazardous / Chemical"),
    CONSTRUCTION("Construction Debris"),
    SEWAGE("Sewage / Drain Blockage")
}

class ReportViewModel(private val repository: ReportRepository) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as ParyavaranApp
                return ReportViewModel(application.repository) as T
            }
        }
    }

    val allReports: StateFlow<List<ReportEntity>> = repository.allReports
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _userRole = MutableStateFlow(UserRole.CITIZEN)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    fun setUserRole(role: UserRole) {
        _userRole.value = role
    }

    fun addReport(
        wasteType: String,
        description: String,
        imageUri: String,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            val newReport = ReportEntity(
                wasteType = wasteType,
                description = description,
                imageUri = imageUri,
                latitude = latitude,
                longitude = longitude,
                status = "Pending",
                timestamp = System.currentTimeMillis()
            )
            repository.insertReport(newReport)
        }
    }

    fun markAsCleaned(report: ReportEntity) {
        viewModelScope.launch {
            val updatedReport = report.copy(status = "Cleaned")
            repository.updateReport(updatedReport)
        }
    }
}
