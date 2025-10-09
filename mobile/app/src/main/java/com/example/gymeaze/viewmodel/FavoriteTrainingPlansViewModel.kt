package com.example.gymeaze.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.TrainingPlan
import com.example.gymeaze.data.models.TrainingPlanAuthor
import com.example.gymeaze.data.repository.TrainingPlanRepository
import com.example.gymeaze.util.GlobalFavoriteState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FavoriteTrainingPlansUiState(
    val trainingPlans: List<TrainingPlan> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = false,
    val isLoadingMore: Boolean = false,
    val snackbarMessage: String? = null,
    val snackbarType: SnackbarType = SnackbarType.SUCCESS
)

class FavoriteTrainingPlansViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TrainingPlanRepository(application)

    private val _uiState = MutableStateFlow(FavoriteTrainingPlansUiState())
    val uiState: StateFlow<FavoriteTrainingPlansUiState> = _uiState.asStateFlow()

    init {
        loadFavoriteTrainingPlans()
    }

    fun refreshFavoritePlansFromOtherScreens() {
        loadFavoriteTrainingPlans()
    }

    private fun loadFavoriteTrainingPlans(append: Boolean = false) {
        viewModelScope.launch {
            if (append) {
                _uiState.value = _uiState.value.copy(isLoadingMore = true)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }

            try {
                val currentState = _uiState.value
                val page = if (append) currentState.currentPage + 1 else 1

                val result = repository.getFavoriteTrainingPlans(page = page, pageSize = 9)

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
                        error = exception.message ?: "Błąd podczas ładowania ulubionych planów",
                        snackbarType = SnackbarType.ERROR
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = e.message ?: "Nieoczekiwany błąd",
                    snackbarType = SnackbarType.ERROR
                )
            }
        }
    }

    fun loadMoreTrainingPlans() {
        if (_uiState.value.hasMorePages && !_uiState.value.isLoadingMore) {
            loadFavoriteTrainingPlans(append = true)
        }
    }

    fun removeFavorite(planId: String) {
        val planName = _uiState.value.trainingPlans.find { it.id == planId }?.name ?: "Plan"

        viewModelScope.launch {
            try {
                val newState = GlobalFavoriteState.getInstance(getApplication()).toggleFavorite(planId)

                _uiState.value = _uiState.value.copy(
                    trainingPlans = _uiState.value.trainingPlans.filter { it.id != planId },
                    snackbarMessage = "Usunięto \"$planName\" z ulubionych",
                    snackbarType = SnackbarType.SUCCESS
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Błąd podczas usuwania z ulubionych",
                    snackbarMessage = "Błąd podczas usuwania z ulubionych",
                    snackbarType = SnackbarType.ERROR
                )
            }
        }
    }

    fun toggleLike(planId: String) {
        viewModelScope.launch {
            try {
                val result = repository.toggleTrainingPlanLike(planId)
                result.onSuccess { likeResponse ->

                    _uiState.value = _uiState.value.copy(
                        trainingPlans = _uiState.value.trainingPlans.map { plan ->
                            if (plan.id == planId) {
                                TrainingPlan(
                                    id = plan.id,
                                    name = plan.name ?: "",
                                    author = plan.author ?: TrainingPlanAuthor("", "", null),
                                    description = plan.description,
                                    image = plan.image ?: "",
                                    goal = plan.goal ?: "",
                                    visibility = plan.visibility ?: "",
                                    advancedLevel = plan.advancedLevel ?: "",
                                    likes = likeResponse.likes,
                                    days = plan.days ?: emptyList(),
                                    hashtags = plan.hashtags ?: emptyList(),
                                    createdAt = plan.createdAt ?: "",
                                    updatedAt = plan.updatedAt ?: "",
                                    totalExercises = plan.totalExercises,
                                    totalDuration = plan.totalDuration,
                                    daysCount = plan.daysCount
                                )
                            } else {
                                plan
                            }
                        }
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Błąd podczas polubienia planu"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Błąd podczas polubienia planu"
                )
            }
        }
    }

    fun clearSnackbarMessage() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }


    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}