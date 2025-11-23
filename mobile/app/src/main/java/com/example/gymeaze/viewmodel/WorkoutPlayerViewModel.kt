package com.example.gymeaze.viewmodel

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.R
import com.example.gymeaze.data.models.ExerciseSet
import com.example.gymeaze.data.models.StartedDayDetails
import com.example.gymeaze.data.models.StartedExercise
import com.example.gymeaze.data.repository.StartedTrainingRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PlayerState {
    LOADING,
    EXERCISE,
    REST,
    FINISHED,
    COMPLETING,
    SUMMARY,
    ERROR
}

data class WorkoutSummaryData(
    val exerciseCount: Int = 0,
    val totalSets: Int = 0,
    val estimatedDurationSeconds: Int = 0
)

data class WorkoutPlayerUiState(
    val playerState: PlayerState = PlayerState.LOADING,
    val dayDetails: StartedDayDetails? = null,
    val currentExerciseIndex: Int = 0,
    val currentSetIndex: Int = 0,
    val restTimeRemaining: Int = 0,
    val exerciseTimeRemaining: Int = 0,
    val isExerciseTimerRunning: Boolean = false,
    val error: String? = null,
    val isWorkoutComplete: Boolean = false,
    val summaryData: WorkoutSummaryData? = null
) {
    val currentExercise: StartedExercise?
        get() = dayDetails?.exercises?.getOrNull(currentExerciseIndex)

    val currentSet: ExerciseSet?
        get() = currentExercise?.sets?.getOrNull(currentSetIndex)

    val totalExercises: Int
        get() = dayDetails?.exercises?.size ?: 0

    val totalSetsInCurrentExercise: Int
        get() = currentExercise?.sets?.size ?: 0
}

sealed class WorkoutNavigationEvent {
    object NavigateBack : WorkoutNavigationEvent()
}

class WorkoutPlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StartedTrainingRepository(application)

    private val _uiState = MutableStateFlow(WorkoutPlayerUiState())
    val uiState: StateFlow<WorkoutPlayerUiState> = _uiState.asStateFlow()

    private var restTimer: CountDownTimer? = null
    private var exerciseTimer: CountDownTimer? = null
    private var userTrainingPlanId: String? = null
    private var dayNumber: Int? = null

    private val _navigationEvent = MutableSharedFlow<WorkoutNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun loadWorkoutDay(userTrainingPlanId: String, dayNumber: Int) {
        if (this.userTrainingPlanId == userTrainingPlanId &&
            this.dayNumber == dayNumber &&
            _uiState.value.dayDetails != null &&
            _uiState.value.playerState !in listOf(PlayerState.ERROR, PlayerState.SUMMARY, PlayerState.COMPLETING, PlayerState.LOADING))
        {
            _uiState.update { it.copy(playerState = PlayerState.EXERCISE, error = null) }
            startExerciseTimerIfNeeded()
            return
        }

        this.userTrainingPlanId = userTrainingPlanId
        this.dayNumber = dayNumber

        _uiState.value = WorkoutPlayerUiState(
            playerState = PlayerState.LOADING,
            error = null,
            dayDetails = null,
            summaryData = null
        )

        viewModelScope.launch {
            val result = repository.getStartedTrainingPlanDayDetails(userTrainingPlanId, dayNumber)
            result.onSuccess { details ->
                if (_uiState.value.playerState == PlayerState.LOADING) {
                    if (details.exercises.isEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            playerState = PlayerState.FINISHED,
                            dayDetails = details,
                            isWorkoutComplete = true
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            playerState = PlayerState.EXERCISE,
                            dayDetails = details
                        )
                        startExerciseTimerIfNeeded()
                    }
                }
            }.onFailure { exception ->
                if (_uiState.value.playerState == PlayerState.LOADING) {
                    _uiState.value = _uiState.value.copy(
                        playerState = PlayerState.ERROR,
                        error = exception.message ?: getApplication<Application>().getString(R.string.error_workout_loading_details)
                    )
                }
            }
        }
    }

    fun nextStep() {
        cancelTimers()
        val currentState = _uiState.value

        if (currentState.playerState == PlayerState.REST) {
            moveToNextSetOrExercise()
            return
        }

        val currentExercise = currentState.currentExercise ?: return

        val isLastSetInExercise = currentState.currentSetIndex >= currentExercise.sets.size - 1
        val isLastExerciseInWorkout = currentState.currentExerciseIndex >= currentState.totalExercises - 1

        when {
            !isLastSetInExercise -> {
                startRestTimer(currentExercise.restAfterSet)
            }
            !isLastExerciseInWorkout -> {
                startRestTimer(currentExercise.restAfterSet)
            }
            else -> {
                _uiState.value = currentState.copy(
                    playerState = PlayerState.FINISHED,
                    isWorkoutComplete = true
                )
            }
        }
    }

    private fun startRestTimer(durationSeconds: Int) {
        val effectiveDuration = maxOf(0, durationSeconds)
        cancelTimers()

        _uiState.value = _uiState.value.copy(
            playerState = PlayerState.REST,
            restTimeRemaining = effectiveDuration
        )

        if (effectiveDuration <= 0) {
            moveToNextSetOrExercise()
            return
        }

        restTimer = object : CountDownTimer(durationSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (_uiState.value.playerState == PlayerState.REST) {
                    _uiState.value = _uiState.value.copy(restTimeRemaining = (millisUntilFinished / 1000).toInt())
                } else {
                    cancel()
                }
            }

            override fun onFinish() {
                if (_uiState.value.playerState == PlayerState.REST) {
                    _uiState.value = _uiState.value.copy(restTimeRemaining = 0)
                    moveToNextSetOrExercise()
                }
            }
        }.start()
    }

    fun decreaseRestTime(seconds: Int = 15) {
        if (_uiState.value.playerState == PlayerState.REST) {
            val currentRemaining = _uiState.value.restTimeRemaining
            val newTime = maxOf(0, currentRemaining - seconds)
            startRestTimer(newTime)
        }
    }

    fun increaseRestTime(seconds: Int = 15) {
        if (_uiState.value.playerState == PlayerState.REST) {
            val currentRemaining = _uiState.value.restTimeRemaining
            val newTime = currentRemaining + seconds
            startRestTimer(newTime)
        }
    }



    private fun moveToNextSetOrExercise() {
        val currentState = _uiState.value
        val currentExercise = currentState.currentExercise ?: return
        val isLastSetInExercise = currentState.currentSetIndex >= currentExercise.sets.size - 1

        if (!isLastSetInExercise) {
            _uiState.value = currentState.copy(
                playerState = PlayerState.EXERCISE,
                currentSetIndex = currentState.currentSetIndex + 1
            )
            startExerciseTimerIfNeeded()
        } else {
            _uiState.value = currentState.copy(
                playerState = PlayerState.EXERCISE,
                currentExerciseIndex = currentState.currentExerciseIndex + 1,
                currentSetIndex = 0
            )
            startExerciseTimerIfNeeded()
        }
    }

    fun previousStep() {
        cancelTimers()
        val currentState = _uiState.value

        when {
            currentState.playerState == PlayerState.REST -> {
                _uiState.value = currentState.copy(playerState = PlayerState.EXERCISE)
                startExerciseTimerIfNeeded()
            }
            currentState.currentSetIndex > 0 -> {
                _uiState.value = currentState.copy(currentSetIndex = currentState.currentSetIndex - 1)
                startExerciseTimerIfNeeded()
            }
            currentState.currentExerciseIndex > 0 -> {
                val previousExerciseIndex = currentState.currentExerciseIndex - 1
                val previousExercise = currentState.dayDetails?.exercises?.getOrNull(previousExerciseIndex)
                val lastSetIndexInPrevious = (previousExercise?.sets?.size ?: 1) - 1
                _uiState.value = currentState.copy(
                    currentExerciseIndex = previousExerciseIndex,
                    currentSetIndex = lastSetIndexInPrevious
                )
                startExerciseTimerIfNeeded()
            }
        }
    }

    private fun startExerciseTimerIfNeeded() {
        cancelExerciseTimer()
        val currentSet = _uiState.value.currentSet
        if (currentSet?.duration != null && currentSet.duration > 0) {
            startExerciseTimer()
        } else {
            _uiState.value = _uiState.value.copy(isExerciseTimerRunning = false, exerciseTimeRemaining = 0)
        }
    }

    fun startExerciseTimer(durationSeconds: Int? = null) {

        val startTime = when {
            durationSeconds != null -> durationSeconds
            _uiState.value.exerciseTimeRemaining > 0 && !_uiState.value.isExerciseTimerRunning -> _uiState.value.exerciseTimeRemaining
            else -> _uiState.value.currentSet?.duration ?: 0
        }

        if (startTime <= 0 || _uiState.value.playerState != PlayerState.EXERCISE) {

            if (_uiState.value.playerState == PlayerState.EXERCISE) {
                _uiState.value = _uiState.value.copy(isExerciseTimerRunning = false, exerciseTimeRemaining = 0)
            }
            return
        }

        cancelExerciseTimer()
        _uiState.value = _uiState.value.copy(
            exerciseTimeRemaining = startTime,
            isExerciseTimerRunning = true
        )
        exerciseTimer = object : CountDownTimer(startTime * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (_uiState.value.playerState == PlayerState.EXERCISE && _uiState.value.isExerciseTimerRunning) {
                    _uiState.value = _uiState.value.copy(exerciseTimeRemaining = (millisUntilFinished / 1000).toInt())
                } else {
                    cancel()
                }
            }
            override fun onFinish() {
                if (_uiState.value.playerState == PlayerState.EXERCISE) {
                    _uiState.value = _uiState.value.copy(
                        exerciseTimeRemaining = 0,
                        isExerciseTimerRunning = false
                    )
                }
            }
        }.start()
    }

    fun stopExerciseTimer() {
        if (_uiState.value.isExerciseTimerRunning) {
            exerciseTimer?.cancel()
            exerciseTimer = null
            _uiState.value = _uiState.value.copy(isExerciseTimerRunning = false)
        }
    }

    fun resetExerciseTimer() {
        val duration = _uiState.value.currentSet?.duration
        if (_uiState.value.playerState == PlayerState.EXERCISE && duration != null && duration > 0) {
            cancelExerciseTimer()
            _uiState.value = _uiState.value.copy(
                exerciseTimeRemaining = duration,
                isExerciseTimerRunning = false
            )
        }
    }

    fun prepareAndCompleteDay() {
        val currentPlanId = userTrainingPlanId ?: return
        val currentDayNumber = dayNumber ?: return
        val details = _uiState.value.dayDetails ?: return

        if (_uiState.value.playerState == PlayerState.COMPLETING) return

        _uiState.value = _uiState.value.copy(playerState = PlayerState.COMPLETING, error = null)

        viewModelScope.launch {
            val result = repository.completeDayTraining(currentPlanId, currentDayNumber)
            result.onSuccess { response ->
                val exerciseCount = details.exercises.size
                val totalSets = details.exercises.sumOf { it.sets.size }
                val summary = WorkoutSummaryData(
                    exerciseCount = exerciseCount,
                    totalSets = totalSets,
                    estimatedDurationSeconds = details.totalDuration
                )

                _uiState.value = _uiState.value.copy(
                    playerState = PlayerState.SUMMARY,
                    summaryData = summary
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    playerState = PlayerState.FINISHED,
                    error = exception.message ?: getApplication<Application>().getString(R.string.error_workout_save_complete)
                )
            }
        }
    }

    fun finalizeWorkoutAndNavigateBack() {
        viewModelScope.launch {
            _navigationEvent.emit(WorkoutNavigationEvent.NavigateBack)
        }
    }

    private fun cancelTimers() {
        restTimer?.cancel()
        exerciseTimer?.cancel()
        restTimer = null
        exerciseTimer = null
        _uiState.value = _uiState.value.copy(
            isExerciseTimerRunning = false,
            exerciseTimeRemaining = 0,
            restTimeRemaining = 0
        )
    }

    private fun cancelExerciseTimer() {
        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseTimer = null
        }
        _uiState.value = _uiState.value.copy(isExerciseTimerRunning = false)
    }

    fun cleanup() {
        cancelTimers()
        userTrainingPlanId = null
        dayNumber = null
    }

    override fun onCleared() {
        super.onCleared()
        cancelTimers()
    }
}