package com.example.gymeaze.ui.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymeaze.data.models.ExerciseSet
import com.example.gymeaze.data.models.StartedExercise
import com.example.gymeaze.ui.components.VideoPlayer
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.PlayerState
import com.example.gymeaze.viewmodel.WorkoutNavigationEvent
import com.example.gymeaze.viewmodel.WorkoutPlayerUiState
import com.example.gymeaze.viewmodel.WorkoutPlayerViewModel
import com.example.gymeaze.viewmodel.WorkoutSummaryData
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun WorkoutPlayerScreen(
    userTrainingPlanId: String,
    dayNumber: Int,
    onBackClick: () -> Unit,
    viewModel: WorkoutPlayerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userTrainingPlanId, dayNumber) {
        viewModel.loadWorkoutDay(userTrainingPlanId, dayNumber)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.cleanup()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is WorkoutNavigationEvent.NavigateBack -> {
                    onBackClick()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            if (uiState.playerState != PlayerState.SUMMARY) {
                WorkoutPlayerAppBar(
                    state = uiState,
                    onBackClick = onBackClick
                )
            }
        },
        containerColor = getBackgroundColor()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = uiState.playerState,
                transitionSpec = {
                    fadeIn() + slideInHorizontally { it } with fadeOut() + slideOutHorizontally { -it }
                }, label = "PlayerStateAnimation"
            ) { targetState ->
                when (targetState) {
                    PlayerState.LOADING -> LoadingState()
                    PlayerState.EXERCISE -> ExerciseState(uiState)
                    PlayerState.REST -> RestState(
                        state = uiState,
                        onDecreaseTime = viewModel::decreaseRestTime,
                        onIncreaseTime = viewModel::increaseRestTime
                    )
                    PlayerState.FINISHED -> FinishedState(
                        state = uiState,
                        onComplete = viewModel::prepareAndCompleteDay
                    )
                    PlayerState.COMPLETING -> CompletingState()
                    PlayerState.SUMMARY -> WorkoutSummary(
                        summaryData = uiState.summaryData,
                        onFinish = viewModel::finalizeWorkoutAndNavigateBack
                    )
                    PlayerState.ERROR -> ErrorState(uiState.error, onRetry = { viewModel.loadWorkoutDay(userTrainingPlanId, dayNumber) })
                }
            }

            if (uiState.playerState == PlayerState.EXERCISE || uiState.playerState == PlayerState.REST) {
                if (uiState.dayDetails != null) {
                    WorkoutNavigationButtons(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        onPrevious = viewModel::previousStep,
                        onNext = viewModel::nextStep,
                        isPreviousEnabled = !(uiState.currentExerciseIndex == 0 && uiState.currentSetIndex == 0 && uiState.playerState == PlayerState.EXERCISE)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = getPrimaryColor())
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExerciseState(state: WorkoutPlayerUiState) {
    val exercise = state.currentExercise
    val set = state.currentSet
    if (exercise == null || set == null || state.dayDetails == null) {
        LoadingState()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp))
                .background(getSurfaceColor())
        ) {
            VideoPlayer(url = exercise.video, modifier = Modifier.fillMaxSize())
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = exercise.name,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = getOnSurfaceColor(),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Ćwiczenie ${state.currentExerciseIndex + 1}/${state.totalExercises} | Seria ${state.currentSetIndex + 1}/${state.totalSetsInCurrentExercise}",
            fontSize = 14.sp,
            color = getOnSurfaceSecondaryColor(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (set.repetitions != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoChip("Powtórzenia", set.repetitions.toString())
                if (set.weight != null && set.weight > 0) {
                    InfoChip("Ciężar", "${set.weight} kg")
                }
            }
        } else if (set.duration != null && set.duration > 0) {
            TimerDisplay(
                timeRemaining = state.exerciseTimeRemaining,
                label = "Czas ćwiczenia"
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (exercise.muscleGroup.isNotEmpty()) {
            InfoSection(title = "Zaangażowane partie", icon = Icons.Default.SportsGymnastics) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    exercise.muscleGroup.forEach { muscle ->
                        InfoChipSmall(text = muscle.name)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (exercise.equipment.isNotEmpty()) {
            InfoSection(title = "Wymagany sprzęt", icon = Icons.Default.MonitorWeight) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    exercise.equipment.forEach { eq ->
                        InfoChipSmall(text = eq.name)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun InfoSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = getOnSurfaceSecondaryColor(),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = getOnSurfaceSecondaryColor()
            )
        }
        content()
    }
}

@Composable
private fun InfoChipSmall(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = getSurfaceColor(),
        border = BorderStroke(1.dp, getInputBorderColor().copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = getOnSurfaceColor(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RestState(
    state: WorkoutPlayerUiState,
    onDecreaseTime: () -> Unit,
    onIncreaseTime: () -> Unit
) {

    if (state.dayDetails == null) {
        LoadingState()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TimerDisplay(
            timeRemaining = state.restTimeRemaining,
            label = "Odpoczynek"
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onDecreaseTime,
                modifier = Modifier.size(width = 100.dp, height = 48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = getSurfaceColor(),
                    contentColor = getOnSurfaceColor()
                ),
                border = BorderStroke(1.dp, getInputBorderColor()),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.RemoveCircleOutline, contentDescription = "-15s", modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("15s", fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            Button(
                onClick = onIncreaseTime,
                modifier = Modifier.size(width = 100.dp, height = 48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = getSurfaceColor(),
                    contentColor = getOnSurfaceColor()
                ),
                border = BorderStroke(1.dp, getInputBorderColor()),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = "+15s", modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("15s", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun FinishedState(state: WorkoutPlayerUiState, onComplete: () -> Unit) {

    if (state.dayDetails == null) {
        LoadingState()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = "Gratulacje",
            tint = getPrimaryColor(),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Gratulacje!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = getOnSurfaceColor()
        )
        Text(
            text = "Ukończyłeś wszystkie ćwiczenia na dzisiaj.",
            fontSize = 16.sp,
            color = getOnSurfaceSecondaryColor(),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        Button(
            onClick = onComplete,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = state.playerState != PlayerState.COMPLETING,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = getPrimaryColor())
        ) {
            if (state.playerState == PlayerState.COMPLETING) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = getOnPrimaryColor(),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Oznacz Dzień jako Ukończony", fontWeight = FontWeight.SemiBold)
            }
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Błąd zapisu: $it",
                color = getErrorColor(),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WorkoutSummary(
    summaryData: WorkoutSummaryData?,
    onFinish: () -> Unit
) {
    if (summaryData == null) {
        LoadingState()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircleOutline,
            contentDescription = "Zakończono",
            tint = getPrimaryColor(),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Dzień Ukończony!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = getOnSurfaceColor()
        )
        Spacer(modifier = Modifier.height(16.dp))

        SummaryInfoRow("Wykonane ćwiczenia:", summaryData.exerciseCount.toString())
        SummaryInfoRow("Łączna liczba serii:", summaryData.totalSets.toString())
        SummaryInfoRow("Szacowany czas:", formatDurationForSummary(summaryData.estimatedDurationSeconds))

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = getPrimaryColor())
        ) {
            Text("OK", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SummaryInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 15.sp, color = getOnSurfaceSecondaryColor())
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = getOnSurfaceColor())
    }
}

private fun formatDurationForSummary(totalSeconds: Int): String {
    if (totalSeconds <= 0) return "-"
    val hours = TimeUnit.SECONDS.toHours(totalSeconds.toLong())
    val minutes = TimeUnit.SECONDS.toMinutes(totalSeconds.toLong()) % 60
    val seconds = totalSeconds % 60

    return buildString {
        if (hours > 0) append("${hours}h ")
        if (minutes > 0) append("${minutes}min ")
        if (seconds > 0 || (hours == 0L && minutes == 0L)) append("${seconds}s")
    }.trim()
}

@Composable
private fun CompletingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = getPrimaryColor())
            Spacer(modifier = Modifier.height(16.dp))
            Text("Zapisywanie postępu...", color = getOnSurfaceSecondaryColor())
        }
    }
}


@Composable
private fun ErrorState(error: String?, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = "Błąd",
            tint = getErrorColor(),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Wystąpił błąd",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = getOnSurfaceColor()
        )
        Text(
            text = error ?: "Nieznany błąd.",
            fontSize = 14.sp,
            color = getOnSurfaceSecondaryColor(),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )
        Button(onClick = onRetry) {
            Text("Spróbuj ponownie")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutPlayerAppBar(
    state: WorkoutPlayerUiState,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            if ((state.playerState == PlayerState.EXERCISE || state.playerState == PlayerState.REST) && state.dayDetails != null) {
                Text(
                    state.dayDetails.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = getOnSurfaceColor()
                )
            } else {
                Text("")
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Zakończ trening",
                    tint = getOnSurfaceColor()
                )
            }
        },
        actions = {
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = getBackgroundColor()
        )
    )
}

@Composable
private fun InfoChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = getOnSurfaceSecondaryColor()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = getPrimaryColor()
        )
    }
}

@Composable
private fun TimerDisplay(timeRemaining: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 18.sp,
            color = getOnSurfaceSecondaryColor()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = formatTime(timeRemaining),
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            color = getPrimaryColor()
        )
    }
}

@Composable
private fun WorkoutNavigationButtons(
    modifier: Modifier = Modifier,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    isPreviousEnabled: Boolean = true,
    isNextEnabled: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onPrevious,
            enabled = isPreviousEnabled,
            modifier = Modifier.weight(1f).height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = getSurfaceColor(),
                contentColor = getOnSurfaceColor(),
                disabledContainerColor = getSurfaceColor().copy(alpha = 0.5f)
            ),
            border = BorderStroke(1.dp, getInputBorderColor())
        ) {
            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Poprzedni")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Cofnij")
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = onNext,
            enabled = isNextEnabled,
            modifier = Modifier.weight(1f).height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = getPrimaryColor(),
                contentColor = getOnPrimaryColor(),
                disabledContainerColor = getPrimaryColor().copy(alpha = 0.5f)
            )
        ) {
            Text("Dalej")
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.ArrowForwardIos, contentDescription = "Następny")
        }
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = TimeUnit.SECONDS.toMinutes(seconds.toLong())
    val remainingSeconds = seconds - TimeUnit.MINUTES.toSeconds(minutes)
    return String.format("%02d:%02d", minutes, remainingSeconds)
}