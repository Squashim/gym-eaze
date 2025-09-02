package com.example.gymeaze.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.ExerciseRepository
import com.example.gymeaze.data.UserPreferences
import com.example.gymeaze.data.models.Equipment
import com.example.gymeaze.data.models.Hashtag
import com.example.gymeaze.data.models.MuscleGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ExerciseCreatorStep { BASIC, EXTRA, SUMMARY }
data class ExerciseCreatorState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val name: String = "",
    val imageUri: Uri? = null,
    val videoUri: Uri? = null,
    val difficulty: String = "easy",
    val isPublic: Boolean = true,
    val description: String = "",
    val selectedMuscleGroups: List<MuscleGroup> = emptyList(),
    val selectedEquipment: List<Equipment> = emptyList(),
    val selectedHashtags: List<Hashtag> = emptyList(),
    val availableHashtags: List<Hashtag> = emptyList(),
    val availableMuscleGroups: List<MuscleGroup> = emptyList(),
    val availableEquipment: List<Equipment> = emptyList(),
    val isLoadingData: Boolean = false
) {

    val basicValid get() = name.isNotBlank() && imageUri != null && videoUri != null
    val extraValid get() = description.isNotBlank() &&
            description.length <= 850 &&
            selectedMuscleGroups.isNotEmpty() &&
            selectedEquipment.isNotEmpty() &&
            selectedHashtags.size <= 50

}

class ExerciseCreatorViewModel(application: Application, private val userPreferences: UserPreferences) : AndroidViewModel(application) {
    private val repo = ExerciseRepository(application, userPreferences)

    private val _uiState = MutableStateFlow(ExerciseCreatorState())
    val uiState: StateFlow<ExerciseCreatorState> = _uiState.asStateFlow()

    var step: ExerciseCreatorStep = ExerciseCreatorStep.BASIC
        private set

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingData = true)

            val muscleGroupsResult = repo.getMuscleGroups()
            val equipmentResult = repo.getEquipment()
            val hashtagsResult = repo.getPopularHashtags(50)

            _uiState.value = _uiState.value.copy(
                isLoadingData = false,
                availableMuscleGroups = muscleGroupsResult.getOrNull() ?: emptyList(),
                availableEquipment = equipmentResult.getOrNull() ?: emptyList(),
                availableHashtags = hashtagsResult.getOrNull() ?: emptyList(),
                error = when {
                    muscleGroupsResult.isFailure -> "Błąd pobierania grup mięśniowych: ${muscleGroupsResult.exceptionOrNull()?.message}"
                    equipmentResult.isFailure -> "Błąd pobierania sprzętu: ${equipmentResult.exceptionOrNull()?.message}"
                    hashtagsResult.isFailure -> "Błąd pobierania hashtagów: ${hashtagsResult.exceptionOrNull()?.message}"
                    else -> null
                }
            )
        }
    }

    fun setName(v: String) { _uiState.value = _uiState.value.copy(name = v.take(150)) }
    fun setImage(u: Uri?) { _uiState.value = _uiState.value.copy(imageUri = u) }
    fun setVideo(u: Uri?) { _uiState.value = _uiState.value.copy(videoUri = u) }
    fun setDifficulty(v: String) { _uiState.value = _uiState.value.copy(difficulty = v) }
    fun setPublic(v: Boolean) { _uiState.value = _uiState.value.copy(isPublic = v) }
    fun setDescription(v: String) { _uiState.value = _uiState.value.copy(description = v.take(850)) }

    fun setHashtags(list: List<Hashtag>) {
        val validatedList = list.take(50).filter { hashtag ->
            hashtag.name.length >= 2 &&
                    hashtag.name.length <= 30 &&
                    hashtag.name.all { it.isLetterOrDigit() }
        }
        _uiState.value = _uiState.value.copy(selectedHashtags = validatedList)
    }

    fun setMuscleGroups(list: List<MuscleGroup>) {
        _uiState.value = _uiState.value.copy(selectedMuscleGroups = list)
    }

    fun setEquipment(list: List<Equipment>) {
        _uiState.value = _uiState.value.copy(selectedEquipment = list)
    }
    fun nextStep()
    { step = when(step) {
        ExerciseCreatorStep.BASIC -> ExerciseCreatorStep.EXTRA
        ExerciseCreatorStep.EXTRA -> ExerciseCreatorStep.SUMMARY
        ExerciseCreatorStep.SUMMARY -> ExerciseCreatorStep.SUMMARY
    }}

    fun prevStep() { step = when (step) {
        ExerciseCreatorStep.BASIC -> ExerciseCreatorStep.BASIC
        ExerciseCreatorStep.EXTRA -> ExerciseCreatorStep.BASIC
        ExerciseCreatorStep.SUMMARY -> ExerciseCreatorStep.EXTRA
    } }

    fun create() {
        val s = _uiState.value
        viewModelScope.launch {
            _uiState.value = s.copy(isLoading = true, error = null, success = false)
            val res = repo.createExercise(
                name = s.name,
                description = s.description,
                imageUri = s.imageUri!!,
                videoUri = s.videoUri!!,
                difficulty = s.difficulty,
                isPublic = s.isPublic,
                muscleGroups = s.selectedMuscleGroups.map { it._id },
                equipment = s.selectedEquipment.map { it._id },
                hashtags = s.selectedHashtags.map { it.name }
            )
            _uiState.value = if (res.isSuccess) {
                _uiState.value.copy(isLoading = false, success = true)
            } else {
                _uiState.value.copy(isLoading = false, error = res.exceptionOrNull()?.message ?: "Błąd")
            }
        }
    }

    fun clearData() {
        _uiState.value = ExerciseCreatorState(
            availableMuscleGroups = _uiState.value.availableMuscleGroups,
            availableEquipment = _uiState.value.availableEquipment,
            availableHashtags = _uiState.value.availableHashtags
        )
        step = ExerciseCreatorStep.BASIC
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
    fun clearSuccess() { _uiState.value = _uiState.value.copy(success = false) }

}