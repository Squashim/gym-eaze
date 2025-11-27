package com.example.gymeaze.viewmodel.trainingsPlan

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.R
import com.example.gymeaze.data.models.*
import com.example.gymeaze.data.repository.TrainingPlanRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class EditTrainingPlanState(
    val isLoading: Boolean = false,
    val originalPlan: TrainingPlan? = null,
    val isUpdating: Boolean = false,
    val error: String? = null,
    val updateSuccess: Boolean = false,
    val validationToast: String? = null,
    val isInitialLoadComplete: Boolean = false,

    val name: String = "",
    val description: String = "",
    val goal: String = "",
    val visibility: String = "",
    val advancedLevel: String = "",
    val hashtags: List<String> = emptyList(),
    val imageUri: Uri? = null,
    val days: List<TrainingDay> = emptyList(),

    val availableExercises: List<Exercise> = emptyList()
) {
    val basicValid: Boolean get() = name.isNotBlank() &&
            name.length <= 150 &&
            (description.isEmpty() || description.length <= 850) &&
            hashtags.size <= 50

    val daysAndExercisesValid: Boolean get() {
        if (days.isEmpty() || days.size > 30) return false
        return days.all { day ->
            if (day.name.isBlank() || day.name.length > 100) return@all false
            val exerciseIds = day.exercises.map { it.exercise.id }
            if (exerciseIds.toSet().size != exerciseIds.size) return@all false
            if (day.isRestDay) return@all true
            if (day.exercises.isEmpty()) return@all true
            day.exercises.all { dayExercise ->
                if (dayExercise.sets.isEmpty() || dayExercise.sets.size > 15) return@all false
                if (dayExercise.restAfterSet !in 30..5400) return@all false
                dayExercise.sets.all { set ->
                    if (set.isRepsSet) {
                        val repsValid = set.repetitions != null && set.repetitions in 1..100
                        val weightValid = set.weight == null || set.weight <= 300.0
                        repsValid && weightValid
                    } else {
                        set.duration != null && set.duration in 30..21600
                    }
                }
            }
        }
    }
}

class EditTrainingPlanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TrainingPlanRepository(application)

    private val _uiState = MutableStateFlow(EditTrainingPlanState())
    val uiState: StateFlow<EditTrainingPlanState> = _uiState.asStateFlow()

    private fun normalizeTrainingPlan(plan: TrainingPlan): List<TrainingDay> {
        return plan.days.map { apiDay ->
            TrainingDay(
                dayNumber = apiDay.dayNumber,
                name = apiDay.name,
                isRestDay = apiDay.isRestDay,
                exercises = apiDay.exercises.map { apiExercise ->
                    if (apiExercise.exercise != null) {
                        DayExercise(
                            exercise = apiExercise.exercise,
                            isRepsSet = apiExercise.isRepsSet,
                            duration = apiExercise.duration ?: 0,
                            restAfterSet = apiExercise.restAfterSet,
                            sets = apiExercise.sets.map {
                                TrainingSet(
                                    repetitions = it.repetitions,
                                    weight = it.weight,
                                    duration = it.duration
                                )
                            }
                        )
                    } else {
                        val pseudoExercise = Exercise(
                            id = apiExercise.originalId ?: apiExercise.id,
                            name = apiExercise.name,
                            description = apiExercise.description,
                            image = apiExercise.image,
                            video = apiExercise.video,
                            advancedLevel = apiExercise.advancedLevel,
                            muscleGroup = apiExercise.muscleGroup,
                            equipment = apiExercise.equipment,
                            hashtags = apiExercise.hashtags,
                            author = ExerciseAuthor(
                                id = "",
                                username = ""
                            ),
                            isLiked = false,
                            createdAt = "",
                            updatedAt = "",
                            visibility = "Publiczny"
                        )

                        DayExercise(
                            exercise = pseudoExercise,
                            isRepsSet = apiExercise.isRepsSet,
                            duration = apiExercise.duration ?: 0,
                            restAfterSet = apiExercise.restAfterSet,
                            sets = apiExercise.sets.map {
                                TrainingSet(
                                    repetitions = it.repetitions,
                                    weight = it.weight,
                                    duration = it.duration
                                )
                            }
                        )
                    }
                }
            )
        }
    }

    fun loadTrainingPlan(planId: String) {
        if (_uiState.value.isInitialLoadComplete) {
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val planResult = repository.getTrainingPlanById(planId)

                planResult.onSuccess { plan ->
                    val normalizedDays = normalizeTrainingPlan(plan);

                    _uiState.value = _uiState.value.copy(
                        originalPlan = plan,
                        name = plan.name,
                        description = plan.description ?: "",
                        goal = plan.goal,
                        visibility = plan.visibility,
                        advancedLevel = plan.advancedLevel,
                        hashtags = plan.hashtags,
                        days = normalizedDays,
                        isLoading = false,
                        isInitialLoadComplete = true,
                        error = null
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: getApplication<Application>().getString(R.string.error_plan_load_failed)
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "${getApplication<Application>().getString(R.string.error_unknown_occurred)}: ${e.message}"
                )
            }
        }
    }

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
                day.copy(dayNumber = idx + 1, name = "Dzień ${idx + 1}")
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

    fun clearTrainingPlan(){
        _uiState.value = EditTrainingPlanState()
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

        if (dayIndex !in currentDays.indices) {
            return
        }

        val day = currentDays[dayIndex]

        if (day.isRestDay) {
            showValidationError(getApplication<Application>().getString(R.string.error_exercise_add_to_rest_day))
            return
        }

        val exerciseExists = day.exercises.any { it.exercise.id == exercise.id }
        if (exerciseExists) {
            showValidationError(getApplication<Application>().getString(R.string.error_exercise_already_added))
            return
        }

        if (day.exercises.size >= 15) {
            showValidationError(getApplication<Application>().getString(R.string.error_exercise_max_limit))
            return
        }

        val dayExercise = DayExercise(
            exercise = exercise,
            isRepsSet = true,
            sets = listOf(TrainingSet(repetitions = 10))
        )

        val updatedExercises = day.exercises + dayExercise
        currentDays[dayIndex] = day.copy(exercises = updatedExercises)
        _uiState.value = _uiState.value.copy(days = currentDays)
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

    fun shouldWarnBeforeRestDayConversion(dayIndex: Int): Boolean {
        val days = _uiState.value.days
        return dayIndex in days.indices &&
                !days[dayIndex].isRestDay &&
                days[dayIndex].exercises.isNotEmpty()
    }

    private fun validateDaysAndExercises(days: List<TrainingDay>): List<String> {
        val errors = mutableListOf<String>()
        val context = getApplication<Application>()

        if (days.isEmpty()) {
            errors.add(context.getString(R.string.error_plan_no_days))
            return errors
        }

        if (days.size > 30) {
            errors.add(context.getString(R.string.error_plan_max_days))
            return errors
        }

        days.forEachIndexed { dayIndex, day ->
            if (day.name.isBlank()) {
                errors.add(context.getString(R.string.error_day_name_required, dayIndex + 1))
            }
            if (day.name.length > 100) {
                errors.add(context.getString(R.string.error_day_name_too_long, dayIndex + 1))
            }

            val exerciseIds = day.exercises.map { it.exercise.id }
            if (exerciseIds.toSet().size != exerciseIds.size) {
                errors.add(context.getString(R.string.error_day_duplicate_exercise, dayIndex + 1))
            }

            if (!day.isRestDay && day.exercises.isNotEmpty()) {
                day.exercises.forEachIndexed { exerciseIndex, dayExercise ->
                    val exerciseName = dayExercise.exercise.name

                    if (dayExercise.sets.isEmpty()) {
                        errors.add(context.getString(R.string.error_exercise_no_sets, dayIndex + 1, exerciseName))
                    }
                    if (dayExercise.sets.size > 15) {
                        errors.add(context.getString(R.string.error_exercise_max_sets, dayIndex + 1, exerciseName))
                    }

                    if (dayExercise.restAfterSet !in 30..5400) {
                        errors.add(context.getString(R.string.error_exercise_rest_time, dayIndex + 1, exerciseName))
                    }

                    dayExercise.sets.forEachIndexed { setIndex, set ->
                        if (set.isRepsSet) {
                            if (set.repetitions == null || set.repetitions !in 1..100) {
                                errors.add(context.getString(R.string.error_set_reps_range, dayIndex + 1, exerciseName, setIndex + 1))
                            }
                            if (set.weight != null && set.weight > 300.0) {
                                errors.add(context.getString(R.string.error_set_weight_range, dayIndex + 1, exerciseName, setIndex + 1))
                            }
                        } else {
                            if (set.duration == null || set.duration !in 30..21600) {
                                errors.add(context.getString(R.string.error_set_time_range, dayIndex + 1, exerciseName, setIndex + 1))
                            }
                        }
                    }
                }
            }
        }
        return errors
    }

    fun validateCurrentStep(): Boolean {
        val state = _uiState.value
        val context = getApplication<Application>()

        if (!state.basicValid) {
            when {
                state.name.isBlank() -> showValidationError(context.getString(R.string.error_plan_name_required))
                state.name.length > 150 -> showValidationError(context.getString(R.string.error_plan_name_too_long))
                state.description.length > 850 -> showValidationError(context.getString(R.string.error_plan_desc_too_long))
                state.hashtags.size > 50 -> showValidationError(context.getString(R.string.error_plan_hashtags_limit))
                else -> showValidationError(context.getString(R.string.error_plan_fill_all_required))
            }
            return false
        }

        if (!state.daysAndExercisesValid) {
            val validationErrors = validateDaysAndExercises(state.days)
            if (validationErrors.isNotEmpty()) {
                showValidationError(validationErrors.first())
                return false
            }
        }

        return true
    }

    fun saveTrainingPlan() {
        val currentState = _uiState.value
        val originalPlan = currentState.originalPlan ?: return

        if (!validateCurrentStep()) {
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isUpdating = true, error = null)

                val updateData = UpdateTrainingPlanData(
                    name = currentState.name.trim(),
                    description = currentState.description.trim().takeIf { it.isNotEmpty() },
                    goal = currentState.goal,
                    visibility = currentState.visibility,
                    advancedLevel = currentState.advancedLevel,
                    hashtags = currentState.hashtags,
                    days = currentState.days.map { day ->
                        UpdateTrainingDay(
                            dayNumber = day.dayNumber,
                            name = day.name,
                            isRestDay = day.isRestDay,
                            exercises = day.exercises.map { exercise ->
                                UpdateDayExercise(
                                    id = exercise.exercise.id,
                                    isRepsSet = exercise.isRepsSet,
                                    duration = exercise.duration,
                                    restAfterSet = exercise.restAfterSet,
                                    sets = exercise.sets.map { set ->
                                        UpdateExerciseSet(
                                            repetitions = set.repetitions,
                                            weight = set.weight,
                                            duration = set.duration
                                        )
                                    }
                                )
                            }
                        )
                    },
                    imageUri = currentState.imageUri?.toString()
                )

                val result = repository.updateTrainingPlan(originalPlan.id, updateData)

                result.onSuccess { updatedPlan ->
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        updateSuccess = true,
                        originalPlan = updatedPlan,
                        error = null
                    )
                }.onFailure { exception ->
                    if (exception.message?.contains(getApplication<Application>().getString(R.string.error_json_exception)) == true) {
                        _uiState.value = _uiState.value.copy(
                            isUpdating = false,
                            updateSuccess = true,
                            error = null
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isUpdating = false,
                            error = exception.message ?: getApplication<Application>().getString(R.string.error_plan_update_failed)
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUpdating = false,
                    error = e.message ?: getApplication<Application>().getString(R.string.error_unknown_occurred)
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(updateSuccess = false)
    }

    fun clearValidationToast() {
        _uiState.value = _uiState.value.copy(validationToast = null)
    }

    fun showValidationError(message: String) {
        _uiState.value = _uiState.value.copy(validationToast = message)
    }


}