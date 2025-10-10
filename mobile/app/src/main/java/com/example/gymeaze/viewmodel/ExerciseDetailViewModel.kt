package com.example.gymeaze.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ExerciseDetailUiState(
    val exercise: Exercise? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ExerciseDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExerciseRepository(application)

    private val _uiState = MutableStateFlow(ExerciseDetailUiState())
    val uiState: StateFlow<ExerciseDetailUiState> = _uiState.asStateFlow()

    fun loadExercise(exerciseId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.getExerciseById(exerciseId)

            result.onSuccess { exercise ->
                _uiState.value = _uiState.value.copy(
                    exercise = exercise,
                    isLoading = false,
                    error = null
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    exercise = null,
                    isLoading = false,
                    error = exception.message ?: "Błąd podczas ładowania ćwiczenia"
                )
            }
        }
    }

    fun toggleLike() {
        val currentExercise = _uiState.value.exercise ?: return

        viewModelScope.launch {
            try {
                val result = repository.toggleExerciseLike(currentExercise.id)
                result.onSuccess { likeResponse ->
                    _uiState.value = _uiState.value.copy(
                        exercise = currentExercise.copy(likes = likeResponse.likes)
                    )
                }
            } catch (e: Exception) { }
        }
    }
}