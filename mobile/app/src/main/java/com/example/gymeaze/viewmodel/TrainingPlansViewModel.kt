package com.example.gymeaze.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.*
import com.example.gymeaze.data.repository.AuthRepository
import com.example.gymeaze.data.repository.TrainingPlanRepository
import com.example.gymeaze.util.GlobalFavoriteState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TrainingPlansUiState(
    val trainingPlans: List<TrainingPlan> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val hasMorePages: Boolean = false,
    val isLoadingMore: Boolean = false,
    val snackbarMessage: String? = null,
    val snackbarType: SnackbarType = SnackbarType.ERROR,
    val isRefreshing: Boolean = false
)

class TrainingPlansViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TrainingPlanRepository(application)
    private val authRepository = AuthRepository(application)
    private val _uiState = MutableStateFlow(TrainingPlansUiState())
    val uiState: StateFlow<TrainingPlansUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGoal = MutableStateFlow<String?>(null)
    val selectedGoal: StateFlow<String?> = _selectedGoal.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow<String?>(null)
    val selectedDifficulty: StateFlow<String?> = _selectedDifficulty.asStateFlow()

    private val _minDuration = MutableStateFlow<Int?>(null)
    val minDuration: StateFlow<Int?> = _minDuration.asStateFlow()

    private val _maxDuration = MutableStateFlow<Int?>(null)
    val maxDuration: StateFlow<Int?> = _maxDuration.asStateFlow()

    private val _selectedHashtags = MutableStateFlow<List<String>>(emptyList())
    val selectedHashtags: StateFlow<List<String>> = _selectedHashtags.asStateFlow()

    private val _sortBy = MutableStateFlow("latest")
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    private val _followersOnly = MutableStateFlow(false)
    val followersOnly: StateFlow<Boolean> = _followersOnly.asStateFlow()

    private var searchJob: Job? = null

    init {
        setupAutoSearch()
        loadTrainingPlans()
    }

    private suspend fun getCurrentUserLogin(): String? {
        return try {
            val userResult = authRepository.getCurrentUser()
            userResult.getOrNull()?.username
        } catch (e: Exception) {
            null
        }
    }

    private fun setupAutoSearch() {
        viewModelScope.launch {
            combine(
                searchQuery.debounce(300L).distinctUntilChanged(),
                selectedGoal,
                selectedDifficulty,
                minDuration,
                maxDuration,
                selectedHashtags,
                sortBy,
                followersOnly
            ) { flows ->
                FilterParams(
                    query = flows[0] as String,
                    goal = flows[1] as String?,
                    difficulty = flows[2] as String?,
                    minDuration = flows[3] as Int?,
                    maxDuration = flows[4] as Int?,
                    hashtags = flows[5] as List<String>,
                    sortBy = flows[6] as String,
                    followersOnly = flows[7] as Boolean
                )
            }.distinctUntilChanged()
                .collect {
                    loadTrainingPlans()
                }
        }
    }

    private data class FilterParams(
        val query: String,
        val goal: String?,
        val difficulty: String?,
        val minDuration: Int?,
        val maxDuration: Int?,
        val hashtags: List<String>,
        val sortBy: String,
        val followersOnly: Boolean
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedGoal(goal: String?) {
        _selectedGoal.value = goal
    }

    fun updateSelectedDifficulty(difficulty: String?) {
        _selectedDifficulty.value = difficulty
    }

    fun updateMinDuration(duration: Int?) {
        _minDuration.value = duration
    }

    fun updateMaxDuration(duration: Int?) {
        _maxDuration.value = duration
    }

    fun updateSelectedHashtags(hashtags: List<String>) {
        _selectedHashtags.value = hashtags
    }

    fun updateSortBy(sort: String) {
        _sortBy.value = sort
    }

    fun updateFollowersOnly(followersOnly: Boolean) {
        _followersOnly.value = followersOnly
    }

    fun clearAllFilters() {
        _searchQuery.value = ""
        _selectedGoal.value = null
        _selectedDifficulty.value = null
        _minDuration.value = null
        _maxDuration.value = null
        _selectedHashtags.value = emptyList()
        _sortBy.value = "latest"
        _followersOnly.value = false
    }

    fun refreshPlansFromOtherScreens() {
        loadTrainingPlans()
    }

    /*
    private fun loadTrainingPlans(append: Boolean = false) {
        viewModelScope.launch {
            try {
                if (append) {
                    _uiState.value = _uiState.value.copy(isLoadingMore = true)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                }

                val currentState = _uiState.value
                val page = if (append) currentState.currentPage + 1 else 1

                val filters = TrainingPlanFilters(
                    search = _searchQuery.value.takeIf { it.isNotBlank() },
                    goal = _selectedGoal.value,
                    difficulty = _selectedDifficulty.value,
                    minDuration = _minDuration.value,
                    maxDuration = _maxDuration.value,
                    hashtags = _selectedHashtags.value.takeIf { it.isNotEmpty() },
                    followers = _followersOnly.value.takeIf { it }
                )

                val result = repository.getTrainingPlans(
                    filters = filters,
                    sortBy = _sortBy.value,
                    page = page,
                    pageSize = 9
                )

                result.onSuccess { response ->
                    val newPlans = if (append) {
                        currentState.trainingPlans + response.items
                    } else {
                        response.items
                    }

                    _uiState.value = _uiState.value.copy(
                        trainingPlans = newPlans,
                        currentPage = response.page,
                        totalPages = (response.total + response.pageSize - 1) / response.pageSize,
                        hasMorePages = response.page * response.pageSize < response.total,
                        isLoading = false,
                        isLoadingMore = false,
                        error = null
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = exception.message ?: "Błąd podczas ładowania planów treningowych"
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

     */

    fun loadMoreTrainingPlans() {
        if (_uiState.value.hasMorePages && !_uiState.value.isLoadingMore && !_uiState.value.isRefreshing) {
            viewModelScope.launch {
                try {
                    _uiState.value = _uiState.value.copy(isLoadingMore = true)

                    val currentState = _uiState.value
                    val nextPage = currentState.currentPage + 1

                    val filters = TrainingPlanFilters(
                        search = _searchQuery.value.takeIf { it.isNotBlank() },
                        goal = _selectedGoal.value,
                        difficulty = _selectedDifficulty.value,
                        minDuration = _minDuration.value,
                        maxDuration = _maxDuration.value,
                        hashtags = _selectedHashtags.value.takeIf { it.isNotEmpty() },
                        followers = _followersOnly.value.takeIf { it }
                    )

                    val result = repository.getTrainingPlans(
                        filters = filters,
                        sortBy = _sortBy.value,
                        page = nextPage,
                        pageSize = 9
                    )

                    result.onSuccess { response ->
                        val updatedPlans = currentState.trainingPlans + response.items

                        _uiState.value = _uiState.value.copy(
                            trainingPlans = updatedPlans,
                            currentPage = response.page,
                            totalPages = (response.total + response.pageSize - 1) / response.pageSize,
                            hasMorePages = response.page * response.pageSize < response.total,
                            isLoadingMore = false,
                            error = null
                        )
                    }.onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoadingMore = false,
                            error = exception.message ?: "Błąd podczas ładowania kolejnych planów"
                        )
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoadingMore = false,
                        error = e.message ?: "Nieoczekiwany błąd podczas ładowania kolejnych planów"
                    )
                }
            }
        }
    }


    /*
    fun refreshTrainingPlans() {
        loadTrainingPlans()
    }


     */

    private fun loadTrainingPlans(append: Boolean = false) {
        viewModelScope.launch {
            try {
                if (append) {
                    _uiState.value = _uiState.value.copy(isLoadingMore = true)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true,
                        error = null,
                        isRefreshing = false
                    )
                }

                val currentState = _uiState.value
                val page = if (append) currentState.currentPage + 1 else 1

                val filters = TrainingPlanFilters(
                    search = _searchQuery.value.takeIf { it.isNotBlank() },
                    goal = _selectedGoal.value,
                    difficulty = _selectedDifficulty.value,
                    minDuration = _minDuration.value,
                    maxDuration = _maxDuration.value,
                    hashtags = _selectedHashtags.value.takeIf { it.isNotEmpty() },
                    followers = _followersOnly.value.takeIf { it }
                )

                val result = repository.getTrainingPlans(
                    filters = filters,
                    sortBy = _sortBy.value,
                    page = page,
                    pageSize = 9
                )

                result.onSuccess { response ->
                    val newPlans = if (append) {
                        currentState.trainingPlans + response.items
                    } else {
                        response.items
                    }

                    _uiState.value = _uiState.value.copy(
                        trainingPlans = newPlans,
                        currentPage = response.page,
                        totalPages = (response.total + response.pageSize - 1) / response.pageSize,
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
                        error = exception.message ?: "Błąd podczas ładowania planów treningowych"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    isRefreshing = false,
                    error = e.message ?: "Nieoczekiwany błąd"
                )
            }
        }
    }

    fun refreshTrainingPlans() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)

                val filters = TrainingPlanFilters(
                    search = _searchQuery.value.takeIf { it.isNotBlank() },
                    goal = _selectedGoal.value,
                    difficulty = _selectedDifficulty.value,
                    minDuration = _minDuration.value,
                    maxDuration = _maxDuration.value,
                    hashtags = _selectedHashtags.value.takeIf { it.isNotEmpty() },
                    followers = _followersOnly.value.takeIf { it }
                )

                val result = repository.getTrainingPlans(
                    filters = filters,
                    sortBy = _sortBy.value,
                    page = 1, // Zawsze ładuj pierwszą stronę przy odświeżaniu
                    pageSize = 9
                )

                result.onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        trainingPlans = response.items,
                        currentPage = response.page,
                        totalPages = (response.total + response.pageSize - 1) / response.pageSize,
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
                        error = exception.message ?: "Błąd podczas odświeżania planów treningowych"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    isRefreshing = false,
                    error = e.message ?: "Nieoczekiwany błąd podczas odświeżania"
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

    fun toggleFavorite(planId: String) {
        viewModelScope.launch {
            try {

                val currentUser = getCurrentUserLogin()
                val plan = _uiState.value.trainingPlans.find { it.id == planId }

                if (plan?.author?.username == currentUser && currentUser != null) {
                    _uiState.value = _uiState.value.copy(
                        snackbarMessage = "Nie możesz dodać swojego planu do ulubionych",
                        snackbarType = SnackbarType.ERROR
                    )
                    return@launch
                }

                val newState = GlobalFavoriteState.getInstance(getApplication()).toggleFavorite(planId)

                _uiState.value = _uiState.value.copy(
                    snackbarMessage = if (newState) {
                        "Dodano do ulubionych"
                    } else {
                        "Usunięto z ulubionych"
                    },
                    snackbarType = SnackbarType.SUCCESS
                )

                val result = repository.toggleTrainingPlanFavorite(planId)
                result.onSuccess { response ->

                    launch {
                        kotlinx.coroutines.delay(1000)
                        GlobalFavoriteState.getInstance(getApplication()).forceRefreshFromBackend()
                    }

                }.onFailure { exception ->

                    GlobalFavoriteState.getInstance(getApplication()).toggleFavorite(planId)

                    _uiState.value = _uiState.value.copy(
                        snackbarMessage = "Błąd: ${exception.message}",
                        snackbarType = SnackbarType.ERROR
                    )
                }

            } catch (e: Exception) {
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