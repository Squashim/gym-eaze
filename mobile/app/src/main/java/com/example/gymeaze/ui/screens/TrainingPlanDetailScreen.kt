package com.example.gymeaze.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymeaze.R
import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.constants.Goals
import com.example.gymeaze.data.models.Comment
import com.example.gymeaze.data.models.TrainingDay
import com.example.gymeaze.data.models.DayExercise
import com.example.gymeaze.data.models.TrainingPlan
import com.example.gymeaze.data.models.timeAgo
import com.example.gymeaze.ui.components.LoadingScreen
import com.example.gymeaze.ui.components.ReportDialog
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.util.formatToRelativeTime
import com.example.gymeaze.viewmodel.trainingsPlan.SnackbarType
import com.example.gymeaze.viewmodel.trainingsPlan.TrainingPlanDetailViewModel
import com.example.gymeaze.ui.components.user.UserAvatar
import com.example.gymeaze.ui.screens.comments.DeleteCommentDialog
import com.example.gymeaze.ui.screens.exercises.logggedIn.EditCommentDialog
import com.example.gymeaze.ui.screens.posts.guest.components.LoginToLikeDialog
import com.example.gymeaze.util.GlobalFavoriteState
import com.example.gymeaze.viewmodel.ReportItemType
import com.example.gymeaze.viewmodel.ReportViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingPlanDetailScreen(
    planId: String,
    onBackClick: () -> Unit,
    onExerciseClick: (String) -> Unit = {},
    viewModel: TrainingPlanDetailViewModel = viewModel(),
    onUserClick: (String) -> Unit = {},
    navigateToWorkoutPlayer: (userTrainingPlanId: String, planName: String, dayNumber: Int) -> Unit,
    isUserLoggedIn: Boolean = true,
    onEditPlanClick: (String) -> Unit = {}
) {
    val reportViewModel: ReportViewModel = viewModel()

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val isDownloadingPdf by viewModel.isDownloadingPdf.collectAsState()

    var showLoginDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(planId) {
        viewModel.resetDeletedFlag()
        viewModel.loadTrainingPlan(planId)
    }

    LaunchedEffect(Unit) {
        GlobalFavoriteState.getInstance(context).forceRefreshFromBackend()
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSnackbarMessage()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            navigateToWorkoutPlayer(event.userTrainingPlanId, event.planName, event.firstDayNumber)
        }
    }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            onBackClick()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    Snackbar(
                        snackbarData = snackbarData,
                        containerColor = when (uiState.snackbarType) {
                            SnackbarType.ERROR -> getErrorColor()
                            SnackbarType.SUCCESS -> getPrimaryColor()
                        },
                        contentColor = if (uiState.error != null) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            getBackgroundColor()
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            )
        },
        containerColor = getBackgroundColor()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingScreen()
                }

                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.plan_detail_loading_error),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = getOnSurfaceColor()
                            )
                            Text(
                                text = uiState.error!!,
                                fontSize = 14.sp,
                                color = getOnSurfaceSecondaryColor(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadTrainingPlan(planId) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = getPrimaryColor()
                                )
                            ) {
                                Text(stringResource(R.string.action_try_again))
                            }
                        }
                    }
                }

                uiState.trainingPlan != null -> {
                    TrainingPlanDetailContent(
                        plan = uiState.trainingPlan!!,
                        viewModel = viewModel,
                        reportViewModel = reportViewModel,
                        isUserLoggedIn = isUserLoggedIn,
                        onShowLoginDialog = { showLoginDialog = true },
                        onExerciseClick = onExerciseClick,
                        onUserClick = onUserClick,
                        onBackClick = onBackClick,
                        isDownloadingPdf = isDownloadingPdf,
                        onReportClick = { showReportDialog = true },
                        onEditPlanClick = onEditPlanClick,
                        onDeletePlanClick = { showDeleteDialog = true }
                    )
                }
            }

            if (showDeleteDialog && uiState.trainingPlan != null) {
                DeleteConfirmationDialog(
                    planName = uiState.trainingPlan!!.name,
                    onConfirm = {
                        viewModel.deleteTrainingPlan(uiState.trainingPlan!!.id)
                        showDeleteDialog = false
                    },
                    onDismiss = {
                        showDeleteDialog = false
                    }
                )
            }
        }
    }

    if (showLoginDialog) {
        LoginToLikeDialog(
            onDismiss = { showLoginDialog = false }
        )
    }

    if (showReportDialog) {
        ReportDialog(
            itemId = planId,
            itemType = ReportItemType.TRAINING,
            onDismiss = {
                showReportDialog = false
                reportViewModel.resetState()
            },
            viewModel = reportViewModel
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    planName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = stringResource(R.string.dialog_delete_title),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = stringResource(R.string.dialog_delete_msg_plan),
                fontSize = 14.sp
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(
                    text = stringResource(R.string.action_delete),
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.action_cancel),
                    color = getOnSurfaceColor()
                )
            }
        },
        containerColor = getSurfaceColor(),
        textContentColor = getOnSurfaceColor()
    )
}

@Composable
fun DifficultyBadgeTrainingPlan(
    difficulty: String,
    modifier: Modifier = Modifier
) {
    val (textRes, backgroundColor, textColor) = when (difficulty) {
        AdvancedLevel.NONE -> Triple(AdvancedLevel.getLabelResId(AdvancedLevel.NONE), getOnSurfaceColor().copy(alpha = 0.1f), getOnSurfaceSecondaryColor())
        AdvancedLevel.EASY -> Triple(AdvancedLevel.getLabelResId(AdvancedLevel.EASY), getPrimaryColor().copy(alpha = 0.2f), getPrimaryColor())
        AdvancedLevel.MEDIUM -> Triple(AdvancedLevel.getLabelResId(AdvancedLevel.MEDIUM), getYellowColor(), getOnYellowColor())
        AdvancedLevel.HARD -> Triple(AdvancedLevel.getLabelResId(AdvancedLevel.HARD), getErrorColor().copy(alpha = 0.2f), getErrorColor())
        else -> Triple(AdvancedLevel.getLabelResId(AdvancedLevel.NONE), getOnSurfaceColor().copy(alpha = 0.1f), getOnSurfaceSecondaryColor())
    }

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(textRes),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun GoalSection(goal: String) {
    val goalResId = Goals.getLabelResId(goal)

    Surface(
        color = getPrimaryColor().copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(goalResId),
            fontSize = 14.sp,
            color = getPrimaryColor(),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StatsSection(
    trainingPlan: TrainingPlan
) {
    val totalDays = trainingPlan.days.size
    val restDays = trainingPlan.days.count { it.isRestDay }
    val trainingDays = totalDays - restDays
    val totalExercises = trainingPlan.days.sumOf { day ->
        if (!day.isRestDay) day.exercises.size else 0
    }
    val totalDuration = trainingPlan.days.sumOf { day ->
        day.totalDuration ?: 0
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            label = stringResource(R.string.stats_days),
            value = totalDays.toString(),
            subtitle = stringResource(R.string.stats_days_training, trainingDays)
        )
        StatItem(
            label = stringResource(R.string.stats_exercises),
            value = totalExercises.toString(),
            subtitle = stringResource(R.string.stats_exercises_total)
        )
        StatItem(
            label = stringResource(R.string.stats_time),
            value = if (totalDuration > 0) formatDuration(totalDuration) else "-",
            subtitle = stringResource(R.string.stats_time_estimated)
        )
        StatItem(
            label = stringResource(R.string.stats_rest),
            value = restDays.toString(),
            subtitle = stringResource(R.string.stats_rest_days)
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    subtitle: String? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = getPrimaryColor()
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = getOnSurfaceColor(),
            fontWeight = FontWeight.Medium
        )
        subtitle?.let {
            Text(
                text = it,
                fontSize = 10.sp,
                color = getOnSurfaceSecondaryColor()
            )
        }
    }
}

@Composable
private fun HashtagsSection(hashtags: List<String>) {
    Text(
        text = stringResource(R.string.plan_detail_hashtags_label),
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = getOnSurfaceColor()
    )
    Spacer(modifier = Modifier.height(8.dp))

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        hashtags.forEach { hashtag ->
            HashtagChip(hashtag = hashtag)
        }
    }
}

@Composable
private fun HashtagChip(hashtag: String) {
    Surface(
        color = getPrimaryColor().copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, getPrimaryColor().copy(alpha = 0.3f))
    ) {
        Text(
            text = "#$hashtag",
            fontSize = 12.sp,
            color = getPrimaryColor(),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ActionButtonsSection(
    trainingPlan: TrainingPlan,
    likes: Int,
    isLiked: Boolean,
    isUserLoggedIn: Boolean,
    onLikeClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDownloadPdf: () -> Unit,
    isDownloadingPdf: Boolean,
    onShowLoginDialog: () -> Unit
) {
    val context = LocalContext.current

    val favoriteIds by GlobalFavoriteState.getInstance(context).favoriteTrainingPlanIds.collectAsState()
    val isFavoriteGlobal = favoriteIds.contains(trainingPlan.id)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = {
                if (isUserLoggedIn) {
                    onLikeClick()
                } else {
                    onShowLoginDialog()
                }
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = getSurfaceColor(),
                contentColor = getOnSurfaceColor()
            ),
            border = BorderStroke(1.dp, getInputBorderColor())
        ) {
            Icon(
                imageVector = if (isUserLoggedIn && isLiked) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                contentDescription = null,
                tint = getPrimaryColor(),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("$likes")
        }

        Button(
            onClick = {
                if (isUserLoggedIn) {
                    onFavoriteClick()
                } else {
                    onShowLoginDialog()
                }
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = getSurfaceColor(),
                contentColor = getOnSurfaceColor()
            ),
            border = BorderStroke(1.dp, getInputBorderColor())
        ) {
            Icon(
                imageVector = if (isFavoriteGlobal) {
                    Icons.Default.Favorite
                } else {
                    Icons.Default.FavoriteBorder
                },
                contentDescription = null,
                tint = if (isFavoriteGlobal) {
                    Color.Red
                } else {
                    getOnSurfaceSecondaryColor()
                },
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isFavoriteGlobal) stringResource(R.string.plan_detail_remove_favorite) else stringResource(R.string.plan_detail_save_favorite)
            )
        }

        Button(
            onClick = {
                if (isUserLoggedIn) {
                    onDownloadPdf()
                } else {
                    onShowLoginDialog()
                }
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = getPrimaryColor()
            ),
            enabled = !isDownloadingPdf
        ) {
            if (isDownloadingPdf) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Text(stringResource(R.string.plan_detail_downloading), fontSize = 10.sp)
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Download,
                    tint = getOnPrimaryColor(),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.plan_detail_download_pdf),
                    color = getOnPrimaryColor()
                )
            }
        }
    }
}

@Composable
private fun TrainingDayDetailCard(
    day: TrainingDay,
    onExerciseClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = day.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = getOnSurfaceColor()
                )

                if (day.isRestDay) {
                    Surface(
                        color = getOnSurfaceColor().copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.day_rest_label),
                            fontSize = 10.sp,
                            color = getOnSurfaceColor(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (!day.isRestDay && day.exercises.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                day.exercises.forEachIndexed { index, exercise ->
                    ExerciseItem(
                        exercise = exercise,
                        onExerciseClick = onExerciseClick
                    )

                    if (index < day.exercises.lastIndex) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.day_estimated_time, day.totalDuration?.let { formatDuration(it) } ?: "?"),
                    fontSize = 12.sp,
                    color = getOnSurfaceSecondaryColor()
                )
            }
        }
    }
}

@Composable
private fun ExerciseItem(
    exercise: DayExercise,
    onExerciseClick: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExerciseClick(exercise.originalId!!) },
        color = getOnSurfaceColor().copy(alpha = 0.05f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = getOnSurfaceColor().copy(alpha = 0.1f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = exercise.image,
                            contentDescription = exercise.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = exercise.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = getOnSurfaceColor(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.clickable {
                            isExpanded = !isExpanded
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = getPrimaryColor(),
                            modifier = Modifier.size(14.dp)
                        )

                        val details = if (exercise.isRepsSet) {
                            stringResource(R.string.exercise_reps_sets, exercise.sets.size, exercise.sets.firstOrNull()?.repetitions ?: 0)
                        } else {
                            stringResource(R.string.exercise_time_sets, formatDuration(exercise.duration ?: 0), exercise.sets.size)
                        }

                        Text(
                            text = details,
                            fontSize = 14.sp,
                            color = getOnSurfaceColor(),
                            fontWeight = FontWeight.Medium
                        )

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) stringResource(R.string.comments_see_less) else stringResource(R.string.comments_see_all),
                            tint = getOnSurfaceSecondaryColor(),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = null,
                            tint = getOnSurfaceSecondaryColor(),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = stringResource(R.string.exercise_rest_time, formatDuration(exercise.restAfterSet)),
                            fontSize = 12.sp,
                            color = getOnSurfaceSecondaryColor()
                        )
                    }

                    if (exercise.muscleGroup.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(exercise.muscleGroup.take(2)) { muscleGroup ->
                                Surface(
                                    color = getPrimaryColor().copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = muscleGroup.name,
                                        fontSize = 10.sp,
                                        color = getPrimaryColor(),
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Surface(
                    modifier = Modifier.size(32.dp),
                    color = getPrimaryColor().copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = stringResource(R.string.exercise_view_details),
                            tint = getPrimaryColor(),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    exercise.sets.forEachIndexed { index, set ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            color = getOnSurfaceColor().copy(alpha = 0.03f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.exercise_set_title, index + 1),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = getOnSurfaceColor()
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (exercise.isRepsSet) {
                                        set.repetitions?.let { reps ->
                                            Text(
                                                text = stringResource(R.string.exercise_set_reps, reps),
                                                fontSize = 12.sp,
                                                color = getOnSurfaceSecondaryColor()
                                            )
                                        }

                                        set.weight?.let { weight ->
                                            Text(
                                                text = stringResource(R.string.exercise_set_weight, weight),
                                                fontSize = 12.sp,
                                                color = getPrimaryColor(),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    } else {
                                        set.duration?.let { duration ->
                                            Text(
                                                text = formatDuration(duration),
                                                fontSize = 12.sp,
                                                color = getOnSurfaceSecondaryColor()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StartTrainingButton(
    isStarting: Boolean,
    isUserLoggedIn: Boolean,
    onClick: () -> Unit,
    onShowLoginDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            if (isUserLoggedIn) {
                onClick()
            } else {
                onShowLoginDialog()
            }
        },
        modifier = modifier
            .height(52.dp),
        enabled = !isStarting,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = getPrimaryColor(),
            contentColor = getOnPrimaryColor(),
            disabledContainerColor = getPrimaryColor().copy(alpha = 0.5f)
        )
    ) {
        if (isStarting) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(24.dp),
                color = getOnPrimaryColor(),
                strokeWidth = 2.dp
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.plan_detail_start_training),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun TrainingPlanDetailContent(
    plan: TrainingPlan,
    viewModel: TrainingPlanDetailViewModel,
    reportViewModel: ReportViewModel,
    isUserLoggedIn: Boolean,
    onShowLoginDialog: () -> Unit,
    onExerciseClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onBackClick: () -> Unit,
    isDownloadingPdf: Boolean,
    onReportClick: () -> Unit,
    onEditPlanClick: (String) -> Unit,
    onDeletePlanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val comments by viewModel.trainingPlanComments.collectAsState()
    val hasMore by viewModel.hasMoreComments.collectAsState()
    val isLoading by viewModel.isLoadingComments.collectAsState()
    val isLiked by viewModel.isTrainingPlanLiked(plan.id).collectAsState(initial = false)
    val currentUser by viewModel.currentUser.collectAsState()
    val currentUsername = if (isUserLoggedIn) currentUser?.username.orEmpty() else ""
    val uiState by viewModel.uiState.collectAsState()

    var newCommentText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<String?>(null) }
    var replyingToUsername by remember { mutableStateOf<String?>(null) }
    var expandedReplies by remember { mutableStateOf<Set<String>>(emptySet()) }

    var editingComment by remember { mutableStateOf<Comment?>(null) }
    var editingContent by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Comment?>(null) }

    var showReportCommentDialog by remember { mutableStateOf(false) }
    var commentToReport by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(plan.id) {
        viewModel.loadTrainingPlanComments(plan.id, refresh = true)
        if (isUserLoggedIn) {
            viewModel.fetchUserLikes()
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                AsyncImage(
                    model = plan.image,
                    contentDescription = plan.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                ),
                                startY = 0f,
                                endY = 900f
                            )
                        )
                )

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            RoundedCornerShape(24.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.action_back),
                        tint = Color.White
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isUserLoggedIn && viewModel.isCurrentUserTrainingPlan(plan.author?.username ?: "")) {
                        IconButton(
                            onClick = {
                                onEditPlanClick(plan.id)
                            },
                            modifier = Modifier
                                .background(
                                    Color.Black.copy(alpha = 0.5f),
                                    RoundedCornerShape(24.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.plan_detail_edit),
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = {
                                onDeletePlanClick()
                            },
                            modifier = Modifier
                                .background(
                                    Color.Black.copy(alpha = 0.5f),
                                    RoundedCornerShape(24.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.plan_detail_delete),
                                tint = Color.White
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                if (!isUserLoggedIn) {
                                    onShowLoginDialog()
                                } else {
                                    onReportClick()
                                }
                            },
                            modifier = Modifier
                                .background(
                                    Color.Black.copy(alpha = 0.5f),
                                    RoundedCornerShape(24.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = stringResource(R.string.plan_detail_report),
                                tint = Color.White
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = plan.name,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onUserClick(plan.author?.username ?: "?")
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                UserAvatar(
                                    imageUrl = plan.author?.profileDetails?.profilePicture,
                                    username = plan.author?.username ?: "?",
                                    modifier = Modifier.size(40.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = plan.author?.username ?: "?",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = stringResource(R.string.plan_detail_added_date, plan.createdAt.formatToRelativeTime()),
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }

                        DifficultyBadgeTrainingPlan(
                            difficulty = plan.advancedLevel,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    GoalSection(goal = plan.goal)

                    Spacer(modifier = Modifier.height(16.dp))

                    StatsSection(
                        trainingPlan = plan
                    )

                    if (!plan.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.plan_detail_description_label),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = getOnSurfaceColor()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = plan.description,
                            fontSize = 14.sp,
                            color = getOnSurfaceColor(),
                            lineHeight = 20.sp
                        )
                    }

                    if (plan.hashtags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HashtagsSection(hashtags = plan.hashtags)
                    }
                }
            }
        }

        item {
            ActionButtonsSection(
                likes = plan.likes,
                isLiked = isLiked,
                isUserLoggedIn = isUserLoggedIn,
                onLikeClick = viewModel::toggleLike,
                onFavoriteClick = viewModel::toggleFavorite,
                onDownloadPdf = viewModel::downloadPdf,
                isDownloadingPdf = isDownloadingPdf,
                trainingPlan = plan,
                onShowLoginDialog = onShowLoginDialog
            )
        }

        item {
            StartTrainingButton(
                isStarting = uiState.isStartingPlan,
                isUserLoggedIn = isUserLoggedIn,
                onClick = { viewModel.startTrainingPlan() },
                onShowLoginDialog = onShowLoginDialog,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
            )
        }

        item {
            Text(
                text = stringResource(R.string.plan_detail_days_count, plan.days.size),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = getOnSurfaceColor(),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        items(
            items = plan.days,
            key = { it.dayNumber }
        ) { day ->
            TrainingDayDetailCard(
                day = day,
                onExerciseClick = onExerciseClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        item {
            HorizontalDivider(
                color = getOnSurfaceSecondaryColor().copy(alpha = 0.3f),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = stringResource(R.string.post_comment_icon_desc),
                    tint = getPrimaryColor(),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = stringResource(R.string.plan_comments_header, comments.size),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = getOnSurfaceColor()
                )
            }
        }

        item {
            if (isUserLoggedIn && currentUsername.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (replyingTo != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                stringResource(R.string.comments_replying_to_format, replyingToUsername.orEmpty()),
                                fontSize = 14.sp,
                                color = getOnSurfaceSecondaryColor()
                            )
                            IconButton(
                                onClick = {
                                    replyingTo = null
                                    replyingToUsername = null
                                },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(R.string.action_cancel),
                                    modifier = Modifier.size(18.dp),
                                    tint = getOnSurfaceColor()
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newCommentText,
                            onValueChange = { newCommentText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = {
                                Text(
                                    if (replyingTo != null) stringResource(R.string.comments_reply_placeholder)
                                    else stringResource(R.string.comments_input_placeholder)
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = getPrimaryColor(),
                                unfocusedBorderColor = getOnSurfaceSecondaryColor(),
                                cursorColor = getPrimaryColor()
                            )
                        )

                        IconButton(
                            onClick = {
                                if (newCommentText.isNotBlank()) {
                                    scope.launch {
                                        if (replyingTo != null) {
                                            viewModel.addTrainingPlanReply(
                                                replyingTo!!,
                                                newCommentText.trim()
                                            )
                                            viewModel.loadRepliesForTrainingPlanComment(replyingTo!!)
                                        } else {
                                            viewModel.addTrainingPlanComment(
                                                plan.id,
                                                newCommentText.trim()
                                            )
                                        }
                                        newCommentText = ""
                                        replyingTo = null
                                        replyingToUsername = null
                                    }
                                }
                            },
                            enabled = newCommentText.isNotBlank()
                        ) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = stringResource(R.string.action_send),
                                tint = if (newCommentText.isNotBlank())
                                    getPrimaryColor()
                                else
                                    getOnSurfaceSecondaryColor()
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(
                            color = getSecondaryColor().copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = stringResource(R.string.auth_login_title),
                        modifier = Modifier.size(32.dp),
                        tint = getOnSurfaceSecondaryColor()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.comments_login_prompt_title),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (comments.isEmpty() && !isLoading) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        tint = getOnSurfaceSecondaryColor(),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.plan_comments_empty_title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.plan_comments_empty_subtitle),
                        fontSize = 14.sp,
                        color = getOnSurfaceSecondaryColor(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        items(comments, key = { it._id }) { comment ->
            TrainingPlanCommentItem(
                comment = comment,
                viewModel = viewModel,
                isUserLoggedIn = isUserLoggedIn,
                onReply = { commentId, username ->
                    if (isUserLoggedIn) {
                        replyingTo = commentId
                        replyingToUsername = username
                    } else {
                        onShowLoginDialog()
                    }
                },
                isExpanded = expandedReplies.contains(comment._id),
                onToggleReplies = {
                    val wasExpanded = expandedReplies.contains(comment._id)
                    expandedReplies = if (wasExpanded) {
                        expandedReplies - comment._id
                    } else {
                        expandedReplies + comment._id
                    }
                    if (!wasExpanded) {
                        viewModel.loadRepliesForTrainingPlanComment(comment._id)
                    }
                },
                currentUsername = currentUsername,
                onEditClick = { commentToEdit ->
                    editingComment = commentToEdit
                    editingContent = commentToEdit.content
                    showEditDialog = true
                },
                onDeleteClick = { commentToDelete ->
                    showDeleteDialog = commentToDelete
                },
                onShowLoginDialog = onShowLoginDialog,
                onReportClick = { commentId ->
                    commentToReport = commentId
                    showReportCommentDialog = true
                },
                onUserClick = { username ->
                    onUserClick(username)
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (expandedReplies.contains(comment._id)) {
                val replies by viewModel.getRepliesForTrainingPlanComment(comment._id).collectAsState()
                replies.forEach { reply ->
                    TrainingPlanReplyItem(
                        reply = reply,
                        viewModel = viewModel,
                        isUserLoggedIn = isUserLoggedIn,
                        currentUsername = currentUsername,
                        onEditClick = { replyToEdit ->
                            editingComment = replyToEdit
                            editingContent = replyToEdit.content
                            showEditDialog = true
                        },
                        onDeleteClick = { replyToDelete ->
                            showDeleteDialog = replyToDelete
                        },
                        onShowLoginDialog = onShowLoginDialog,
                        onReportClick = { replyId ->
                            commentToReport = replyId
                            showReportCommentDialog = true
                        },
                        onUserClick = { username ->
                            onUserClick(username)
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        if (hasMore && !isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = {
                            viewModel.loadMoreTrainingPlanComments(plan.id)
                        }
                    ) {
                        Text(stringResource(R.string.comments_see_all), color = getPrimaryColor())
                    }
                }
            }
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = getPrimaryColor())
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showDeleteDialog != null) {
        DeleteCommentDialog(
            onConfirm = {
                scope.launch {
                    viewModel.deleteTrainingPlanComment(showDeleteDialog!!) { success, _ ->
                        if (success) {
                            showDeleteDialog = null
                            viewModel.loadTrainingPlanComments(plan.id, refresh = true)
                        }
                    }
                }
            },
            onDismiss = { showDeleteDialog = null }
        )
    }

    if (showEditDialog && editingComment != null) {
        EditCommentDialog(
            editingContent = editingContent,
            onContentChange = { editingContent = it },
            editMessage = "",
            isReply = editingComment?.parentId != null,
            onConfirm = {
                scope.launch {
                    viewModel.editTrainingPlanComment(
                        editingComment!!,
                        editingContent.trim()
                    ) { success, _ ->
                        if (success) {
                            showEditDialog = false
                            editingComment = null
                            editingContent = ""
                            viewModel.loadTrainingPlanComments(plan.id, refresh = true)
                        }
                    }
                }
            },
            onDismiss = {
                showEditDialog = false
                editingComment = null
                editingContent = ""
            },
            canSave = editingContent.trim().isNotBlank() &&
                    editingContent.trim() != editingComment?.content
        )
    }

    if (showReportCommentDialog && commentToReport != null) {
        ReportDialog(
            itemId = commentToReport!!,
            itemType = ReportItemType.COMMENT,
            onDismiss = {
                showReportCommentDialog = false
                commentToReport = null
                reportViewModel.resetState()
            },
            viewModel = reportViewModel
        )
    }
}

@Composable
private fun TrainingPlanCommentItem(
    comment: Comment,
    viewModel: TrainingPlanDetailViewModel,
    isUserLoggedIn: Boolean = true,
    onReply: (String, String) -> Unit,
    isExpanded: Boolean,
    onToggleReplies: () -> Unit,
    currentUsername: String,
    onEditClick: (Comment) -> Unit = {},
    onDeleteClick: (Comment) -> Unit = {},
    onShowLoginDialog: () -> Unit = {},
    onReportClick: (String) -> Unit = {},
    onUserClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    val isLiked by viewModel.isTrainingPlanCommentLiked(comment._id).collectAsState(initial = false)

    val maxChars = 250
    var expanded by remember { mutableStateOf(false) }
    val cleanedLength = comment.content.replace(Regex("\\s+"), " ").trim().length
    val shouldShowSeeAll = cleanedLength > maxChars

    val displayText = if (!expanded && shouldShowSeeAll) {
        comment.content.trim().take(maxChars) + "..."
    } else {
        comment.content.trim()
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserAvatar(
                imageUrl = comment.author.profileDetails?.profilePicture,
                username = comment.author.username,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onUserClick(comment.author.username) }
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = comment.author.username,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = getOnSurfaceColor()
                    )

                    if (isUserLoggedIn) {
                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = stringResource(R.string.action_options),
                                    modifier = Modifier.size(18.dp),
                                    tint = getOnSurfaceColor()
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                if (comment.author.username == currentUsername) {
                                    DropdownMenuItem(
                                        onClick = {
                                            showMenu = false
                                            onEditClick(comment)
                                        },
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp),
                                                    tint = getOnSurfaceColor()
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(stringResource(R.string.comments_edit_option), color = getOnSurfaceColor())
                                            }
                                        }
                                    )

                                    HorizontalDivider(
                                        color = getOnSurfaceColor().copy(alpha = 0.3f),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(horizontal = 12.dp)
                                    )

                                    DropdownMenuItem(
                                        onClick = {
                                            showMenu = false
                                            onDeleteClick(comment)
                                        },
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp),
                                                    tint = getErrorColor()
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(stringResource(R.string.comments_delete_option), color = getErrorColor())
                                            }
                                        }
                                    )
                                } else {
                                    DropdownMenuItem(
                                        onClick = {
                                            showMenu = false
                                            onReportClick(comment._id)
                                        },
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Flag,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp),
                                                    tint = getOnSurfaceColor()
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(stringResource(R.string.comments_report_option), color = getOnSurfaceColor())
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = displayText,
                    fontSize = 14.sp,
                    color = getOnSurfaceColor()
                )

                if (shouldShowSeeAll) {
                    TextButton(
                        onClick = { expanded = !expanded },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            if (expanded) stringResource(R.string.comments_see_less) else stringResource(R.string.comments_see_all),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = getOnSurfaceSecondaryColor()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.clickable {
                            if (isUserLoggedIn) {
                                viewModel.toggleTrainingPlanCommentLike(comment._id) { success, message ->
                                    if (success) {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                onShowLoginDialog()
                            }
                        }
                    ) {
                        Text(
                            text = " ${comment.timeAgo()}",
                            color = getOnSurfaceSecondaryColor(),
                            fontSize = 11.sp
                        )
                        if (comment.isEdited == true) {
                            Text(
                                text = stringResource(R.string.comments_edited_suffix),
                                color = getOnSurfaceSecondaryColor(),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        Spacer(Modifier.padding(2.dp))
                        Icon(
                            if (isUserLoggedIn && isLiked) Icons.Filled.ThumbUpAlt else Icons.Default.ThumbUpOffAlt,
                            contentDescription = "Polub",
                            tint = if (isUserLoggedIn && isLiked) getPrimaryColor() else getOnSurfaceColor(),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${comment.likes ?: 0}",
                            fontSize = 12.sp,
                            color = getOnSurfaceColor()
                        )
                    }

                    TextButton(
                        onClick = { onReply(comment._id, comment.author.username) },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            stringResource(R.string.comments_reply_action),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = getOnSurfaceColor()
                        )
                    }

                    if (comment.repliesCount != null && comment.repliesCount > 0) {
                        TextButton(
                            onClick = onToggleReplies,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                if (isExpanded) stringResource(R.string.comments_hide_replies)
                                else stringResource(R.string.comments_show_replies_format, comment.repliesCount),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = getOnSurfaceColor()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrainingPlanReplyItem(
    reply: Comment,
    viewModel: TrainingPlanDetailViewModel,
    isUserLoggedIn: Boolean = true,
    currentUsername: String,
    onEditClick: (Comment) -> Unit = {},
    onDeleteClick: (Comment) -> Unit = {},
    onShowLoginDialog: () -> Unit = {},
    onReportClick: (String) -> Unit = {},
    onUserClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    val isLiked by viewModel.isTrainingPlanCommentLiked(reply._id).collectAsState(initial = false)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 52.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        UserAvatar(
            imageUrl = reply.author.profileDetails?.profilePicture,
            username = reply.author.username,
            modifier = Modifier
                .size(40.dp)
                .clickable { onUserClick(reply.author.username) }
        )

        Column(modifier = modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reply.author.username,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = getOnSurfaceColor()
                )

                if (isUserLoggedIn) {
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.action_options),
                                modifier = Modifier.size(16.dp),
                                tint = getOnSurfaceColor()
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            if (reply.author.username == currentUsername) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.comments_edit_reply_option), color = getOnSurfaceColor()) },
                                    onClick = {
                                        showMenu = false
                                        onEditClick(reply)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = null,
                                            tint = getOnSurfaceColor()
                                        )
                                    }
                                )

                                HorizontalDivider(
                                    color = getOnSurfaceColor().copy(alpha = 0.3f),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )

                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.comments_delete_reply_option), color = getErrorColor()) },
                                    onClick = {
                                        showMenu = false
                                        onDeleteClick(reply)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = getErrorColor()
                                        )
                                    }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.comments_report_option), color = getOnSurfaceColor()) },
                                    onClick = {
                                        showMenu = false
                                        onReportClick(reply._id)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Flag,
                                            contentDescription = null,
                                            tint = getOnSurfaceColor()
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = reply.content,
                fontSize = 14.sp,
                color = getOnSurfaceColor()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.clickable {
                    if (isUserLoggedIn) {
                        viewModel.toggleTrainingPlanCommentLike(reply._id) { success, message ->
                            if (success) {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        onShowLoginDialog()
                    }
                }
            ) {
                Text(
                    text = reply.timeAgo(),
                    color = getOnSurfaceSecondaryColor(),
                    fontSize = 12.sp
                )
                if (reply.isEdited == true) {
                    Text(
                        text = stringResource(R.string.comments_edited_suffix),
                        color = getOnSurfaceSecondaryColor(),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(Modifier.padding(3.dp))
                Icon(
                    if (isUserLoggedIn && isLiked) Icons.Filled.ThumbUpAlt else Icons.Default.ThumbUpOffAlt,
                    contentDescription = "Polub",
                    tint = if (isUserLoggedIn && isLiked) getPrimaryColor() else getOnSurfaceColor(),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${reply.likes ?: 0}",
                    fontSize = 11.sp,
                    color = getOnSurfaceColor()
                )
            }
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val hours = minutes / 60
    val remainingMinutes = minutes % 60

    return when {
        hours == 0 -> "${minutes}min"
        remainingMinutes == 0 -> "${hours}h"
        else -> "${hours}h ${remainingMinutes}min"
    }
}