package com.example.gymeaze.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.TrainingPlan
import com.example.gymeaze.data.repository.TrainingPlanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TrainingPlanDetailUiState(
    val trainingPlan: TrainingPlan? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class TrainingPlanDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TrainingPlanRepository(application)

    private val _uiState = MutableStateFlow(TrainingPlanDetailUiState())
    val uiState: StateFlow<TrainingPlanDetailUiState> = _uiState.asStateFlow()

    fun loadTrainingPlan(planId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.getTrainingPlanById(planId)

            result.onSuccess { trainingPlan ->
                _uiState.value = _uiState.value.copy(
                    trainingPlan = trainingPlan,
                    isLoading = false,
                    error = null
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    trainingPlan = null,
                    isLoading = false,
                    error = exception.message ?: "Błąd podczas ładowania planu treningowego"
                )
            }
        }
    }

    fun toggleLike() {
        val currentPlan = _uiState.value.trainingPlan ?: return

        viewModelScope.launch {
            try {
                val result = repository.toggleTrainingPlanLike(currentPlan.id)
                result.onSuccess {
                    // TODO: Zaaktualizować liczbę lajków na podstawie odpowiedzi serwera na razie ponowne załadowanie planu
                    loadTrainingPlan(currentPlan.id)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Błąd podczas polubienia planu"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
