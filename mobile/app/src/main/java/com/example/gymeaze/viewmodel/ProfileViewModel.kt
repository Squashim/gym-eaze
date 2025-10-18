package com.example.gymeaze.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.StartedTrainingPlan
import com.example.gymeaze.data.repository.StartedTrainingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val startedPlans: List<StartedTrainingPlan> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = StartedTrainingRepository(application)
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadStartedPlans()
    }

    fun loadStartedPlans() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // 5 ostatnich planów
            val result = repository.getStartedTrainingPlans(page = 1, pageSize = 5)

            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    startedPlans = response.items
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message
                )
            }
        }
    }
}