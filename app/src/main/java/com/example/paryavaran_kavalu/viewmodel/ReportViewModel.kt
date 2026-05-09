package com.example.paryavaran_kavalu.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import com.example.paryavaran_kavalu.data.model.Report
import com.example.paryavaran_kavalu.data.repository.ReportRepository
import com.example.paryavaran_kavalu.data.repository.StorageRepository
import com.example.paryavaran_kavalu.data.repository.AuthRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
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

sealed class ReportUiState {
    object Idle : ReportUiState()
    object Loading : ReportUiState()
    object Success : ReportUiState()
    data class Error(val message: String) : ReportUiState()
}

class ReportViewModel(
    private val repository: ReportRepository,
    private val storageRepository: StorageRepository = StorageRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: androidx.lifecycle.viewmodel.CreationExtras
            ): T {
                return ReportViewModel(ReportRepository()) as T
            }
        }
    }

    private val _uiState = MutableStateFlow<ReportUiState>(ReportUiState.Idle)
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    private val _userPoints = MutableStateFlow(0)
    val userPoints: StateFlow<Int> = _userPoints.asStateFlow()

    private val _userMedal = MutableStateFlow("Eco Beginner")
    val userMedal: StateFlow<String> = _userMedal.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userRole = MutableStateFlow(UserRole.CITIZEN)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    val allReports: StateFlow<List<Report>> = repository.getAllReports()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val myReports: StateFlow<List<Report>> = combine(repository.getAllReports(), _userRole) { reports, role ->
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (role == UserRole.VOLUNTEER) {
            reports.filter { it.cleanedBy == uid }
        } else {
            reports.filter { it.userId == uid }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val data = authRepository.getUserData(uid)
                if (data != null) {
                    _userPoints.value = (data["points"] as? Long)?.toInt() ?: 0
                    _userMedal.value = data["medal"] as? String ?: "Eco Beginner"
                    _userName.value = data["name"] as? String ?: ""
                    val roleStr = data["role"] as? String
                    if (roleStr == "Volunteer") {
                        _userRole.value = UserRole.VOLUNTEER
                    } else {
                        _userRole.value = UserRole.CITIZEN
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun refreshProfile() {
        loadUserProfile()
    }

    fun setUserRole(role: UserRole) {
        _userRole.value = role
    }

    fun addReport(
        wasteType: String,
        description: String,
        imageUri: Uri?,
        latitude: Double,
        longitude: Double,
        currentUserRole: String
    ) {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading
            try {
                var imageUrl = ""
                if (imageUri != null) {
                    imageUrl = storageRepository.uploadImage(imageUri, "reports")
                }

                val user = FirebaseAuth.getInstance().currentUser
                val userId = user?.uid ?: ""
                val newReport = Report(
                    wasteType = wasteType,
                    description = description,
                    imageUrl = imageUrl,
                    latitude = latitude,
                    longitude = longitude,
                    status = "Pending",
                    timestamp = Timestamp.now(),
                    userId = userId,
                    userRole = currentUserRole
                )
                repository.insertReport(newReport)
                
                // Update Karma
                val newPoints = _userPoints.value + 100
                val newMedal = calculateMedal(newPoints)
                _userPoints.value = newPoints
                _userMedal.value = newMedal
                authRepository.updateKarma(userId, newPoints, newMedal)

                _uiState.value = ReportUiState.Success
            } catch (e: Exception) {
                _uiState.value = ReportUiState.Error(e.message ?: "Failed to submit report")
            }
        }
    }

    fun markAsCleaned(reportId: String, cleanedImageUri: Uri?) {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading
            try {
                var cleanedImageUrl = ""
                if (cleanedImageUri != null) {
                    cleanedImageUrl = storageRepository.uploadImage(cleanedImageUri, "cleaned_reports")
                }
                
                val user = FirebaseAuth.getInstance().currentUser
                val userId = user?.uid ?: ""
                
                repository.markAsCleaned(reportId, cleanedImageUrl, userId)

                // Volunteers also get points for cleaning
                val newPoints = _userPoints.value + 100
                val newMedal = calculateMedal(newPoints)
                _userPoints.value = newPoints
                _userMedal.value = newMedal
                authRepository.updateKarma(userId, newPoints, newMedal)

                _uiState.value = ReportUiState.Success
            } catch (e: Exception) {
                _uiState.value = ReportUiState.Error(e.message ?: "Failed to mark as cleaned")
            }
        }
    }

    private fun calculateMedal(points: Int): String {
        return when {
            points >= 2000 -> "Green Guardian"
            points >= 1000 -> "Eco Warrior"
            else -> "Eco Beginner"
        }
    }

    fun resetUiState() {
        _uiState.value = ReportUiState.Idle
    }
}
