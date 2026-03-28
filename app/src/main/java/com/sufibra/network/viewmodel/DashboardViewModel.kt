package com.sufibra.network.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sufibra.network.data.repository.DashboardRepository
import com.sufibra.network.domain.model.AdminDashboardMetrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val repository = DashboardRepository()

    private val _metrics = MutableStateFlow<AdminDashboardMetrics?>(null)
    val metrics: StateFlow<AdminDashboardMetrics?> = _metrics

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadMetrics()
    }

    fun loadMetrics() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.getAdminMetrics()

            result.onSuccess { dashboardMetrics ->
                _metrics.value = dashboardMetrics
            }

            result.onFailure {
                _errorMessage.value = "No se pudieron cargar las métricas del panel."
            }

            _isLoading.value = false
        }
    }
}
