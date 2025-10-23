package com.example.gymeaze.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.StartedDay
import com.example.gymeaze.data.repository.StartedTrainingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StartedPlanDaysUiState(
    val days: List<StartedDay> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val planName: String = "",
    val completingDayNumber: Int? = null
)

class StartedPlanDaysViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StartedTrainingRepository(application)

    private val _uiState = MutableStateFlow(StartedPlanDaysUiState())
    val uiState: StateFlow<StartedPlanDaysUiState> = _uiState.asStateFlow()

    private var currentUserTrainingPlanId: String? = null

    fun loadDays(userTrainingPlanId: String, planName: String) {
        if (userTrainingPlanId == currentUserTrainingPlanId && _uiState.value.days.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(planName = planName)
            return
        }

        currentUserTrainingPlanId = userTrainingPlanId
        _uiState.value = StartedPlanDaysUiState(isLoading = true, planName = planName, error = null)

        viewModelScope.launch {
            val result = repository.getStartedTrainingPlanDays(userTrainingPlanId)

            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    days = response.days.sortedBy { it.dayNumber }
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Błąd ładowania dni treningowych"
                )
            }
        }
    }

    fun completeRestDay(dayNumber: Int) {
        val planId = currentUserTrainingPlanId ?: return

        if (_uiState.value.completingDayNumber != null || _uiState.value.days.find { it.dayNumber == dayNumber }?.isCompleted == true) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(completingDayNumber = dayNumber, error = null)
            val result = repository.completeDayTraining(planId, dayNumber)

            result.onSuccess {
                refreshDays()
                _uiState.value = _uiState.value.copy(completingDayNumber = null)
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    completingDayNumber = null,
                    error = exception.message ?: "Błąd podczas oznaczania dnia odpoczynku."
                )
            }
        }
    }

    fun refreshDays() {
        currentUserTrainingPlanId?.let { id ->
            val currentName = _uiState.value.planName
            _uiState.value = _uiState.value.copy(days = emptyList())
            loadDays(id, currentName)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}