package com.example.gymeaze.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.R
import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.constants.PrivacySettings
import com.example.gymeaze.data.repository.ExerciseRepository
import com.example.gymeaze.data.models.Equipment
import com.example.gymeaze.data.models.Exercise
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
    val difficulty: String = AdvancedLevel.NONE,
    val isPublic: String = PrivacySettings.PUBLIC,
    val description: String = "",
    val selectedMuscleGroups: List<MuscleGroup> = emptyList(),
    val selectedEquipment: List<Equipment> = emptyList(),
    val hashtags: List<String> = emptyList(),
    val availableMuscleGroups: List<MuscleGroup> = emptyList(),
    val availableEquipment: List<Equipment> = emptyList(),
    val isLoadingData: Boolean = false,
    val currentStep: ExerciseCreatorStep = ExerciseCreatorStep.BASIC,
    val isEditMode: Boolean = false,
    val editingExerciseId: String? = null,
    val existingImageUrl: String? = null,
    val existingVideoUrl: String? = null
) {
    val basicValid get() = name.isNotBlank() &&
            (imageUri != null || existingImageUrl != null) &&
            (videoUri != null || existingVideoUrl != null)

    val extraValid get() = description.isNotBlank() &&
            description.length <= 850 &&
            selectedMuscleGroups.isNotEmpty() &&
            selectedEquipment.isNotEmpty() &&
            hashtags.size <= 50
}

class ExerciseCreatorViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ExerciseRepository(application)

    private val _uiState = MutableStateFlow(ExerciseCreatorState())
    val uiState: StateFlow<ExerciseCreatorState> = _uiState.asStateFlow()

    val step: ExerciseCreatorStep get() = _uiState.value.currentStep

    init {
        loadFilterOptions()
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingData = true)

            val muscleGroupsResult = repo.getMuscleGroups()
            val equipmentResult = repo.getEquipment()

            _uiState.value = _uiState.value.copy(
                isLoadingData = false,
                availableMuscleGroups = muscleGroupsResult.getOrNull() ?: emptyList(),
                availableEquipment = equipmentResult.getOrNull() ?: emptyList(),
                error = when {
                    muscleGroupsResult.isFailure -> getApplication<Application>().getString(
                        R.string.error_loading_muscle_groups,
                        muscleGroupsResult.exceptionOrNull()?.message ?: ""
                    )
                    equipmentResult.isFailure -> getApplication<Application>().getString(
                        R.string.error_loading_equipment,
                        equipmentResult.exceptionOrNull()?.message ?: ""
                    )
                    else -> null
                }
            )
        }
    }

    fun loadExerciseForEdit(exercise: Exercise) {
        viewModelScope.launch {
            println("=== Loading exercise for edit ===")

            // Poczekaj, aż dostępne opcje zostaną załadowane (jeśli jeszcze nie są)
            if (_uiState.value.availableMuscleGroups.isEmpty() ||
                _uiState.value.availableEquipment.isEmpty()) {
                println("Loading available options first...")
                loadFilterOptions() // Upewnij się, że ta metoda istnieje
            }

            // Połącz wybrane elementy z pełnymi danymi (z obrazami)
            val muscleGroupsWithImages = exercise.muscleGroup.mapNotNull { selectedGroup ->
                val fullData = _uiState.value.availableMuscleGroups.find { it._id == selectedGroup._id }
                if (fullData != null) {
                    println("  ✓ Found image for ${selectedGroup.name}: ${fullData.image}")
                    fullData
                } else {
                    println("  ✗ No image found for ${selectedGroup.name}")
                    selectedGroup // Użyj bez obrazu jako fallback
                }
            }

            val equipmentWithImages = exercise.equipment.mapNotNull { selectedEquip ->
                val fullData = _uiState.value.availableEquipment.find { it._id == selectedEquip._id }
                if (fullData != null) {
                    println("  ✓ Found image for ${selectedEquip.name}: ${fullData.image}")
                    fullData
                } else {
                    println("  ✗ No image found for ${selectedEquip.name}")
                    selectedEquip // Użyj bez obrazu jako fallback
                }
            }

            _uiState.value = _uiState.value.copy(
                isEditMode = true,
                editingExerciseId = exercise.id,
                name = exercise.name,
                description = exercise.description,
                difficulty = exercise.advancedLevel,
                isPublic = if (exercise.visibility == PrivacySettings.PUBLIC) {
                    PrivacySettings.PUBLIC
                } else {
                    PrivacySettings.PRIVATE
                },
                selectedMuscleGroups = muscleGroupsWithImages,
                selectedEquipment = equipmentWithImages,
                hashtags = exercise.hashtags,
                existingImageUrl = exercise.image,
                existingVideoUrl = exercise.video,
                imageUri = null,
                videoUri = null
            )

            println("=== Final state ===")
            println("Muscle groups: ${_uiState.value.selectedMuscleGroups.size}")
            _uiState.value.selectedMuscleGroups.forEach {
                println("  - ${it.name}: ${it.image}")
            }
            println("Equipment: ${_uiState.value.selectedEquipment.size}")
            _uiState.value.selectedEquipment.forEach {
                println("  - ${it.name}: ${it.image}")
            }
        }
    }

    private fun loadFilterOptions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingData = true)

            try {
                val muscleGroupsResult = repo.getMuscleGroups()
                val equipmentResult = repo.getEquipment()

                muscleGroupsResult.onSuccess { groups ->
                    _uiState.value = _uiState.value.copy(availableMuscleGroups = groups)
                    println("Loaded ${groups.size} muscle groups")
                }

                equipmentResult.onSuccess { equipment ->
                    _uiState.value = _uiState.value.copy(availableEquipment = equipment)
                    println("Loaded ${equipment.size} equipment items")
                }
            } catch (e: Exception) {
                println("Error loading filter options: ${e.message}")
            } finally {
                _uiState.value = _uiState.value.copy(isLoadingData = false)
            }
        }
    }

    fun setName(v: String) { _uiState.value = _uiState.value.copy(name = v.take(150)) }

    fun setImage(u: Uri?) {
        _uiState.value = _uiState.value.copy(imageUri = u)
    }

    fun setVideo(u: Uri?) {
        _uiState.value = _uiState.value.copy(videoUri = u)
    }

    fun setDifficulty(v: String) { _uiState.value = _uiState.value.copy(difficulty = v) }

    fun setPublic(isChecked: Boolean) {
        _uiState.value = _uiState.value.copy(
            isPublic = if (isChecked) PrivacySettings.PUBLIC else PrivacySettings.PRIVATE
        )
    }

    fun setDescription(v: String) { _uiState.value = _uiState.value.copy(description = v.take(850)) }

    fun setHashtags(list: List<String>) {
        val validatedList = list.take(50).filter { hashtag ->
            hashtag.length >= 2 &&
                    hashtag.length <= 30 &&
                    hashtag.all { it.isLetterOrDigit() }
        }
        _uiState.value = _uiState.value.copy(hashtags = validatedList)
    }

    fun setMuscleGroups(list: List<MuscleGroup>) {
        _uiState.value = _uiState.value.copy(selectedMuscleGroups = list)
    }

    fun setEquipment(list: List<Equipment>) {
        _uiState.value = _uiState.value.copy(selectedEquipment = list)
    }

    fun nextStep() {
        val newStep = when (_uiState.value.currentStep) {
            ExerciseCreatorStep.BASIC -> ExerciseCreatorStep.EXTRA
            ExerciseCreatorStep.EXTRA -> ExerciseCreatorStep.SUMMARY
            ExerciseCreatorStep.SUMMARY -> ExerciseCreatorStep.SUMMARY
        }
        _uiState.value = _uiState.value.copy(currentStep = newStep)
    }

    fun prevStep() {
        val newStep = when (_uiState.value.currentStep) {
            ExerciseCreatorStep.BASIC -> ExerciseCreatorStep.BASIC
            ExerciseCreatorStep.EXTRA -> ExerciseCreatorStep.BASIC
            ExerciseCreatorStep.SUMMARY -> ExerciseCreatorStep.EXTRA
        }
        _uiState.value = _uiState.value.copy(currentStep = newStep)
    }

    fun create() {
        if (_uiState.value.isEditMode) {
            updateExercise()
        } else {
            createNewExercise()
        }
    }

    private fun createNewExercise() {
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
                muscleGroup = s.selectedMuscleGroups.map { it._id },
                equipment = s.selectedEquipment.map { it._id },
                hashtags = s.hashtags
            )
            _uiState.value = if (res.isSuccess) {
                _uiState.value.copy(isLoading = false, success = true)
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    error = res.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.error_exercise_creation_failed)
                )
            }
        }
    }

    private fun updateExercise() {
        val s = _uiState.value
        val exerciseId = s.editingExerciseId ?: return

        viewModelScope.launch {
            _uiState.value = s.copy(isLoading = true, error = null, success = false)

            val res = repo.updateExercise(
                exerciseId = exerciseId,
                name = s.name,
                description = s.description,
                imageUri = s.imageUri,
                videoUri = s.videoUri,
                difficulty = s.difficulty,
                isPublic = s.isPublic,
                muscleGroup = s.selectedMuscleGroups.map { it._id },
                equipment = s.selectedEquipment.map { it._id },
                hashtags = s.hashtags
            )

            _uiState.value = if (res.isSuccess) {
                _uiState.value.copy(isLoading = false, success = true)
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    error = res.exceptionOrNull()?.message ?: "Błąd podczas aktualizacji ćwiczenia"
                )
            }
        }
    }

    fun clearData() {
        _uiState.value = ExerciseCreatorState(
            availableMuscleGroups = _uiState.value.availableMuscleGroups,
            availableEquipment = _uiState.value.availableEquipment,
            currentStep = ExerciseCreatorStep.BASIC
        )
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
    fun clearSuccess() { _uiState.value = _uiState.value.copy(success = false) }
}