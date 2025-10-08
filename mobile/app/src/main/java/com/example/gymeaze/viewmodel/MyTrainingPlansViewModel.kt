package com.example.gymeaze.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.TrainingPlan
import com.example.gymeaze.data.models.TrainingPlansResponse
import com.example.gymeaze.data.repository.AuthRepository
import com.example.gymeaze.data.repository.TrainingPlanRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MyTrainingPlansUiState(
    val trainingPlans: List<TrainingPlan> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = false,
    val isLoadingMore: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val planToDelete: TrainingPlan? = null
)

class MyTrainingPlansViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TrainingPlanRepository(application)
    private val authRepository = AuthRepository(application)
    private val _uiState = MutableStateFlow(MyTrainingPlansUiState())
    val uiState: StateFlow<MyTrainingPlansUiState> = _uiState.asStateFlow()

    init {
        loadMyTrainingPlans()
    }

    fun refreshMyPlansFromOtherScreens() {
        loadMyTrainingPlans()
    }

    private fun loadMyTrainingPlans(append: Boolean = false) {
        viewModelScope.launch {
            if (append) {
                _uiState.value = _uiState.value.copy(isLoadingMore = true)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }

            try {
                val currentState = _uiState.value
                val page = if (append) currentState.currentPage + 1 else 1

                val result = repository.getMyTrainingPlans(page = page, pageSize = 9)

                result.onSuccess { response ->
                    val newPlans = if (append) {
                        currentState.trainingPlans + response.items
                    } else {
                        response.items
                    }

                    _uiState.value = _uiState.value.copy(
                        trainingPlans = newPlans,
                        currentPage = response.page,
                        hasMorePages = response.page * response.pageSize < response.total,
                        isLoading = false,
                        isLoadingMore = false,
                        error = null
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = exception.message ?: "Błąd podczas ładowania moich planów"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = e.message ?: "Nieoczekiwany błąd"
                )
            }
        }
    }

    fun loadMoreTrainingPlans() {
        if (_uiState.value.hasMorePages && !_uiState.value.isLoadingMore) {
            loadMyTrainingPlans(append = true)
        }
    }

    fun refreshTrainingPlans() {
        loadMyTrainingPlans()
    }

    fun showDeleteDialog(plan: TrainingPlan) {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = true,
            planToDelete = plan
        )
    }

    fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = false,
            planToDelete = null
        )
    }

    fun deletePlan() {
        val planToDelete = _uiState.value.planToDelete ?: return

        viewModelScope.launch {
            try {
                val result = repository.deleteTrainingPlan(planToDelete.id)
                result.onSuccess {
                    _uiState.value = _uiState.value.copy(
                        trainingPlans = _uiState.value.trainingPlans.filter { it.id != planToDelete.id },
                        showDeleteDialog = false,
                        planToDelete = null
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Błąd podczas usuwania planu",
                        showDeleteDialog = false,
                        planToDelete = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Nieoczekiwany błąd",
                    showDeleteDialog = false,
                    planToDelete = null
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}