package com.example.gymeaze.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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

            // Sprawdź czy userId nie jest null
            if (userId.isNullOrEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Użytkownik nie jest zalogowany"
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
                            ?: "Błąd ładowania profilu"
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
                    it.copy(operationError = "Brak danych profilu")
                }
                return@launch
            }

            _uiState.update { it.copy(isFollowLoading = true) }

            val result = userRepository.toggleUserFollow(userId)

            _uiState.update { currentState ->
                val response = result.getOrNull()
                if (result.isSuccess && response != null) {
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
                        operationError = result.exceptionOrNull()?.message ?: "Błąd obserwowania"
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



    // ==================== PROFIL UŻYTKOWNIKA (dla zakładek) ====================

    private val _viewedUserProfile = MutableStateFlow<UserProfileDetails?>(null)
    val viewedUserProfile: StateFlow<UserProfileDetails?> = _viewedUserProfile

    private val _isLoadingProfile = MutableStateFlow(false)
    val isLoadingProfile: StateFlow<Boolean> = _isLoadingProfile

    private val _profileError = MutableStateFlow<String?>(null)
    val profileErrorState: StateFlow<String?> = _profileError


    private val _startedPlans = MutableStateFlow<List<StartedTrainingPlan>>(emptyList())
    val startedPlans: StateFlow<List<StartedTrainingPlan>> = _startedPlans

    private val _deletingPlanId = MutableStateFlow<String?>(null)
    val deletingPlanId: StateFlow<String?> = _deletingPlanId

    private val _resettingPlanId = MutableStateFlow<String?>(null)
    val resettingPlanId: StateFlow<String?> = _resettingPlanId

    private val _isFollowLoading = MutableStateFlow(false)
    val isFollowLoading: StateFlow<Boolean> = _isFollowLoading

    private val _followers = MutableStateFlow<List<FollowerUser>>(emptyList())
    val followers: StateFlow<List<FollowerUser>> = _followers

    private val _following = MutableStateFlow<List<FollowerUser>>(emptyList())
    val following: StateFlow<List<FollowerUser>> = _following

    private val _isLoadingFollowers = MutableStateFlow(false)
    val isLoadingFollowers: StateFlow<Boolean> = _isLoadingFollowers

    private val _showFollowersDialog = MutableStateFlow(false)
    val showFollowersDialog: StateFlow<Boolean> = _showFollowersDialog

    private val _showFollowingDialog = MutableStateFlow(false)
    val showFollowingDialog: StateFlow<Boolean> = _showFollowingDialog

    private val _operationSuccessMessage = MutableStateFlow<String?>(null)
    val operationSuccessMessage: StateFlow<String?> = _operationSuccessMessage

    private val _operationErrorMessage = MutableStateFlow<String?>(null)
    val operationErrorMessage: StateFlow<String?> = _operationErrorMessage

    // ==================== FUNKCJE PROFILU ====================

    fun loadUserProfileByUsername(username: String) {
        viewModelScope.launch {
            _isLoadingProfile.value = true
            _profileError.value = null

            val result = userRepository.getUserProfile(username)

            result.onSuccess { profile ->
                _viewedUserProfile.value = profile
                _isLoadingProfile.value = false
            }.onFailure { error ->
                _profileError.value = error.message ?: "Błąd ładowania profilu"
                _isLoadingProfile.value = false
            }
        }
    }

    fun clearViewedProfile() {
        _viewedUserProfile.value = null
        _profileError.value = null
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
                        operationError = "Nie udało się pobrać rozpoczętych planów"
                    )
                }
            }
        }
    }
}