package com.example.gymeaze.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.constants.Goals
import com.example.gymeaze.data.models.TrainingDay
import com.example.gymeaze.data.models.DayExercise
import com.example.gymeaze.data.models.TrainingPlan
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.util.formatToRelativeTime
import com.example.gymeaze.viewmodel.SnackbarType
import com.example.gymeaze.viewmodel.TrainingPlanDetailViewModel
import com.example.gymeaze.ui.components.user.UserAvatar
import com.example.gymeaze.util.GlobalFavoriteState
import com.example.gymeaze.util.formatToRelativeTime
import okhttp3.internal.concurrent.formatDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingPlanDetailScreen(
    planId: String,
    onBackClick: () -> Unit,
    onExerciseClick: (String) -> Unit = {},
    viewModel: TrainingPlanDetailViewModel = viewModel(),
    onUserClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val isDownloadingPdf by viewModel.isDownloadingPdf.collectAsState()

    LaunchedEffect(planId) {
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
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = getPrimaryColor()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Ładowanie planu treningowego...",
                                color = getOnSurfaceSecondaryColor(),
                                fontSize = 14.sp
                            )
                        }
                    }
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
                                text = "Błąd ładowania",
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
                                Text("Spróbuj ponownie")
                            }
                        }
                    }
                }

                uiState.trainingPlan != null -> {
                    val plan = uiState.trainingPlan!!

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
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
                                        contentDescription = "Wróć",
                                        tint = Color.White
                                    )
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
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onUserClick(plan.author?.username ?: "?") },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                    UserAvatar(
                                        imageUrl = plan.author?.profileDetails?.profilePicture,
                                        username = plan.author?.username ?: "?",
                                        modifier = Modifier.size(40.dp)
                                    )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { onUserClick(plan.author?.username ?: "?") }
                                        ) {
                                            Text(
                                                text = plan.author?.username ?: "?",
                                                color = getOnSurfaceColor(),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = plan.createdAt.formatToRelativeTime(),
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }

                                DifficultyBadge(
                                    difficulty = plan.advancedLevel,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(16.dp)
                                )
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
                                            text = "Opis",
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
                                onLikeClick = viewModel::toggleLike,
                                onFavoriteClick = viewModel::toggleFavorite,
                                onDownloadPdf = viewModel::downloadPdf,
                                isDownloadingPdf = isDownloadingPdf,
                                trainingPlan = plan
                            )
                        }

                        item {
                            StartTrainingButton(
                                isStarting = uiState.isStartingPlan,
                                onClick = { viewModel.startTrainingPlan() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 8.dp)
                            )
                        }

                        item {
                            Text(
                                text = "Plan treningowy (${plan.days.size} dni)",
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
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DifficultyBadge(
    difficulty: String,
    modifier: Modifier = Modifier
) {
    val (text, backgroundColor, textColor) = when (difficulty) {
        AdvancedLevel.EASY -> Triple("P", getPrimaryColor().copy(alpha = 0.9f), Color.White)
        AdvancedLevel.MEDIUM -> Triple("Ś", getYellowColor(), getOnYellowColor())
        AdvancedLevel.HARD -> Triple("Z", Color.Red.copy(alpha = 0.9f), Color.White)
        else -> Triple("?", Color.Gray.copy(alpha = 0.9f), Color.White)
    }

    Box(
        modifier = modifier
            .size(32.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun GoalSection(goal: String) {
    val goalText = when (goal) {
        Goals.MUSCLE_GAIN -> "Budowanie masy mięśniowej"
        Goals.STRENGTH -> "Zwiększanie siły"
        Goals.ENDURANCE -> "Poprawa wytrzymałości"
        Goals.FAT_LOSS -> "Redukcja tkanki tłuszczowej"
        Goals.CALORIE_BURN -> "Spalanie kalorii"
        Goals.FLEXIBILITY -> "Poprawa gibkości"
        Goals.MOBILITY -> "Poprawa mobilności"
        else -> goal
    }

    Surface(
        color = getPrimaryColor().copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Cel: $goalText",
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
            label = "Dni",
            value = totalDays.toString(),
            subtitle = "$trainingDays treningowych"
        )
        StatItem(
            label = "Ćwiczenia",
            value = totalExercises.toString(),
            subtitle = "łącznie"
        )
        StatItem(
            label = "Czas",
            value = if (totalDuration > 0) formatDuration(totalDuration) else "-",
            subtitle = "szacowany"
        )
        StatItem(
            label = "Odpoczynek",
            value = restDays.toString(),
            subtitle = "dni"
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
        text = "Hashtagi",
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
    onLikeClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDownloadPdf: () -> Unit,
    isDownloadingPdf: Boolean
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
            onClick = onLikeClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = getSurfaceColor(),
                contentColor = getOnSurfaceColor()
            ),
            border = BorderStroke(1.dp, getInputBorderColor())
        ) {
            Icon(
                imageVector = if (likes > 0) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                contentDescription = null,
                tint = getPrimaryColor(),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("$likes")
        }

        Button(
            onClick = {
                onFavoriteClick()
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
                text = if (isFavoriteGlobal) "Usuń" else "Zapisz"
            )
        }

        Button(
            onClick = onDownloadPdf,
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
                    Text("Pobieranie", fontSize = 10.sp)
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
                    text = "PDF",
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
                    text = "${day.name}",
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
                            text = "Odpoczynek",
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
                    text = "Szacowany czas: ${day.totalDuration?.let { formatDuration(it) } ?: "?"}",
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
            .clickable { onExerciseClick(exercise.id) },
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
                        if (exercise.exercise != null && exercise.exercise.image.isNotEmpty()) {
                            AsyncImage(
                                model = exercise.exercise.image,
                                contentDescription = exercise.exercise.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = getOnSurfaceSecondaryColor(),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = exercise.exercise?.name ?: "Ładowanie ćwiczenia...",
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
                            "${exercise.sets.size} serie × ${exercise.sets.firstOrNull()?.repetitions ?: 0} powtórzeń"
                        } else {
                            "${formatDuration(exercise.duration ?: 0)} × ${exercise.sets.size} serie"
                        }

                        Text(
                            text = details,
                            fontSize = 14.sp,
                            color = getOnSurfaceColor(),
                            fontWeight = FontWeight.Medium
                        )

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Zwiń" else "Rozwiń",
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
                            text = "Odpoczynek: ${formatDuration(exercise.restAfterSet)}",
                            fontSize = 12.sp,
                            color = getOnSurfaceSecondaryColor()
                        )
                    }

                    exercise.exercise?.let { ex ->
                        if (ex.muscleGroup.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(ex.muscleGroup.take(2)) { muscleGroup ->
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
                            contentDescription = "Zobacz ćwiczenie",
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
                                    text = "Seria ${index + 1}",
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
                                                text = "$reps powtórzeń",
                                                fontSize = 12.sp,
                                                color = getOnSurfaceSecondaryColor()
                                            )
                                        }

                                        set.weight?.let { weight ->
                                            Text(
                                                text = "${weight}kg",
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
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
                    text = "Rozpocznij plan treningowy",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
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