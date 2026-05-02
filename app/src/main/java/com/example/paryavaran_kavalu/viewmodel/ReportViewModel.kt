package com.example.paryavaran_kavalu.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ── Data Model ──────────────────────────────────────────────────────────────

enum class ReportStatus { PENDING, CLEANED }

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

data class WasteReport(
    val id: String,
    val wasteType: WasteType,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val status: ReportStatus = ReportStatus.PENDING,
    val imageUrl: String = "" // placeholder for future real images
)

// ── ViewModel ────────────────────────────────────────────────────────────────

class ReportViewModel : ViewModel() {

    private val _reports = MutableStateFlow(dummyReports())
    val reports: StateFlow<List<WasteReport>> = _reports.asStateFlow()

    private val _userRole = MutableStateFlow(UserRole.CITIZEN)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    /** Add a new report to the list */
    fun addReport(report: WasteReport) {
        _reports.update { current -> current + report }
    }

    /** Set the user role */
    fun setUserRole(role: UserRole) {
        _userRole.value = role
    }

    /** Mark an existing report as cleaned */
    fun markAsCleaned(reportId: String) {
        _reports.update { current ->
            current.map { report ->
                if (report.id == reportId) report.copy(status = ReportStatus.CLEANED)
                else report
            }
        }
    }

    /** Find a single report by id */
    fun getReportById(reportId: String): WasteReport? =
        _reports.value.find { it.id == reportId }

    // ── Dummy seed data ──────────────────────────────────────────────────────

    private fun dummyReports(): List<WasteReport> = listOf(
        WasteReport(
            id = "1",
            wasteType = WasteType.PLASTIC,
            description = "Large pile of plastic bottles dumped near the park entrance.",
            latitude = 12.9716,
            longitude = 77.5946,
            status = ReportStatus.PENDING
        ),
        WasteReport(
            id = "2",
            wasteType = WasteType.ORGANIC,
            description = "Rotting organic waste left beside the community bin.",
            latitude = 12.9756,
            longitude = 77.5986,
            status = ReportStatus.CLEANED
        ),
        WasteReport(
            id = "3",
            wasteType = WasteType.EWASTE,
            description = "Old TV sets and broken appliances scattered on the roadside.",
            latitude = 12.9680,
            longitude = 77.5900,
            status = ReportStatus.PENDING
        ),
        WasteReport(
            id = "4",
            wasteType = WasteType.GENERAL,
            description = "Mixed household garbage overflowing from an open drain.",
            latitude = 12.9820,
            longitude = 77.6010,
            status = ReportStatus.PENDING
        ),
        WasteReport(
            id = "5",
            wasteType = WasteType.CONSTRUCTION,
            description = "Construction rubble blocking the footpath on MG Road.",
            latitude = 12.9740,
            longitude = 77.6070,
            status = ReportStatus.CLEANED
        )
    )
}
