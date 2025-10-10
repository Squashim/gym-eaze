package com.example.gymeaze.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.Equipment
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.data.models.ExerciseFilters
import com.example.gymeaze.data.models.MuscleGroup
import com.example.gymeaze.data.repository.ExerciseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ExercisesUiState(
    val exercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val hasMorePages: Boolean = false,
    val isLoadingMore: Boolean = false
)

class ExercisesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExerciseRepository(application)

    private val _uiState = MutableStateFlow(ExercisesUiState())
    val uiState: StateFlow<ExercisesUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMuscleGroups = MutableStateFlow<List<MuscleGroup>>(emptyList())
    val selectedMuscleGroups: StateFlow<List<MuscleGroup>> = _selectedMuscleGroups.asStateFlow()

    private val _selectedEquipment = MutableStateFlow<List<Equipment>>(emptyList())
    val selectedEquipment: StateFlow<List<Equipment>> = _selectedEquipment.asStateFlow()

    private val _selectedHashtags = MutableStateFlow<List<String>>(emptyList())
    val selectedHashtags: StateFlow<List<String>> = _selectedHashtags.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow<String?>(null)
    val selectedDifficulty: StateFlow<String?> = _selectedDifficulty.asStateFlow()

    private val _sortBy = MutableStateFlow("latest")
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    private val _followersOnly = MutableStateFlow(false)
    val followersOnly: StateFlow<Boolean> = _followersOnly.asStateFlow()

    private val _availableMuscleGroups = MutableStateFlow<List<MuscleGroup>>(emptyList())
    val availableMuscleGroups: StateFlow<List<MuscleGroup>> = _availableMuscleGroups.asStateFlow()

    private val _availableEquipment = MutableStateFlow<List<Equipment>>(emptyList())
    val availableEquipment: StateFlow<List<Equipment>> = _availableEquipment.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadFilterOptions()
        setupAutoSearch()
    }

    private fun setupAutoSearch() {
        viewModelScope.launch {
            combine(
                searchQuery.debounce(300L).distinctUntilChanged(),
                selectedMuscleGroups,
                selectedEquipment,
                selectedHashtags,
                selectedDifficulty,
                sortBy,
                followersOnly
            ) { flows ->
                val query = flows[0] as String
                val muscleGroups = flows[1] as List<MuscleGroup>
                val equipment = flows[2] as List<Equipment>
                val hashtags = flows[3] as List<String>
                val difficulty = flows[4] as String?
                val sort = flows[5] as String
                val followers = flows[6] as Boolean

                FilterParams(query, muscleGroups, equipment, hashtags, difficulty, sort, followers)
            }.distinctUntilChanged()
                .collect {
                    loadExercises()
                }
        }
    }

    private data class FilterParams(
        val query: String,
        val muscleGroups: List<MuscleGroup>,
        val equipment: List<Equipment>,
        val hashtags: List<String>,
        val difficulty: String?,
        val sortBy: String,
        val followersOnly: Boolean
    )

    private fun loadFilterOptions() {
        viewModelScope.launch {
            try {
                val muscleGroupsResult = repository.getMuscleGroups()
                val equipmentResult = repository.getEquipment()

                muscleGroupsResult.onSuccess { groups ->
                    _availableMuscleGroups.value = groups
                }

                equipmentResult.onSuccess { equipment ->
                    _availableEquipment.value = equipment
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Błąd ładowania opcji filtrowania: ${e.message}"
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedMuscleGroups(muscleGroups: List<MuscleGroup>) {
        _selectedMuscleGroups.value = muscleGroups
    }

    fun updateSelectedEquipment(equipment: List<Equipment>) {
        _selectedEquipment.value = equipment
    }

    fun updateSelectedHashtags(hashtags: List<String>) {
        _selectedHashtags.value = hashtags
    }

    fun updateSelectedDifficulty(difficulty: String?) {
        _selectedDifficulty.value = difficulty
    }

    fun updateSortBy(sort: String) {
        _sortBy.value = sort
    }

    fun updateFollowersOnly(followersOnly: Boolean) {
        _followersOnly.value = followersOnly
    }

    fun clearAllFilters() {
        _searchQuery.value = ""
        _selectedMuscleGroups.value = emptyList()
        _selectedEquipment.value = emptyList()
        _selectedHashtags.value = emptyList()
        _selectedDifficulty.value = null
        _sortBy.value = "latest"
        _followersOnly.value = false
    }

    private fun loadExercises(append: Boolean = false) {
        viewModelScope.launch {
            if (append) {
                _uiState.value = _uiState.value.copy(isLoadingMore = true)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }

            try {
                val currentState = _uiState.value
                val page = if (append) currentState.currentPage + 1 else 1

                val filters = ExerciseFilters(
                    search = _searchQuery.value.takeIf { it.isNotBlank() },
                    muscleGroups = _selectedMuscleGroups.value.map { it._id }.takeIf { it.isNotEmpty() },
                    equipment = _selectedEquipment.value.map { it._id }.takeIf { it.isNotEmpty() },
                    difficulty = _selectedDifficulty.value,
                    hashtags = _selectedHashtags.value.takeIf { it.isNotEmpty() },
                    followers = _followersOnly.value.takeIf { it }
                )

                val result = repository.getExercises(
                    filters = filters,
                    sortBy = _sortBy.value,
                    page = page,
                    pageSize = 20
                )

                result.onSuccess { response ->
                    val newExercises = if (append) {
                        currentState.exercises + response.items
                    } else {
                        response.items
                    }

                    _uiState.value = _uiState.value.copy(
                        exercises = newExercises,
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
                        error = exception.message ?: "Błąd podczas ładowania ćwiczeń"
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

    fun loadMoreExercises() {
        if (_uiState.value.hasMorePages && !_uiState.value.isLoadingMore) {
            loadExercises(append = true)
        }
    }

    fun refreshExercises() {
        loadExercises()
    }

    fun toggleLike(exerciseId: String) {
        viewModelScope.launch {
            try {
                val result = repository.toggleExerciseLike(exerciseId)
                result.onSuccess { likeResponse ->
                    _uiState.value = _uiState.value.copy(
                        exercises = _uiState.value.exercises.map { exercise ->
                            if (exercise.id == exerciseId) {
                                exercise.copy(likes = likeResponse.likes)
                            } else {
                                exercise
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Nieoczekiwany błąd podczas polubienia ćwiczenia"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    @Deprecated("Use updateSearchQuery instead")
    fun searchExercises(query: String) {
        updateSearchQuery(query)
    }

    @Deprecated("Use updateSortBy instead")
    fun sortExercises(sortBy: String) {
        updateSortBy(sortBy)
    }

    @Deprecated("Use clearAllFilters instead")
    fun clearFilters() {
        clearAllFilters()
    }
}