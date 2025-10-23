package com.example.gymeaze.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.FollowerUser
import com.example.gymeaze.data.models.StartedTrainingPlan
import com.example.gymeaze.data.repository.StartedTrainingRepository
import com.example.gymeaze.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val startedPlans: List<StartedTrainingPlan> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val deletingPlanId: String? = null,
    val resettingPlanId: String? = null,
    val operationError: String? = null,
    val operationSuccessMessage: String? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val followers: List<FollowerUser> = emptyList(),
    val following: List<FollowerUser> = emptyList(),
    val isLoadingFollowers: Boolean = false,
    val showFollowersDialog: Boolean = false,
    val showFollowingDialog: Boolean = false

)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = StartedTrainingRepository(application)

    private val userRepository = UserRepository(application)
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

    fun deleteStartedPlan(userTrainingPlanId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(deletingPlanId = userTrainingPlanId, operationError = null, operationSuccessMessage = null) }
            val result = repository.deleteStartedTrainingPlan(userTrainingPlanId)
            result.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        deletingPlanId = null,
                        // Usuń plan z listy
                        startedPlans = it.startedPlans.filterNot { plan -> plan.id == userTrainingPlanId },
                        operationSuccessMessage = response.message
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        deletingPlanId = null,
                        operationError = exception.message ?: "Błąd podczas usuwania planu"
                    )
                }
            }
        }

    }

    fun resetStartedPlan(userTrainingPlanId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(resettingPlanId = userTrainingPlanId, operationError = null, operationSuccessMessage = null) }
            val result = repository.resetStartedTrainingPlan(userTrainingPlanId)
            result.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        resettingPlanId = null,
                        startedPlans = it.startedPlans.map { plan ->
                            if (plan.id == userTrainingPlanId) {
                                plan.copy(completedDays = 0, status = "active")
                            } else {
                                plan
                            }
                        },
                        operationSuccessMessage = response.message
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        resettingPlanId = null,
                        operationError = exception.message ?: "Błąd podczas resetowania planu"
                    )
                }
            }
        }
    }

    fun clearOperationMessages() {
        _uiState.update { it.copy(operationError = null, operationSuccessMessage = null) }
    }
    //FOLLOW
    fun loadFollowersAndFollowing() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingFollowers = true) }

            val followersResult = userRepository.getFollowers()
            val followingResult = userRepository.getFollowing()

            followersResult.onSuccess { followers ->
                _uiState.update {
                    it.copy(
                        followers = followers,
                        followersCount = followers.size
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(operationError = error.message) }
            }

            followingResult.onSuccess { following ->
                _uiState.update {
                    it.copy(
                        following = following,
                        followingCount = following.size
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(operationError = error.message) }
            }

            _uiState.update { it.copy(isLoadingFollowers = false) }
        }
    }

    fun showFollowersDialog() {
        _uiState.update { it.copy(showFollowersDialog = true) }
    }

    fun hideFollowersDialog() {
        _uiState.update { it.copy(showFollowersDialog = false) }
    }

    fun showFollowingDialog() {
        _uiState.update { it.copy(showFollowingDialog = true) }
    }

    fun hideFollowingDialog() {
        _uiState.update { it.copy(showFollowingDialog = false) }
    }
}