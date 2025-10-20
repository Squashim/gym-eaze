package com.example.gymeaze.viewmodel

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.ExerciseSet
import com.example.gymeaze.data.models.StartedDayDetails
import com.example.gymeaze.data.models.StartedExercise
import com.example.gymeaze.data.repository.StartedTrainingRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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
        if (this.userTrainingPlanId == userTrainingPlanId && this.dayNumber == dayNumber && _uiState.value.dayDetails != null) {
            return
        }

        this.userTrainingPlanId = userTrainingPlanId
        this.dayNumber = dayNumber
        _uiState.value = WorkoutPlayerUiState(playerState = PlayerState.LOADING)

        viewModelScope.launch {
            val result = repository.getStartedTrainingPlanDayDetails(userTrainingPlanId, dayNumber)
            result.onSuccess { details ->
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
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    playerState = PlayerState.ERROR,
                    error = exception.message ?: "Błąd ładowania szczegółów dnia."
                )
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
            startExerciseTimer(currentSet.duration)
        }
    }

    private fun startExerciseTimer(durationSeconds: Int) {
        _uiState.value = _uiState.value.copy(exerciseTimeRemaining = durationSeconds)
        exerciseTimer = object : CountDownTimer(durationSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _uiState.value = _uiState.value.copy(exerciseTimeRemaining = (millisUntilFinished / 1000).toInt())
            }
            override fun onFinish() {
                _uiState.value = _uiState.value.copy(exerciseTimeRemaining = 0)
            }
        }.start()
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
                    error = exception.message ?: "Błąd podczas zapisywania ukończenia dnia."
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
        _uiState.value = _uiState.value.copy(exerciseTimeRemaining = 0)
    }
    private fun cancelExerciseTimer() {
        exerciseTimer?.cancel()
        exerciseTimer = null
        if (_uiState.value.playerState != PlayerState.REST) {
            _uiState.value = _uiState.value.copy(exerciseTimeRemaining = 0)
        }
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