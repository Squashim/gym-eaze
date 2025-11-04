package com.example.gymeaze.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.data.models.DayExerciseFormData
import com.example.gymeaze.data.models.TrainingDayFormData
import com.example.gymeaze.ui.theme.getInputBorderColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor

@Composable
fun TrainingDayCard(
    day: TrainingDayFormData,
    dayIndex: Int,
    onEditName: (String) -> Unit,
    onToggleRestDay: () -> Unit,
    onAddExercise: () -> Unit,
    onRemoveExercise: (Int) -> Unit,
    onRemoveDay: () -> Unit,
    onEditExercise: (Int) -> Unit
) {
    var showNameDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dzień $dayIndex",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = getPrimaryColor()
                    )

                    if (day.isRestDay) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = getOnSurfaceColor().copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {

                            Text(
                                text = "Odpoczynek",
                                fontSize = 10.sp,
                                color = getOnSurfaceColor(),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { showDeleteConfirmation = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Usuń dzień",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (day.name.isNotEmpty()) day.name else "Nazwa dnia",
                    fontSize = 16.sp,
                    color = if (day.name.isNotEmpty()) getOnSurfaceColor() else getOnSurfaceSecondaryColor(),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showNameDialog = true }
                )

                IconButton(
                    onClick = { showNameDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edytuj nazwę",
                        tint = getOnSurfaceSecondaryColor(),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dzień odpoczynku",
                    fontSize = 14.sp,
                    color = getOnSurfaceColor()
                )

                Switch(
                    checked = day.isRestDay,
                    onCheckedChange = { onToggleRestDay() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = getPrimaryColor(),
                        checkedTrackColor = getPrimaryColor().copy(alpha = 0.5f)
                    )
                )
            }

            if (!day.isRestDay) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ćwiczenia (${day.exercises.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor()
                    )

                    IconButton(
                        onClick = onAddExercise
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Dodaj ćwiczenie",
                            tint = getPrimaryColor()
                        )
                    }
                }

                if (day.exercises.isNotEmpty()) {
                    day.exercises.forEachIndexed { exerciseIndex, exercise ->
                        ExerciseInDayItem(
                            exercise = exercise,
                            onEdit = { onEditExercise(exerciseIndex) },
                            onRemove = { onRemoveExercise(exerciseIndex) }
                        )

                        if (exerciseIndex < day.exercises.lastIndex) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAddExercise() },
                        color = getOnSurfaceColor().copy(alpha = 0.05f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = getOnSurfaceSecondaryColor(),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Dodaj pierwsze ćwiczenie",
                                color = getOnSurfaceSecondaryColor(),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                if (day.exercises.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    val totalDuration = calculateDayDuration(day)
                    Text(
                        text = "Szacowany czas: ${formatDuration(totalDuration)}",
                        fontSize = 12.sp,
                        color = getOnSurfaceSecondaryColor(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    if (showNameDialog) {
        DayNameEditDialog(
            currentName = day.name,
            onNameChanged = { newName ->
                onEditName(newName)
                showNameDialog = false
            },
            onDismiss = { showNameDialog = false }
        )
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Usuń dzień") },
            text = {
                Text("Czy na pewno chcesz usunąć dzień $dayIndex? Ta operacja nie może zostać cofnięta.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRemoveDay()
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Usuń", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Anuluj")
                }
            }
        )
    }
}

@Composable
private fun ExerciseInDayItem(
    exercise: DayExerciseFormData,
    onEdit: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = getOnSurfaceColor().copy(alpha = 0.05f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = exercise.exercise?.name ?: "Nieznane ćwiczenie",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = getOnSurfaceColor()
                )

                val details = if (exercise.isRepsSet) {
                    "${exercise.sets.size} serie × ${exercise.sets.firstOrNull()?.repetitions ?: 0} powtórzeń"
                } else {
                    "${formatDuration(exercise.duration ?: 0)} × ${exercise.sets.size} serie"
                }

                Text(
                    text = details,
                    fontSize = 12.sp,
                    color = getOnSurfaceSecondaryColor()
                )

                Text(
                    text = "Odpoczynek: ${formatDuration(exercise.restAfterSet)}",
                    fontSize = 10.sp,
                    color = getOnSurfaceSecondaryColor()
                )
            }

            Row {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edytuj ćwiczenie",
                        tint = getOnSurfaceSecondaryColor(),
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Usuń ćwiczenie",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DayNameEditDialog(
    currentName: String,
    onNameChanged: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nazwa dnia") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= 100) name = it },
                label = { Text("Nazwa") },
                placeholder = { Text("np. Klatka piersiowa + triceps") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = getPrimaryColor(),
                    unfocusedBorderColor = getInputBorderColor()
                ),
                supportingText = { Text("${name.length}/100") }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onNameChanged(name.trim()) },
                enabled = name.trim().isNotEmpty()
            ) {
                Text("Zapisz", color = getPrimaryColor())
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}

private fun calculateDayDuration(day: TrainingDayFormData): Int {
    if (day.isRestDay) return 0

    return day.exercises.sumOf { exercise ->
        val exerciseTime = if (exercise.isRepsSet) {
            exercise.sets.sumOf { it.repetitions * 3 }
        } else {
            (exercise.duration ?: 0) * exercise.sets.size
        }

        val restTime = exercise.restAfterSet * exercise.sets.size
        exerciseTime + restTime
    }
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