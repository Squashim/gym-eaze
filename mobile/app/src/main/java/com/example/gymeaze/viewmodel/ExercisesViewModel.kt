package com.example.gymeaze.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.Comment
import com.example.gymeaze.data.models.Equipment
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.data.models.ExerciseFilters
import com.example.gymeaze.data.models.MuscleGroup
import com.example.gymeaze.data.models.User
import com.example.gymeaze.data.repository.AuthRepository
import com.example.gymeaze.data.repository.ExerciseRepository
import com.example.gymeaze.data.repository.PostsRepository
import com.example.gymeaze.data.repository.UserRepository
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
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
)

class ExercisesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExerciseRepository(application)
    private val userRepository = UserRepository(application)

    private val authRepository = AuthRepository(application)

    private val postRepository = PostsRepository(application)


    private val _uiState = MutableStateFlow(ExercisesUiState())
    val uiState: StateFlow<ExercisesUiState> = _uiState.asStateFlow()

    private val _likedExercises = MutableStateFlow<Set<String>>(emptySet())

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
        loadUserLikes()
        loadUserFavorites()
        loadCurrentUser()
        setupAutoSearch()
    }

    fun isExerciseLiked(exerciseId: String): Boolean {
        return _likedExercises.value.contains(exerciseId)
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

    private fun loadUserLikes() {
        viewModelScope.launch {
            try {
                val result = repository.getUserLikes()
                result.onSuccess { likesResponse ->
                    _likedExercises.value = likesResponse.exercises.toSet()
                }.onFailure {
                }
            } catch (e: Exception) {
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

    private val _currentExercise = MutableStateFlow<Exercise?>(null)
    val currentExercise: StateFlow<Exercise?> = _currentExercise.asStateFlow()

    private val _isLoadingExerciseDetail = MutableStateFlow(false)
    val isLoadingExerciseDetail: StateFlow<Boolean> = _isLoadingExerciseDetail.asStateFlow()

    fun loadExerciseDetail(exerciseId: String) {
        viewModelScope.launch {
            _isLoadingExerciseDetail.value = true
            _uiState.value = _uiState.value.copy(error = null)

            val result = repository.getExerciseById(exerciseId)

            result.onSuccess { exercise ->
                _currentExercise.value = exercise
                _isLoadingExerciseDetail.value = false
            }.onFailure { exception ->
                _isLoadingExerciseDetail.value = false
                _uiState.value = _uiState.value.copy(
                    error = exception.message ?: "Błąd podczas ładowania ćwiczenia"
                )
            }
        }
    }

    fun toggleLike(exerciseId: String) {
        viewModelScope.launch {
            val currentExercise = _uiState.value.exercises.find { it.id == exerciseId }
                ?: _currentExercise.value?.takeIf { it.id == exerciseId }

            if (currentExercise == null) return@launch

            val isCurrentlyLiked = _likedExercises.value.contains(exerciseId)

            try {
                val result = repository.toggleExerciseLike(exerciseId)
                result.onSuccess { likeResponse ->
                    _likedExercises.value = if (isCurrentlyLiked) {
                        _likedExercises.value - exerciseId
                    } else {
                        _likedExercises.value + exerciseId
                    }

                    _uiState.value = _uiState.value.copy(
                        exercises = _uiState.value.exercises.map { exercise ->
                            if (exercise.id == exerciseId) {
                                exercise.copy(likes = likeResponse.likes)
                            } else {
                                exercise
                            }
                        }
                    )

                    if (_currentExercise.value?.id == exerciseId) {
                        _currentExercise.value = _currentExercise.value?.copy(likes = likeResponse.likes)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Nieoczekiwany błąd podczas polubienia ćwiczenia"
                )
            }
        }
    }

    fun clearCurrentExercise() {
        _currentExercise.value = null
    }

    private val _favoriteExercises = MutableStateFlow<Set<String>>(emptySet())

    val favoriteExercises: StateFlow<Set<String>> = _favoriteExercises.asStateFlow()

    fun isExerciseFavorite(exerciseId: String): Boolean {
        return _favoriteExercises.value.contains(exerciseId)
    }

    fun toggleFavorite(exerciseId: String) {
        viewModelScope.launch {
            val currentExercise = _uiState.value.exercises.find { it.id == exerciseId }
                ?: _currentExercise.value?.takeIf { it.id == exerciseId }

            if (currentExercise == null) return@launch

            try {
                val result = repository.toggleExerciseFavorite(exerciseId)
                result.onSuccess { favoriteResponse ->

                    loadUserFavorites()
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Błąd podczas przełączania ulubionego"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Nieoczekiwany błąd podczas dodawania do ulubionych"
                )
            }
        }
    }

    private fun loadUserFavorites() {
        viewModelScope.launch {
            try {
                val result = userRepository.getUserFavorites()
                result.onSuccess { favoritesResponse ->
                    _favoriteExercises.value = favoritesResponse.exercises.toSet()
                }.onFailure { exception ->
                    println("Błąd podczas ładowania ulubionych: ${exception.message}")
                }
            } catch (e: Exception) {
                println("Nieoczekiwany błąd podczas ładowania ulubionych: ${e.message}")
            }
        }
    }

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val result = authRepository.getCurrentUser()
                result.onSuccess { user ->
                    _currentUser.value = user
                }.onFailure { exception ->
                    println("Błąd podczas ładowania aktualnego użytkownika: ${exception.message}")
                }
            } catch (e: Exception) {
                println("Nieoczekiwany błąd podczas ładowania użytkownika: ${e.message}")
            }
        }
    }

    fun isCurrentUserExercise(authorLogin: String): Boolean {
        return _currentUser.value?.username == authorLogin
    }

    private val _scrollIndex: MutableState<Int> = mutableStateOf(0)
    val scrollIndex: MutableState<Int> = _scrollIndex

    private val _scrollOffset: MutableState<Int> = mutableStateOf(0)
    val scrollOffset: MutableState<Int> = _scrollOffset

    fun updateScrollPosition(index: Int, offset: Int) {
        _scrollIndex.value = index
        _scrollOffset.value = offset
    }

    fun resetScrollPosition() {
        _scrollIndex.value = 0
        _scrollOffset.value = 0
    }


    //ULUBIONE CWICZENIA UZYTKOWNIKA
    fun loadFavoriteExercises(append: Boolean = false) {
        viewModelScope.launch {
            if (append) {
                _uiState.value = _uiState.value.copy(isLoadingMore = true)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }

            try {
                val currentState = _uiState.value
                val page = if (append) currentState.currentPage + 1 else 1

                val result = repository.getFavoriteExercises(
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
                        error = exception.message ?: "Błąd podczas ładowania ulubionych ćwiczeń"
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

    fun loadUserExercises(userId: String, append: Boolean = false) {
        viewModelScope.launch {
            if (append) {
                _uiState.value = _uiState.value.copy(isLoadingMore = true)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }

            try {
                val currentState = _uiState.value
                val page = if (append) currentState.currentPage + 1 else 1

                val result = repository.getUserExercises(
                    userId = userId,
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
                        error = exception.message ?: "Błąd podczas ładowania ćwiczeń użytkownika"
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

    fun loadMoreUserExercises(userId: String) {
        if (_uiState.value.hasMorePages && !_uiState.value.isLoadingMore) {
            loadUserExercises(userId = userId, append = true)
        }
    }

    fun refreshUserExercises(userId: String) {
        loadUserExercises(userId = userId, append = false)
    }

    fun loadMoreFavoriteExercises() {
        if (_uiState.value.hasMorePages && !_uiState.value.isLoadingMore) {
            loadFavoriteExercises(append = true)
        }
    }

    fun refreshFavoriteExercises() {
        loadFavoriteExercises(append = false)
    }
    fun clearUserExercisesState() {
        _uiState.value = ExercisesUiState()
    }

    private val _currentMode = MutableStateFlow<ExerciseMode>(ExerciseMode.ALL)

    enum class ExerciseMode {
        ALL,
        FAVORITES,
        MY_EXERCISES
    }

    fun switchToMode(mode: ExerciseMode, userId: String? = null) {
        if (_currentMode.value == mode) return

        _currentMode.value = mode
        _uiState.value = ExercisesUiState()

        when (mode) {
            ExerciseMode.ALL -> loadExercises()
            ExerciseMode.FAVORITES -> loadFavoriteExercises()
            ExerciseMode.MY_EXERCISES -> userId?.let { loadUserExercises(it) }
        }
    }

    fun reloadCurrentUser() {
        loadCurrentUser()
    }

    fun clearAllData() {
        searchJob?.cancel()
        searchJob = null

        _uiState.value = ExercisesUiState()
        _likedExercises.value = emptySet()
        _favoriteExercises.value = emptySet()
        _searchQuery.value = ""
        _selectedMuscleGroups.value = emptyList()
        _selectedEquipment.value = emptyList()
        _selectedHashtags.value = emptyList()
        _selectedDifficulty.value = null
        _sortBy.value = "latest"
        _followersOnly.value = false
        _currentExercise.value = null
        _isLoadingExerciseDetail.value = false
        _currentUser.value = null
        _scrollIndex.value = 0
        _scrollOffset.value = 0
        _currentMode.value = ExerciseMode.ALL
    }

    fun reloadUserDataForExercises() {
        viewModelScope.launch {
            launch { loadUserLikes() }
            launch { loadUserFavorites() }
        }
    }

    fun deleteExercise(exerciseId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.deleteExercise(exerciseId)

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        exercises = _uiState.value.exercises.filter { it.id != exerciseId },
                        isLoading = false
                    )

                    onSuccess()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Błąd podczas usuwania ćwiczenia"
                    )
                }
            )
        }
    }

    // ==================== KOMENTARZE DO ĆWICZEŃ ====================

    private val _exerciseComments = MutableStateFlow<List<Comment>>(emptyList())
    val exerciseComments: StateFlow<List<Comment>> = _exerciseComments.asStateFlow()

    private val _isLoadingComments = MutableStateFlow(false)
    val isLoadingComments: StateFlow<Boolean> = _isLoadingComments.asStateFlow()

    private val _hasMoreComments = MutableStateFlow(false)
    val hasMoreComments: StateFlow<Boolean> = _hasMoreComments.asStateFlow()

    private val _nextCursorComment = MutableStateFlow<String?>(null)

    private val _repliesMap = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())

    private val _likedComments = MutableStateFlow<Set<String>>(emptySet())

    fun loadExerciseComments(exerciseId: String, cursor: String? = null, limit: Int = 10, refresh: Boolean = false) {
        viewModelScope.launch {
            try {
                val effectiveCursor = if (refresh) null else cursor
                val response = repository.getCommentsForExercise(exerciseId, effectiveCursor, limit)
                _exerciseComments.value = response?.items ?: emptyList()
                _hasMoreComments.value = response?.hasMore ?: false
                _nextCursorComment.value = response?.nextCursor
            } catch (e: Exception) {
                println("Błąd podczas ładowania komentarzy: ${e.message}")
            }
        }
    }

    fun loadMoreExerciseComments(exerciseId: String, limit: Int = 10) {
        val cursor = _nextCursorComment.value
        if (_hasMoreComments.value && cursor != null && !_isLoadingComments.value) {
            viewModelScope.launch {
                _isLoadingComments.value = true
                try {
                    val response = repository.getCommentsForExercise(exerciseId, cursor, limit)
                    val current = _exerciseComments.value
                    val newComments = response?.items ?: emptyList()
                    _exerciseComments.value = current + newComments
                    _hasMoreComments.value = response?.hasMore ?: false
                    _nextCursorComment.value = response?.nextCursor
                } catch (e: Exception) {
                    println("Błąd podczas ładowania kolejnych komentarzy: ${e.message}")
                } finally {
                    _isLoadingComments.value = false
                }
            }
        }
    }

    fun loadRepliesForExerciseComment(commentId: String, cursor: String? = null, limit: Int = 10) {
        viewModelScope.launch {
            try {
                val response = postRepository.getRepliesForComment(commentId, cursor, limit)
                if (response != null) {
                    val currentMap = _repliesMap.value.toMutableMap()
                    currentMap[commentId] = response.items
                    _repliesMap.value = currentMap
                }
            } catch (e: Exception) {
                println("Błąd podczas ładowania odpowiedzi: ${e.message}")
            }
        }
    }

    fun getRepliesForExerciseComment(commentId: String): StateFlow<List<Comment>> {
        return _repliesMap.map { it[commentId] ?: emptyList() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
    }

    fun addExerciseComment(exerciseId: String, content: String) {
        viewModelScope.launch {
            try {
                val response = postRepository.createComment(
                    content = content,
                    parentType = "Ćwiczenie",
                    parentId = exerciseId
                )
                if (response != null) {
                    loadExerciseComments(exerciseId, refresh = true)
                }
            } catch (e: Exception) {
                println("Błąd podczas dodawania komentarza: ${e.message}")
            }
        }
    }

    fun addExerciseReply(commentId: String, content: String) {
        viewModelScope.launch {
            try {
                val response = postRepository.createComment(
                    content = content,
                    parentType = "Komentarz",
                    parentId = commentId
                )
                if (response != null) {
                    loadRepliesForExerciseComment(commentId)

                    val updatedComments = _exerciseComments.value.map { comment ->
                        if (comment._id == commentId) {
                            comment.copy(repliesCount = (comment.repliesCount ?: 0) + 1)
                        } else {
                            comment
                        }
                    }
                    _exerciseComments.value = updatedComments
                }
            } catch (e: Exception) {
                println("Błąd podczas dodawania odpowiedzi: ${e.message}")
            }
        }
    }

    fun toggleExerciseCommentLike(commentId: String) {
        viewModelScope.launch {
            try {
                val response = postRepository.toggleCommentLike(commentId)
                if (response != null) {
                    val isLiked = response.message.contains("Dodano")

                    _likedComments.value = if (isLiked) {
                        _likedComments.value + commentId
                    } else {
                        _likedComments.value - commentId
                    }

                    updateExerciseCommentLikes(commentId, response.likes)
                }
            } catch (e: Exception) {
                println("Błąd podczas przełączania polubienia: ${e.message}")
            }
        }
    }

    private fun updateExerciseCommentLikes(commentId: String, likes: Int) {
        _exerciseComments.value = _exerciseComments.value.map { comment ->
            if (comment._id == commentId) {
                comment.copy(likes = likes)
            } else {
                comment
            }
        }

        _repliesMap.value = _repliesMap.value.mapValues { (_, replies) ->
            replies.map { reply ->
                if (reply._id == commentId) {
                    reply.copy(likes = likes)
                } else {
                    reply
                }
            }
        }
    }

    fun isExerciseCommentLiked(commentId: String): StateFlow<Boolean> {
        return _likedComments.map { it.contains(commentId) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                false
            )
    }

    fun editExerciseComment(comment: Comment, newContent: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = postRepository.editComment(comment._id, newContent)
                onResult(result.isSuccess, result.getOrElse { it.message ?: "Błąd edycji" })
                if (result.isSuccess) {
                    loadExerciseComments(comment.parentId ?: "", refresh = true)

                    _repliesMap.value.keys.forEach { parentId ->
                        loadRepliesForExerciseComment(parentId)
                    }
                }
            } catch (e: Exception) {
                onResult(false, e.message ?: "Błąd edycji")
            }
        }
    }


    fun deleteExerciseComment(comment: Comment, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = postRepository.deleteComment(comment._id)
                onResult(result.isSuccess, result.getOrElse { it.message ?: "Błąd usuwania" })
                if (result.isSuccess) {
                    loadExerciseComments(comment.parentId ?: "", refresh = true)

                    _repliesMap.value.keys.forEach { parentId ->
                        loadRepliesForExerciseComment(parentId)
                    }
                }
            } catch (e: Exception) {
                onResult(false, e.message ?: "Błąd usuwania")
            }
        }
    }

    fun fetchUserCommentLikes() {
        viewModelScope.launch {
            try {
                val response = repository.getUserLikes()
                response.onSuccess { likesResponse ->
                    _likedComments.value = likesResponse.comments.map { it.toString() }.toSet()
                }
            } catch (e: Exception) {
                println("Błąd podczas pobierania polubionych komentarzy: ${e.message}")
            }
        }
    }

    fun clearExerciseComments() {
        _exerciseComments.value = emptyList()
        _repliesMap.value = emptyMap()
        _likedComments.value = emptySet()
        _hasMoreComments.value = false
        _nextCursorComment.value = null
        _isLoadingComments.value = false
    }
}