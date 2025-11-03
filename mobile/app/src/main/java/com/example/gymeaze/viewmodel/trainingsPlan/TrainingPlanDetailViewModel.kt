package com.example.gymeaze.viewmodel.trainingsPlan

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.Comment
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.data.models.TrainingPlan
import com.example.gymeaze.data.models.User
import com.example.gymeaze.data.repository.AuthRepository
import com.example.gymeaze.data.repository.ExerciseRepository
import com.example.gymeaze.data.repository.PostsRepository
import com.example.gymeaze.data.repository.StartedTrainingRepository
import com.example.gymeaze.data.repository.TrainingPlanRepository
import com.example.gymeaze.util.GlobalFavoriteState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

data class TrainingPlanDetailUiState(
    val trainingPlan: TrainingPlan? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDownloadingPdf: Boolean = false,
    val snackbarMessage: String? = null,
    val snackbarType: SnackbarType = SnackbarType.ERROR,
    val isStartingPlan: Boolean = false,
    val isDeleted: Boolean = false
)

data class NavigationEvent(
    val userTrainingPlanId: String,
    val planName: String,
    val firstDayNumber: Int
)

enum class SnackbarType {
    ERROR, SUCCESS
}

class TrainingPlanDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TrainingPlanRepository(application)
    private val authRepository = AuthRepository(application)
    private val postRepository = PostsRepository(application)

    private val startedTrainingRepository = StartedTrainingRepository(application)
    private val _uiState = MutableStateFlow(TrainingPlanDetailUiState())
    val uiState: StateFlow<TrainingPlanDetailUiState> = _uiState.asStateFlow()

    private val _isDownloadingPdf = MutableStateFlow(false)
    val isDownloadingPdf: StateFlow<Boolean> = _isDownloadingPdf.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0f)

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()
    private suspend fun getCurrentUserLogin(): String? {
        return try {
            val userResult = authRepository.getCurrentUser()
            userResult.getOrNull()?.username
        } catch (e: Exception) {
            null
        }
    }

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _likedPlans = MutableStateFlow<Set<String>>(emptySet())
    val likedPlans: StateFlow<Set<String>> = _likedPlans

    init {
        loadCurrentUser()
    }

    fun isCurrentUserTrainingPlan(authorLogin: String): Boolean {
        return _currentUser.value?.username == authorLogin
    }

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

    fun loadTrainingPlan(planId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val result = repository.getTrainingPlanById(planId)

                result.onSuccess { plan ->
                    _uiState.value = _uiState.value.copy(
                        trainingPlan = plan,
                        isLoading = false,
                        error = null
                    )

                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        trainingPlan = null,
                        isLoading = false,
                        error = exception.message ?: "Błąd podczas ładowania planu treningowego"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    trainingPlan = null,
                    isLoading = false,
                    error = e.message ?: "Nieoczekiwany błąd"
                )
            }
        }
    }

    fun toggleLike() {
        val currentPlan = _uiState.value.trainingPlan ?: return

        viewModelScope.launch {
            try {
                val result = repository.toggleTrainingPlanLike(currentPlan.id)
                result.onSuccess { likeResponse ->
                    _uiState.value = _uiState.value.copy(
                        snackbarMessage = likeResponse.message,
                        snackbarType = SnackbarType.SUCCESS,
                        trainingPlan = currentPlan.copy(
                            likes = likeResponse.likes,
                            days = currentPlan.days
                        )
                    )
                    _likedPlans.update { current  ->
                        if(current.contains(currentPlan.id)) current - currentPlan.id
                        else current + currentPlan.id
                    }

                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Błąd podczas polubienia planu",
                        snackbarType = SnackbarType.ERROR
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Błąd podczas polubienia planu",
                    snackbarType = SnackbarType.ERROR
                )
            }
        }
    }



    fun toggleFavorite() {
        val plan = _uiState.value.trainingPlan
        val planId = plan?.id ?: return

        viewModelScope.launch {
            try {

                val currentUser = getCurrentUserLogin()

                if (plan.author?.username == currentUser && currentUser != null) {
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
                        delay(1000)
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


    fun downloadPdf() {
        val planId = _uiState.value.trainingPlan?.id ?: return

        viewModelScope.launch {
            try {
                _isDownloadingPdf.value = true
                _downloadProgress.value = 0f

                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Rozpoczynanie pobierania PDF...",
                    snackbarType = SnackbarType.SUCCESS
                )

                val result = repository.downloadTrainingPlanPDF(planId)

                result.onSuccess { pdfBytes ->
                    _downloadProgress.value = 1f
                    savePDFToDownloads(pdfBytes, planId)
                }.onFailure { exception ->
                    _downloadProgress.value = 0f

                    _uiState.value = _uiState.value.copy(
                        snackbarMessage = exception.message ?: "Błąd podczas pobierania PDF",
                        snackbarType = SnackbarType.ERROR
                    )
                }
            } catch (e: Exception) {
                _downloadProgress.value = 0f

                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "Nieoczekiwany błąd podczas pobierania PDF",
                    snackbarType = SnackbarType.ERROR
                )
            } finally {
                _isDownloadingPdf.value = false
                _downloadProgress.value = 0f
            }
        }
    }

    private fun savePDFToDownloads(pdfBytes: ByteArray, planId: String) {
        try {
            val context = getApplication<Application>().applicationContext
            val planName = _uiState.value.trainingPlan?.name?.replace("[^a-zA-Z0-9]".toRegex(), "-") ?: "plan-treningowy"
            val fileName = "plan-treningowy-${planName}-${planId}.pdf"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                savePDFWithMediaStore(context, pdfBytes, fileName)
            } else {
                savePDFToExternalStorage(context, pdfBytes, fileName)
            }

            _uiState.value = _uiState.value.copy(
                snackbarMessage = "PDF zapisany w folderze Pobrane jako: $fileName",
                snackbarType = SnackbarType.SUCCESS
            )

        } catch (e: Exception) {

            _uiState.value = _uiState.value.copy(
                snackbarMessage = "Błąd podczas zapisywania PDF: ${e.message}",
                snackbarType = SnackbarType.ERROR
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun savePDFWithMediaStore(context: Context, pdfBytes: ByteArray, fileName: String) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(pdfBytes)
            }
        } ?: throw Exception("Nie można utworzyć pliku w folderze Pobrane")
    }

    private fun savePDFToExternalStorage(context: Context, pdfBytes: ByteArray, fileName: String) {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }

        val file = File(downloadsDir, fileName)
        file.writeBytes(pdfBytes)
    }

    fun clearSnackbarMessage() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun startTrainingPlan() {
        val plan = _uiState.value.trainingPlan ?: return
        val planId = plan.id

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isStartingPlan = true,
                snackbarMessage = null,
                error = null
            )

            val result = startedTrainingRepository.startTrainingPlan(planId)

            result.onSuccess { response ->
                val firstTrainingDay = plan.days.firstOrNull { !it.isRestDay }
                val firstDayNumber = firstTrainingDay?.dayNumber ?: 1

                _uiState.value = _uiState.value.copy(
                    isStartingPlan = false
                )
                _navigationEvent.emit(
                    NavigationEvent(
                        userTrainingPlanId = response.userTrainingPlanId,
                        planName = plan.name,
                        firstDayNumber = firstDayNumber
                    )
                )

            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isStartingPlan = false,
                    snackbarMessage = exception.message ?: "Wystąpił błąd",
                    snackbarType = SnackbarType.ERROR
                )
            }
        }
    }
    fun resetDeletedFlag() {
        _uiState.update { it.copy(isDeleted = false) }
    }

    fun deleteTrainingPlan(planId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                repository.deleteTrainingPlan(planId)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        snackbarMessage = "Plan treningowy został usunięty",
                        snackbarType = SnackbarType.SUCCESS,
                        isDeleted = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        snackbarMessage = "Nie udało się usunąć planu: ${e.message}",
                        snackbarType = SnackbarType.ERROR
                    )
                }
            }
        }
    }

    fun isTrainingPlanLiked(planId: String): Flow<Boolean>  = _likedPlans.map { it.contains(planId) }.distinctUntilChanged()


    //=========================COMMENTS SECTION=============================

    private val _trainingPlanComments = MutableStateFlow<List<Comment>>(emptyList())
    val trainingPlanComments: StateFlow<List<Comment>> = _trainingPlanComments.asStateFlow()

    private val _isLoadingComments = MutableStateFlow(false)
    val isLoadingComments: StateFlow<Boolean> = _isLoadingComments.asStateFlow()

    private val _hasMoreComments = MutableStateFlow(false)
    val hasMoreComments: StateFlow<Boolean> = _hasMoreComments.asStateFlow()

    private val _nextCursorComment = MutableStateFlow<String?>(null)

    private val _repliesMap = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())

    private val _likedComments = MutableStateFlow<Set<String>>(emptySet())

    fun loadTrainingPlanComments(planId: String, cursor: String? = null, limit: Int = 10, refresh: Boolean = false) {
        viewModelScope.launch {
            try {
                val effectiveCursor = if (refresh) null else cursor
                val response = repository.getCommentsForTrainingPlan(planId, effectiveCursor, limit)
                _trainingPlanComments.value = response?.items ?: emptyList()
                _hasMoreComments.value = response?.hasMore ?: false
                _nextCursorComment.value = response?.nextCursor
            } catch (e: Exception) {
                println("Błąd podczas ładowania komentarzy: ${e.message}")
            }
        }
    }

    fun loadMoreTrainingPlanComments(planId: String, limit: Int = 10) {
        val cursor = _nextCursorComment.value
        if (_hasMoreComments.value && cursor != null && !_isLoadingComments.value) {
            viewModelScope.launch {
                _isLoadingComments.value = true
                try {
                    val response = repository.getCommentsForTrainingPlan(planId, cursor, limit)
                    val current = _trainingPlanComments.value
                    val newComments = response?.items ?: emptyList()
                    _trainingPlanComments.value = current + newComments
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

    fun loadRepliesForTrainingPlanComment(commentId: String, cursor: String? = null, limit: Int = 10) {
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

    fun getRepliesForTrainingPlanComment(commentId: String): StateFlow<List<Comment>> {
        return _repliesMap.map { it[commentId] ?: emptyList() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
    }

    fun addTrainingPlanComment(planId: String, content: String) {
        viewModelScope.launch {
            try {
                val response = postRepository.createComment(
                    content = content,
                    parentType = "Trening",
                    parentId = planId
                )
                if (response != null) {
                    loadTrainingPlanComments(planId, refresh = true)
                }
            } catch (e: Exception) {
                println("Błąd podczas dodawania komentarza: ${e.message}")
            }
        }
    }

    fun addTrainingPlanReply(commentId: String, content: String) {
        viewModelScope.launch {
            try {
                val response = postRepository.createComment(
                    content = content,
                    parentType = "Komentarz",
                    parentId = commentId
                )
                if (response != null) {
                    loadRepliesForTrainingPlanComment(commentId)

                    val updatedComments = _trainingPlanComments.value.map { comment ->
                        if (comment._id == commentId) {
                            comment.copy(repliesCount = (comment.repliesCount ?: 0) + 1)
                        } else {
                            comment
                        }
                    }
                    _trainingPlanComments.value = updatedComments
                }
            } catch (e: Exception) {
                println("Błąd podczas dodawania odpowiedzi: ${e.message}")
            }
        }
    }

    fun toggleTrainingPlanCommentLike(commentId: String, onResult: (Boolean, String) -> Unit) {
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

                    updateTrainingPlanCommentLikes(commentId, response.likes)

                    val message = if (isLiked) {
                        "Polubiono komentarz"
                    } else {
                        "Usunięto polubienie komentarza"
                    }
                    onResult(true, message)
                } else {
                    onResult(false, "Błąd podczas polubienia komentarza")
                }
            } catch (e: Exception) {
                println("Błąd podczas przełączania polubienia: ${e.message}")
                onResult(false, e.message ?: "Błąd podczas polubienia komentarza")
            }
        }
    }

    private fun updateTrainingPlanCommentLikes(commentId: String, likes: Int) {
        _trainingPlanComments.value = _trainingPlanComments.value.map { comment ->
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

    fun isTrainingPlanCommentLiked(commentId: String): StateFlow<Boolean> {
        return _likedComments.map { it.contains(commentId) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                false
            )
    }

    fun editTrainingPlanComment(comment: Comment, newContent: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = postRepository.editComment(comment._id, newContent)
                onResult(result.isSuccess, result.getOrElse { it.message ?: "Błąd edycji" })
                if (result.isSuccess) {
                    loadTrainingPlanComments(comment.parentId ?: "", refresh = true)

                    _repliesMap.value.keys.forEach { parentId ->
                        loadRepliesForTrainingPlanComment(parentId)
                    }
                }
            } catch (e: Exception) {
                onResult(false, e.message ?: "Błąd edycji")
            }
        }
    }

    fun deleteTrainingPlanComment(comment: Comment, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = postRepository.deleteComment(comment._id)
                onResult(result.isSuccess, result.getOrElse { it.message ?: "Błąd usuwania" })
                if (result.isSuccess) {
                    loadTrainingPlanComments(comment.parentId ?: "", refresh = true)

                    _repliesMap.value.keys.forEach { parentId ->
                        loadRepliesForTrainingPlanComment(parentId)
                    }
                }
            } catch (e: Exception) {
                onResult(false, e.message ?: "Błąd usuwania")
            }
        }
    }

    fun fetchUserLikes() {
        viewModelScope.launch {
            try {
                val response = repository.getUserLikes()
                response.onSuccess { likesResponse ->
                    _likedComments.value = likesResponse.comments.map { it.toString() }.toSet()
                    _likedPlans.value = likesResponse.trainingPlans.map{it.toString()}.toSet()
                }
            } catch (e: Exception) {
                println("Błąd podczas pobierania polubionych: ${e.message}")
            }
        }
    }
}