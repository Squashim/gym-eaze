package com.example.gymeaze.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.*
import com.example.gymeaze.data.repository.TrainingPlanRepository
import com.example.gymeaze.util.EnumMappers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CreateTrainingPlanUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class CreateTrainingPlanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TrainingPlanRepository(application)

    private val _uiState = MutableStateFlow(CreateTrainingPlanUiState())
    val uiState: StateFlow<CreateTrainingPlanUiState> = _uiState.asStateFlow()

    private val _formData = MutableStateFlow(TrainingPlanFormData())
    val formData: StateFlow<TrainingPlanFormData> = _formData.asStateFlow()

    fun updateTitle(title: String) {
        if (title.length <= 150) {
            _formData.value = _formData.value.copy(title = title)
        }
    }

    fun updateDescription(description: String) {
        if (description.length <= 850) {
            _formData.value = _formData.value.copy(description = description)
        }
    }

    fun updateGoal(goal: String) {
        _formData.value = _formData.value.copy(goal = goal)
    }

    fun updateDifficulty(difficulty: String) {
        _formData.value = _formData.value.copy(advancedLevel = difficulty)
    }

    fun updateSelectedImage(uri: Uri) {
        _formData.value = _formData.value.copy(selectedImageUri = uri.toString())
    }

    fun addHashtag(hashtag: String) {
        val currentHashtags = _formData.value.hashtags
        if (!currentHashtags.contains(hashtag) && currentHashtags.size < 50) {
            _formData.value = _formData.value.copy(
                hashtags = currentHashtags + hashtag
            )
        }
    }

    fun removeHashtag(index: Int) {
        val currentHashtags = _formData.value.hashtags
        if (index in currentHashtags.indices) {
            _formData.value = _formData.value.copy(
                hashtags = currentHashtags.filterIndexed { i, _ -> i != index }
            )
        }
    }

    fun addTrainingDay() {
        val currentDays = _formData.value.days
        if (currentDays.size < 30) {
            val newDay = TrainingDayFormData(
                name = "Dzień ${currentDays.size + 1}",
                isRestDay = false
            )
            _formData.value = _formData.value.copy(
                days = currentDays + newDay
            )
        }
    }

    fun removeTrainingDay(dayIndex: Int) {
        val currentDays = _formData.value.days
        if (dayIndex in currentDays.indices) {
            _formData.value = _formData.value.copy(
                days = currentDays.filterIndexed { i, _ -> i != dayIndex }
            )
        }
    }

    fun updateDayName(dayIndex: Int, name: String) {
        val currentDays = _formData.value.days.toMutableList()
        if (dayIndex in currentDays.indices && name.length <= 100) {
            currentDays[dayIndex] = currentDays[dayIndex].copy(name = name)
            _formData.value = _formData.value.copy(days = currentDays)
        }
    }

    fun toggleRestDay(dayIndex: Int) {
        val currentDays = _formData.value.days.toMutableList()
        if (dayIndex in currentDays.indices) {
            val day = currentDays[dayIndex]
            currentDays[dayIndex] = day.copy(
                isRestDay = !day.isRestDay,
                exercises = if (!day.isRestDay) emptyList() else day.exercises
            )
            _formData.value = _formData.value.copy(days = currentDays)
        }
    }

    fun addExerciseToDay(dayIndex: Int, exercise: Exercise) {
        val currentDays = _formData.value.days.toMutableList()
        if (dayIndex in currentDays.indices) {
            val day = currentDays[dayIndex]
            if (!day.isRestDay && day.exercises.size < 15) {
                val newExercise = DayExerciseFormData(
                    exerciseId = exercise.id,
                    exercise = exercise,
                    isRepsSet = true,
                    restAfterSet = 60,
                    sets = listOf(ExerciseSetFormData(repetitions = 12, weight = 0.0))
                )
                currentDays[dayIndex] = day.copy(
                    exercises = day.exercises + newExercise
                )
                _formData.value = _formData.value.copy(days = currentDays)
            }
        }
    }

    fun removeExercise(dayIndex: Int, exerciseIndex: Int) {
        val currentDays = _formData.value.days.toMutableList()
        if (dayIndex in currentDays.indices) {
            val day = currentDays[dayIndex]
            if (exerciseIndex in day.exercises.indices) {
                currentDays[dayIndex] = day.copy(
                    exercises = day.exercises.filterIndexed { i, _ -> i != exerciseIndex }
                )
                _formData.value = _formData.value.copy(days = currentDays)
            }
        }
    }

    fun updateExerciseInDay(dayIndex: Int, exerciseIndex: Int, updatedExercise: DayExerciseFormData) {
        val currentDays = _formData.value.days.toMutableList()
        if (dayIndex in currentDays.indices) {
            val day = currentDays[dayIndex]
            if (exerciseIndex in day.exercises.indices) {
                val exercises = day.exercises.toMutableList()
                exercises[exerciseIndex] = updatedExercise
                currentDays[dayIndex] = day.copy(exercises = exercises)
                _formData.value = _formData.value.copy(days = currentDays)
            }
        }
    }

    fun createTrainingPlan() {
        viewModelScope.launch {
            val currentData = _formData.value

            if (currentData.title.trim().isEmpty()) {
                _uiState.value = _uiState.value.copy(error = "Tytuł jest wymagany")
                return@launch
            }

            if (currentData.selectedImageUri == null) {
                _uiState.value = _uiState.value.copy(error = "Zdjęcie jest wymagane")
                return@launch
            }

            if (currentData.days.isEmpty()) {
                _uiState.value = _uiState.value.copy(error = "Plan musi zawierać co najmniej jeden dzień")
                return@launch
            }

            val invalidDays = currentData.days.filter { !it.isRestDay && it.exercises.isEmpty() }
            if (invalidDays.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(
                    error = "Dni treningowe muszą zawierać co najmniej jedno ćwiczenie"
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val imageUri = Uri.parse(currentData.selectedImageUri)

                val request = CreateTrainingPlanRequest(
                    title = currentData.title.trim(),
                    description = currentData.description.trim().takeIf { it.isNotEmpty() },
                    goal = EnumMappers.mapGoalToBackend(currentData.goal),
                    visibility = EnumMappers.mapPrivacyToBackend(currentData.visibility),
                    advancedLevel = EnumMappers.mapAdvancedLevelToBackend(currentData.advancedLevel),
                    hashtags = currentData.hashtags,
                    days = currentData.days.map { day ->
                        CreateTrainingDay(
                            name = day.name,
                            isRestDay = day.isRestDay,
                            exercises = day.exercises.map { exercise ->
                                CreateDayExercise(
                                    id = exercise.exerciseId,
                                    isRepsSet = exercise.isRepsSet,
                                    duration = exercise.duration,
                                    restAfterSet = exercise.restAfterSet,
                                    sets = exercise.sets.map { set ->
                                        CreateExerciseSet(
                                            repetitions = set.repetitions,
                                            weight = set.weight
                                        )
                                    }
                                )
                            }
                        )
                    }
                )

                val result = repository.createTrainingPlan(request, imageUri)

                result.onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Wystąpił błąd podczas tworzenia planu"
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Nieoczekiwany błąd"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetForm() {
        _formData.value = TrainingPlanFormData()
        _uiState.value = CreateTrainingPlanUiState()
    }

    // Validation helpers
    fun isFormValid(): Boolean {
        val data = _formData.value
        return data.title.trim().isNotEmpty() &&
                data.selectedImageUri != null &&
                data.days.isNotEmpty() &&
                data.days.all { day -> day.isRestDay || day.exercises.isNotEmpty() }
    }

    fun getFormErrors(): List<String> {
        val data = _formData.value
        val errors = mutableListOf<String>()

        if (data.title.trim().isEmpty()) {
            errors.add("Tytuł jest wymagany")
        }

        if (data.selectedImageUri == null) {
            errors.add("Zdjęcie jest wymagane")
        }

        if (data.days.isEmpty()) {
            errors.add("Plan musi zawierać co najmniej jeden dzień")
        }

        val invalidDays = data.days.withIndex().filter { (_, day) ->
            !day.isRestDay && day.exercises.isEmpty()
        }

        invalidDays.forEach { (index, _) ->
            errors.add("Dzień ${index + 1} musi zawierać co najmniej jedno ćwiczenie")
        }

        return errors
    }
}

