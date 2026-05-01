package com.example.paryavaran_kavalu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paryavaran_kavalu.data.repository.ReportRepository
import com.example.paryavaran_kavalu.data.local.entity.ReportEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReportViewModel(private val repository: ReportRepository) : ViewModel() {

    val allReports: StateFlow<List<ReportEntity>> = repository.allReports
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insert(report: ReportEntity) = viewModelScope.launch {
        repository.insert(report)
    }
}
