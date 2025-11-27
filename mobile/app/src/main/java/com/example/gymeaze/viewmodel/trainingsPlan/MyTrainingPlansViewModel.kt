package com.example.gymeaze.viewmodel.trainingsPlan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.R
import com.example.gymeaze.data.models.TrainingPlan
import com.example.gymeaze.data.models.TrainingPlanAuthor
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
    val planToDelete: TrainingPlan? = null,
    val snackbarMessage: String? = null,
    val snackbarType: SnackbarType = SnackbarType.SUCCESS,
    val isRefreshing: Boolean = false
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
                _uiState.value = _uiState.value.copy(isLoading = true, error = null,isRefreshing = false)
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
                        isRefreshing = false,
                        error = null
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        isRefreshing = false,
                        error = exception.message ?: getApplication<Application>().getString(R.string.error_load_my_plans)
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    isRefreshing = false,
                    error = e.message ?: getApplication<Application>().getString(R.string.error_unknown_occurred)
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
                        error = exception.message ?: getApplication<Application>().getString(R.string.error_like_plan)
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: getApplication<Application>().getString(R.string.error_like_plan)
                )
            }
        }
    }

    fun toggleFavorite(planId: String) {
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                snackbarMessage = getApplication<Application>().getString(R.string.error_favorite_own_plan),
                snackbarType = SnackbarType.ERROR
            )
        }
    }

    fun loadMoreTrainingPlans() {
        if (_uiState.value.hasMorePages && !_uiState.value.isLoadingMore) {
            loadMyTrainingPlans(append = true)
        }
    }

    fun refreshTrainingPlans() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)

                val result = repository.getMyTrainingPlans(page = 1, pageSize = 9)

                result.onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        trainingPlans = response.items,
                        currentPage = response.page,
                        hasMorePages = response.page * response.pageSize < response.total,
                        isLoading = false,
                        isLoadingMore = false,
                        isRefreshing = false,
                        error = null
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        isRefreshing = false,
                        error = exception.message ?: getApplication<Application>().getString(R.string.error_refresh_plans)
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    isRefreshing = false,
                    error = e.message ?: getApplication<Application>().getString(R.string.error_refresh_unknown)
                )
            }
        }
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
                        error = exception.message ?: getApplication<Application>().getString(R.string.error_delete_plan),
                        showDeleteDialog = false,
                        planToDelete = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: getApplication<Application>().getString(R.string.error_unknown_occurred),
                    showDeleteDialog = false,
                    planToDelete = null
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