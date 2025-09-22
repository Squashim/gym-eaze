package com.example.gymeaze.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.constants.Goals
import com.example.gymeaze.data.models.DayExercise
import com.example.gymeaze.data.models.TrainingDay
import com.example.gymeaze.data.models.TrainingPlan
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor
import com.example.gymeaze.viewmodel.TrainingPlanDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingPlanDetailScreen(
    trainingPlanId: String,
    onBackClick: () -> Unit,
    viewModel: TrainingPlanDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(trainingPlanId) {
        viewModel.loadTrainingPlan(trainingPlanId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.trainingPlan?.name ?: "Plan treningowy",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor(),
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wstecz",
                            tint = getOnSurfaceColor()
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = getBackgroundColor()
                )
            )
        },
        containerColor = getBackgroundColor()
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = getPrimaryColor())
                }
            }
            uiState.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.error ?: "Wystąpił błąd",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.loadTrainingPlan(trainingPlanId) }) {
                        Text("Spróbuj ponownie")
                    }
                }
            }
            uiState.trainingPlan != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        TrainingPlanHeader(uiState.trainingPlan!!)
                    }

                    items(uiState.trainingPlan!!.days) { day ->
                        TrainingDayDetailCard(day)
                    }
                }
            }
        }
    }
}

@Composable
private fun TrainingPlanHeader(trainingPlan: TrainingPlan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AsyncImage(
                model = trainingPlan.image,
                contentDescription = trainingPlan.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = trainingPlan.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = getOnSurfaceColor()
            )

            if (trainingPlan.description != null) {
                Text(
                    text = trainingPlan.description,
                    fontSize = 14.sp,
                    color = getOnSurfaceColor(),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoChip(
                    label = "Cel",
                    value = getGoalDisplayName(trainingPlan.goal)
                )
                InfoChip(
                    label = "Poziom",
                    value = getDifficultyDisplayName(trainingPlan.advancedLevel)
                )
                InfoChip(
                    label = "Dni",
                    value = trainingPlan.days.size.toString()
                )
            }
        }
    }
}

@Composable
private fun TrainingDayDetailCard(day: TrainingDay) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    fontWeight = FontWeight.Medium,
                    color = getOnSurfaceColor()
                )

                if (day.isRestDay) {
                    Surface(
                        color = getOnSurfaceColor().copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Odpoczynek",
                            fontSize = 12.sp,
                            color = getOnSurfaceColor(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (!day.isRestDay && day.exercises.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                day.exercises.forEach { exercise ->
                    ExerciseInPlanItem(exercise)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                day.totalDuration?.let { duration ->
                    Text(
                        text = "Szacowany czas: ${formatDuration(duration)}",
                        fontSize = 12.sp,
                        color = getOnSurfaceSecondaryColor(),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseInPlanItem(dayExercise: DayExercise) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = getOnSurfaceColor().copy(alpha = 0.05f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = dayExercise.exercise?.name ?: "Ćwiczenie",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = getOnSurfaceColor()
                )

                val details = if (dayExercise.isRepsSet) {
                    "${dayExercise.sets.size} serie × ${dayExercise.sets.firstOrNull()?.repetitions ?: 0} powtórzeń"
                } else {
                    "${formatDuration(dayExercise.duration ?: 0)} × ${dayExercise.sets.size} serie"
                }

                Text(
                    text = details,
                    fontSize = 12.sp,
                    color = getOnSurfaceSecondaryColor()
                )

                Text(
                    text = "Odpoczynek: ${formatDuration(dayExercise.restAfterSet)}",
                    fontSize = 10.sp,
                    color = getOnSurfaceSecondaryColor()
                )
            }
        }
    }
}

@Composable
private fun InfoChip(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = getOnSurfaceSecondaryColor()
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = getOnSurfaceColor()
        )
    }
}

private fun getGoalDisplayName(goal: String): String = when (goal) {
    Goals.NONE -> "Nie wybrano"
    Goals.RECOVERY -> "Regeneracja i odpoczynek"
    Goals.STRENGTH -> "Zwiększanie siły"
    Goals.POSTURE -> "Poprawa postawy ciała"
    Goals.MASS_GAIN -> "Zwiększenie masy ciała"
    Goals.COORDINATION -> "Poprawa koordynacji"
    Goals.CALORIE_BURN -> "Spalanie kalorii"
    Goals.MOBILITY -> "Poprawa mobilności"
    Goals.LEGS_GLUTES -> "Rozwój nóg i pośladków"
    Goals.ENDURANCE -> "Poprawa wytrzymałości"
    Goals.FAT_LOSS -> "Redukcja tkanki tłuszczowej"
    Goals.MUSCLE_GAIN -> "Budowanie masy mięśniowej"
    Goals.FLEXIBILITY -> "Poprawa gibkości"
    else -> "Nie wybrano"
}

private fun getDifficultyDisplayName(difficulty: String): String = when (difficulty) {
    AdvancedLevel.NONE -> "Nie wybrano"
    AdvancedLevel.EASY -> "Początkujący"
    AdvancedLevel.MEDIUM -> "Średniozaawansowany"
    AdvancedLevel.HARD -> "Zaawansowany"
    else -> "Nie wybrano"
}

private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    return when {
        minutes == 0 -> "${remainingSeconds}s"
        minutes < 60 -> "${minutes}min ${remainingSeconds}s"
        else -> {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            "${hours}h ${remainingMinutes}min"
        }
    }.replace(" 0s", "").replace(" 0min", "")
}