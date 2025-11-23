package com.example.gymeaze.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.R
import com.example.gymeaze.data.models.FollowerUser
import com.example.gymeaze.data.models.StartedTrainingPlan
import com.example.gymeaze.data.models.UserProfileDetails
import com.example.gymeaze.data.repository.AuthRepository
import com.example.gymeaze.data.repository.StartedTrainingRepository
import com.example.gymeaze.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val startedPlans: List<StartedTrainingPlan> = emptyList(),
    val userProfile: UserProfileDetails? = null,
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
    val showFollowingDialog: Boolean = false,
    val isFollowLoading: Boolean = false,

)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = StartedTrainingRepository(application)

    private val authRepository = AuthRepository(application)

    private val userRepository = UserRepository(application)
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadStartedPlans()
    }

    fun loadStartedPlans() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val userId = authRepository.getCurrentUserId()

            if (userId.isNullOrEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = getApplication<Application>().getString(R.string.error_user_not_logged_in)
                )
                return@launch
            }

            val result = repository.getStartedTrainingPlans(
                userId = userId,
                page = 1,
                pageSize = 5
            )

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
                        startedPlans = it.startedPlans.filterNot { plan -> plan.id == userTrainingPlanId },
                        operationSuccessMessage = response.message
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        deletingPlanId = null,
                        operationError = exception.message ?: getApplication<Application>().getString(R.string.error_delete_started_plan)
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
                        operationError = exception.message ?: getApplication<Application>().getString(R.string.error_reset_started_plan)
                    )
                }
            }
        }
    }

    fun clearOperationMessages() {
        _uiState.update { it.copy(operationError = null, operationSuccessMessage = null) }
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

    fun loadUserProfile(username: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, operationError = null) }

            val result = userRepository.getUserProfile(username)

            if (result.isSuccess) {
                val profile = result.getOrNull()
                _uiState.update {
                    it.copy(
                        userProfile = profile,
                        isLoading = false,
                        operationError = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        operationError = result.exceptionOrNull()?.message
                            ?: getApplication<Application>().getString(R.string.error_load_profile)
                    )
                }
            }
        }
    }


    fun toggleFollow(userId: String) {
        viewModelScope.launch {
            val currentProfile = _uiState.value.userProfile

            if (currentProfile == null) {
                _uiState.update {
                    it.copy(operationError = getApplication<Application>().getString(R.string.error_profile_missing))
                }
                return@launch
            }

            _uiState.update { it.copy(isFollowLoading = true) }

            val result = userRepository.toggleUserFollow(userId)

            _uiState.update { currentState ->
                val response = result.getOrNull()
                if (result.isSuccess && response != null) {
                    loadUserProfile(currentProfile.username)
                    val safeProfile = currentProfile.copy(
                        profilePicture = currentProfile.profilePicture ?: "",
                        description = currentProfile.description ?: "",
                        isFollowing = response.isFollowing,
                        followersCount = response.followersCount,
                        followingCount = response.followingCount
                    )
                    currentState.copy(
                        userProfile = safeProfile,
                        isFollowLoading = false,
                        operationSuccessMessage = response.message
                    )

                } else {
                    currentState.copy(
                        isFollowLoading = false,
                        operationError = result.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.error_follow_failed)
                    )
                }
            }
        }
    }

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

    fun loadUserStartedPlans(username: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val plans = repository.getUserStartedPlans(username)
                _uiState.update {
                    it.copy(
                        startedPlans = plans,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        operationError = getApplication<Application>().getString(R.string.error_load_started_plans)
                    )
                }
            }
        }
    }
}