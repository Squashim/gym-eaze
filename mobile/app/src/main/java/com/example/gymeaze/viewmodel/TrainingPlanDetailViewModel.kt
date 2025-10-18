package com.example.gymeaze.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.data.models.MessageResponse
import com.example.gymeaze.data.models.TrainingPlan
import com.example.gymeaze.data.repository.AuthRepository
import com.example.gymeaze.data.repository.ExerciseRepository
import com.example.gymeaze.data.repository.StartedTrainingRepository
import com.example.gymeaze.data.repository.TrainingPlanRepository
import com.example.gymeaze.util.GlobalFavoriteState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TrainingPlanDetailUiState(
    val trainingPlan: TrainingPlan? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDownloadingPdf: Boolean = false,
    val snackbarMessage: String? = null,
    val snackbarType: SnackbarType = SnackbarType.ERROR,
    val isStartingPlan: Boolean = false
)

enum class SnackbarType {
    ERROR, SUCCESS
}

class TrainingPlanDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TrainingPlanRepository(application)
    private val authRepository = AuthRepository(application)
    private val exerciseRepository = ExerciseRepository(application)

    private val startedTrainingRepository = StartedTrainingRepository(application)
    private val _uiState = MutableStateFlow(TrainingPlanDetailUiState())
    val uiState: StateFlow<TrainingPlanDetailUiState> = _uiState.asStateFlow()

    private val _isDownloadingPdf = MutableStateFlow(false)
    val isDownloadingPdf: StateFlow<Boolean> = _isDownloadingPdf.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0f)
    val downloadProgress: StateFlow<Float> = _downloadProgress.asStateFlow()

    private suspend fun getCurrentUserLogin(): String? {
        return try {
            val userResult = authRepository.getCurrentUser()
            userResult.getOrNull()?.username
        } catch (e: Exception) {
            null
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

                    loadExerciseDetails(plan)

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

    private fun loadExerciseDetails(plan: TrainingPlan) {
        viewModelScope.launch {
            try {

                val exerciseIds = plan.days.flatMap { day ->
                    day.exercises.map { it.id }
                }.distinct()

                if (exerciseIds.isEmpty()) {
                    return@launch
                }

                loadExercisesIndividually(plan, exerciseIds)

            } catch (e: Exception) {
            }
        }
    }

    private fun loadExercisesIndividually(plan: TrainingPlan, exerciseIds: List<String>) {
        viewModelScope.launch {
            val exerciseMap = mutableMapOf<String, Exercise>()

            exerciseIds.forEach { exerciseId ->
                try {
                    val result = exerciseRepository.getExerciseById(exerciseId)
                    result.onSuccess { exercise ->
                        exerciseMap[exerciseId] = exercise
                    }
                } catch (e: Exception) {
                }
            }

            if (exerciseMap.isNotEmpty()) {
                val updatedPlan = plan.copy(
                    days = plan.days.map { day ->
                        day.copy(
                            exercises = day.exercises.map { dayExercise ->
                                dayExercise.copy(
                                    exercise = exerciseMap[dayExercise.id]
                                )
                            }
                        )
                    }
                )

                _uiState.value = _uiState.value.copy(trainingPlan = updatedPlan)
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
                        trainingPlan = currentPlan.copy(
                            likes = likeResponse.likes,
                            days = currentPlan.days ?: emptyList()
                        )
                    )
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

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
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

    @androidx.annotation.RequiresApi(android.os.Build.VERSION_CODES.Q)
    private fun savePDFWithMediaStore(context: Context, pdfBytes: ByteArray, fileName: String) {
        val resolver = context.contentResolver
        val contentValues = android.content.ContentValues().apply {
            put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(pdfBytes)
            }
        } ?: throw Exception("Nie można utworzyć pliku w folderze Pobrane")
    }

    private fun savePDFToExternalStorage(context: Context, pdfBytes: ByteArray, fileName: String) {
        val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }

        val file = java.io.File(downloadsDir, fileName)
        file.writeBytes(pdfBytes)
    }

    fun clearSnackbarMessage() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun startTrainingPlan() {
        val planId = _uiState.value.trainingPlan?.id ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isStartingPlan = true,
                snackbarMessage = null,
                error = null
            )

            val result = startedTrainingRepository.startTrainingPlan(planId)

            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    isStartingPlan = false,
                    snackbarMessage = response.message,
                    snackbarType = SnackbarType.SUCCESS
                )
                // TODO: Można dodać nawigację do listy rozpoczętych planów
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isStartingPlan = false,
                    snackbarMessage = exception.message ?: "Wystąpił błąd",
                    snackbarType = SnackbarType.ERROR
                )
            }
        }
    }
}