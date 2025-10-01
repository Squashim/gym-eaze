package com.example.gymeaze.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.R
import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.constants.Goals
import com.example.gymeaze.constants.PrivacySettings
import com.example.gymeaze.data.repository.TrainingPlanRepository
import com.example.gymeaze.data.models.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TrainingSet(
    val repetitions: Int? = null,
    val weight: Double? = null,
    val duration: Int? = null
) {
    val isRepsSet: Boolean get() = repetitions != null
    val isDurationSet: Boolean get() = duration != null
}

data class DayExercise(
    val exercise: Exercise,
    val isRepsSet: Boolean = true,
    val duration: Int = 0,
    val restAfterSet: Int = 60,
    val sets: List<TrainingSet> = listOf(TrainingSet(repetitions = 10))
)

data class TrainingDay(
    val dayNumber: Int,
    val name: String = "",
    val isRestDay: Boolean = false,
    val exercises: List<DayExercise> = emptyList(),
    val totalDuration: Int = 0
)

enum class TrainingPlanCreatorStep { BASIC, DAYS_AND_EXERCISES, SUMMARY }

data class CreateTrainingPlanState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,


    val name: String = "",
    val description: String = "",
    val goal: String = Goals.NONE,
    val visibility: String = PrivacySettings.PUBLIC,
    val advancedLevel: String = AdvancedLevel.EASY,
    val hashtags: List<String> = emptyList(),
    val imageUri: Uri? = null,

    val days: List<TrainingDay> = emptyList(),
    val selectedDayIndex: Int? = null,

    val currentStep: TrainingPlanCreatorStep = TrainingPlanCreatorStep.BASIC
) {
    val basicValid: Boolean get() = name.isNotBlank() &&
            name.length <= 150 &&
            imageUri != null &&
            (description.isEmpty() || description.length <= 850) &&
            hashtags.size <= 50

    val daysAndExercisesValid: Boolean get() = days.isNotEmpty() &&
            days.size <= 30 &&
            days.all { day ->
                day.name.isNotBlank() &&
                        day.name.length <= 100 &&
                        if (!day.isRestDay && day.exercises.isNotEmpty()) {
                            day.exercises.all { dayExercise ->
                                dayExercise.sets.isNotEmpty() &&
                                        dayExercise.sets.size <= 15 &&
                                        dayExercise.restAfterSet >= 30 &&
                                        dayExercise.restAfterSet <= 5400 &&
                                        dayExercise.sets.all { set ->
                                            if (set.isRepsSet) {
                                                set.repetitions != null &&
                                                        set.repetitions!! in 1..100 &&
                                                        (set.weight == null || set.weight!! in 0.0..300.0)
                                            } else {
                                                set.duration != null &&
                                                        set.duration!! in 30..21600
                                            }
                                        }
                            }
                        } else {
                            true
                        }
            }
}

class CreateTrainingPlanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TrainingPlanRepository(application)

    private val _uiState = MutableStateFlow(CreateTrainingPlanState())
    val uiState: StateFlow<CreateTrainingPlanState> = _uiState.asStateFlow()

    fun setName(name: String) {
        _uiState.value = _uiState.value.copy(name = name.take(150))
    }

    fun setDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description.take(850))
    }

    fun setGoal(goal: String) {
        _uiState.value = _uiState.value.copy(goal = goal)
    }

    fun setVisibility(visibility: String) {
        _uiState.value = _uiState.value.copy(visibility = visibility)
    }

    fun setAdvancedLevel(level: String) {
        _uiState.value = _uiState.value.copy(advancedLevel = level)
    }

    fun setHashtags(hashtags: List<String>) {
        val validatedList = hashtags.take(50).filter { hashtag ->
            hashtag.length >= 2 &&
                    hashtag.length <= 30 &&
                    hashtag.all { it.isLetterOrDigit() }
        }
        _uiState.value = _uiState.value.copy(hashtags = validatedList)
    }

    fun setImage(uri: Uri?) {
        _uiState.value = _uiState.value.copy(imageUri = uri)
    }

    fun addDay() {
        val currentDays = _uiState.value.days
        if (currentDays.size < 30) {
            val newDay = TrainingDay(
                dayNumber = currentDays.size + 1,
                name = "Dzień ${currentDays.size + 1}"
            )
            _uiState.value = _uiState.value.copy(days = currentDays + newDay)
        }
    }

    fun removeDay(index: Int) {
        val currentDays = _uiState.value.days.toMutableList()
        if (index in currentDays.indices) {
            currentDays.removeAt(index)
            val renumberedDays = currentDays.mapIndexed { idx, day ->
                day.copy(dayNumber = idx + 1)
            }
            _uiState.value = _uiState.value.copy(days = renumberedDays)
        }
    }

    fun updateDayName(index: Int, name: String) {
        val currentDays = _uiState.value.days.toMutableList()
        if (index in currentDays.indices) {
            currentDays[index] = currentDays[index].copy(name = name.take(100))
            _uiState.value = _uiState.value.copy(days = currentDays)
        }
    }

    fun toggleRestDay(index: Int) {
        val currentDays = _uiState.value.days.toMutableList()
        if (index in currentDays.indices) {
            val day = currentDays[index]
            currentDays[index] = day.copy(
                isRestDay = !day.isRestDay,
                exercises = if (!day.isRestDay) emptyList() else day.exercises
            )
            _uiState.value = _uiState.value.copy(days = currentDays)
        }
    }

    fun addExerciseToDay(dayIndex: Int, exercise: Exercise) {
        val currentDays = _uiState.value.days.toMutableList()
        if (dayIndex in currentDays.indices && !currentDays[dayIndex].isRestDay) {
            val day = currentDays[dayIndex]
            if (day.exercises.size < 15) {
                val dayExercise = DayExercise(exercise = exercise)
                val updatedExercises = day.exercises + dayExercise
                currentDays[dayIndex] = day.copy(exercises = updatedExercises)
                _uiState.value = _uiState.value.copy(days = currentDays)
            }
        }
    }

    fun removeExerciseFromDay(dayIndex: Int, exerciseIndex: Int) {
        val currentDays = _uiState.value.days.toMutableList()
        if (dayIndex in currentDays.indices) {
            val day = currentDays[dayIndex]
            val updatedExercises = day.exercises.toMutableList()
            if (exerciseIndex in updatedExercises.indices) {
                updatedExercises.removeAt(exerciseIndex)
                currentDays[dayIndex] = day.copy(exercises = updatedExercises)
                _uiState.value = _uiState.value.copy(days = currentDays)
            }
        }
    }

    fun updateExerciseRestTime(dayIndex: Int, exerciseIndex: Int, restTime: Int) {
        val currentDays = _uiState.value.days.toMutableList()
        if (dayIndex in currentDays.indices) {
            val day = currentDays[dayIndex]
            val exercises = day.exercises.toMutableList()
            if (exerciseIndex in exercises.indices) {
                exercises[exerciseIndex] = exercises[exerciseIndex].copy(
                    restAfterSet = restTime.coerceIn(30, 5400)
                )
                currentDays[dayIndex] = day.copy(exercises = exercises)
                _uiState.value = _uiState.value.copy(days = currentDays)
            }
        }
    }

    fun toggleExerciseType(dayIndex: Int, exerciseIndex: Int) {
        val currentDays = _uiState.value.days.toMutableList()
        if (dayIndex in currentDays.indices) {
            val day = currentDays[dayIndex]
            val exercises = day.exercises.toMutableList()
            if (exerciseIndex in exercises.indices) {
                val exercise = exercises[exerciseIndex]
                exercises[exerciseIndex] = exercise.copy(
                    isRepsSet = !exercise.isRepsSet,
                    sets = if (!exercise.isRepsSet) {
                        listOf(TrainingSet(repetitions = 10))
                    } else {
                        listOf(TrainingSet(duration = 60))
                    }
                )
                currentDays[dayIndex] = day.copy(exercises = exercises)
                _uiState.value = _uiState.value.copy(days = currentDays)
            }
        }
    }

    fun addSetToExercise(dayIndex: Int, exerciseIndex: Int) {
        val currentDays = _uiState.value.days.toMutableList()
        if (dayIndex in currentDays.indices) {
            val day = currentDays[dayIndex]
            val exercises = day.exercises.toMutableList()
            if (exerciseIndex in exercises.indices) {
                val exercise = exercises[exerciseIndex]
                if (exercise.sets.size < 15) {
                    val newSet = if (exercise.isRepsSet) {
                        TrainingSet(repetitions = 10)
                    } else {
                        TrainingSet(duration = 60)
                    }
                    exercises[exerciseIndex] = exercise.copy(sets = exercise.sets + newSet)
                    currentDays[dayIndex] = day.copy(exercises = exercises)
                    _uiState.value = _uiState.value.copy(days = currentDays)
                }
            }
        }
    }

    fun removeSetFromExercise(dayIndex: Int, exerciseIndex: Int, setIndex: Int) {
        val currentDays = _uiState.value.days.toMutableList()
        if (dayIndex in currentDays.indices) {
            val day = currentDays[dayIndex]
            val exercises = day.exercises.toMutableList()
            if (exerciseIndex in exercises.indices) {
                val exercise = exercises[exerciseIndex]
                val sets = exercise.sets.toMutableList()
                if (setIndex in sets.indices && sets.size > 1) {
                    sets.removeAt(setIndex)
                    exercises[exerciseIndex] = exercise.copy(sets = sets)
                    currentDays[dayIndex] = day.copy(exercises = exercises)
                    _uiState.value = _uiState.value.copy(days = currentDays)
                }
            }
        }
    }

    fun updateSet(dayIndex: Int, exerciseIndex: Int, setIndex: Int, set: TrainingSet) {
        val currentDays = _uiState.value.days.toMutableList()
        if (dayIndex in currentDays.indices) {
            val day = currentDays[dayIndex]
            val exercises = day.exercises.toMutableList()
            if (exerciseIndex in exercises.indices) {
                val exercise = exercises[exerciseIndex]
                val sets = exercise.sets.toMutableList()
                if (setIndex in sets.indices) {
                    sets[setIndex] = set
                    exercises[exerciseIndex] = exercise.copy(sets = sets)
                    currentDays[dayIndex] = day.copy(exercises = exercises)
                    _uiState.value = _uiState.value.copy(days = currentDays)
                }
            }
        }
    }

    fun nextStep() {
        val newStep = when (_uiState.value.currentStep) {
            TrainingPlanCreatorStep.BASIC -> TrainingPlanCreatorStep.DAYS_AND_EXERCISES
            TrainingPlanCreatorStep.DAYS_AND_EXERCISES -> TrainingPlanCreatorStep.SUMMARY
            TrainingPlanCreatorStep.SUMMARY -> TrainingPlanCreatorStep.SUMMARY
        }
        _uiState.value = _uiState.value.copy(currentStep = newStep)
    }

    fun prevStep() {
        val newStep = when (_uiState.value.currentStep) {
            TrainingPlanCreatorStep.BASIC -> TrainingPlanCreatorStep.BASIC
            TrainingPlanCreatorStep.DAYS_AND_EXERCISES -> TrainingPlanCreatorStep.BASIC
            TrainingPlanCreatorStep.SUMMARY -> TrainingPlanCreatorStep.DAYS_AND_EXERCISES
        }
        _uiState.value = _uiState.value.copy(currentStep = newStep)
    }

    fun createTrainingPlan() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)

            try {
                val result = repository.createTrainingPlan(
                    name = state.name,
                    description = state.description,
                    goal = state.goal,
                    visibility = state.visibility,
                    advancedLevel = state.advancedLevel,
                    hashtags = state.hashtags,
                    imageUri = state.imageUri!!,
                    days = state.days
                )

                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        success = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?:
                        getApplication<Application>().getString(R.string.error_training_plan_creation_failed)
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: getApplication<Application>().getString(R.string.error_unknown_occurred)
                )
            }
        }
    }

    fun clearData() {
        _uiState.value = CreateTrainingPlanState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(success = false)
    }
}