package com.example.gymeaze.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import com.example.gymeaze.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.constants.Goals
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor

@Composable
fun GoalSelectionDialog(
    selectedGoal: String,
    onGoalSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val goals = listOf(
        Goals.NONE to "Nie wybrano",
        Goals.MUSCLE_GAIN to "Budowanie masy mięśniowej",
        Goals.STRENGTH to "Zwiększanie siły",
        Goals.ENDURANCE to "Poprawa wytrzymałości",
        Goals.FAT_LOSS to "Redukcja tkanki tłuszczowej",
        Goals.CALORIE_BURN to "Spalanie kalorii",
        Goals.MASS_GAIN to "Zwiększenie masy ciała",
        Goals.COORDINATION to "Poprawa koordynacji",
        Goals.MOBILITY to "Poprawa mobilności",
        Goals.FLEXIBILITY to "Poprawa gibkości",
        Goals.LEGS_GLUTES to "Rozwój nóg i pośladków",
        Goals.POSTURE to "Poprawa postawy ciała",
        Goals.RECOVERY to "Regeneracja i odpoczynek"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
            shape = RoundedCornerShape(16.dp)
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
                        text = stringResource(R.string.choose_training_goal),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor()
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
                            tint = getOnSurfaceColor()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(goals) { (value, label) ->
                        GoalOptionItem(
                            label = label,
                            description = getGoalDescription(value),
                            isSelected = selectedGoal == value,
                            onClick = { onGoalSelected(value) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DifficultySelectionDialog(
    selectedDifficulty: String,
    onDifficultySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val difficulties = listOf(
        AdvancedLevel.NONE to "Nie wybrano",
        AdvancedLevel.EASY to "Początkujący",
        AdvancedLevel.MEDIUM to "Średniozaawansowany",
        AdvancedLevel.HARD to "Zaawansowany"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
            shape = RoundedCornerShape(16.dp)
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
                        text = stringResource(R.string.choose_difficulty),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor()
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
                            tint = getOnSurfaceColor()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                difficulties.forEach { (value, label) ->
                    DifficultyOptionItem(
                        label = label,
                        description = getDifficultyDescription(value),
                        isSelected = selectedDifficulty == value,
                        color = getDifficultyColor(value),
                        onClick = { onDifficultySelected(value) }
                    )
                }
            }
        }
    }
}

@Composable
fun GoalOptionItem(
    label: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = if (isSelected) getPrimaryColor().copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = getPrimaryColor(),
                    unselectedColor = getOnSurfaceSecondaryColor()
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = getOnSurfaceColor()
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = getOnSurfaceSecondaryColor()
                )
            }
        }
    }
}

@Composable
fun DifficultyOptionItem(
    label: String,
    description: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        color = if (isSelected) color.copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = color,
                    unselectedColor = getOnSurfaceSecondaryColor()
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = getOnSurfaceColor()
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = getOnSurfaceSecondaryColor()
                )
            }
        }
    }
}

fun getGoalDescription(goal: String): String = when (goal) {
    Goals.MUSCLE_GAIN -> "Budowanie masy mięśniowej i objętości"
    Goals.STRENGTH -> "Zwiększanie siły maksymalnej"
    Goals.ENDURANCE -> "Poprawa wytrzymałości mięśniowej"
    Goals.FAT_LOSS -> "Spalanie tkanki tłuszczowej"
    Goals.CALORIE_BURN -> "Intensywne spalanie kalorii"
    Goals.MASS_GAIN -> "Zwiększenie masy ciała"
    Goals.COORDINATION -> "Rozwój koordynacji ruchowej"
    Goals.MOBILITY -> "Poprawa zakresu ruchów"
    Goals.FLEXIBILITY -> "Zwiększenie elastyczności"
    Goals.LEGS_GLUTES -> "Wzmocnienie dolnej partii ciała"
    Goals.POSTURE -> "Korekta i wzmocnienie postawy"
    Goals.RECOVERY -> "Regeneracja i relaksacja"
    else -> "Brak konkretnego celu"
}

fun getDifficultyDescription(difficulty: String): String = when (difficulty) {
    AdvancedLevel.NONE -> "Nie wybrano poziomu trudności"
    AdvancedLevel.EASY -> "Do 6 miesięcy doświadczenia"
    AdvancedLevel.MEDIUM -> "6 miesięcy - 2 lata doświadczenia"
    AdvancedLevel.HARD -> "Ponad 2 lata doświadczenia"
    else -> ""
}

@Composable
fun getDifficultyColor(difficulty: String): Color = when (difficulty) {
    AdvancedLevel.NONE -> getOnSurfaceSecondaryColor()
    AdvancedLevel.EASY -> Color(0xFF4CAF50)
    AdvancedLevel.MEDIUM -> Color(0xFFFF9800)
    AdvancedLevel.HARD -> Color(0xFFF44336)
    else -> getPrimaryColor()
}