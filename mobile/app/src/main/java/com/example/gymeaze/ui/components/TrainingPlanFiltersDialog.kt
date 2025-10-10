package com.example.gymeaze.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.constants.Goals
import com.example.gymeaze.ui.theme.*

@Composable
fun TrainingPlanFiltersDialog(
    isVisible: Boolean,
    searchQuery: String,
    selectedGoal: String?,
    selectedDifficulty: String?,
    minDuration: Int?,
    maxDuration: Int?,
    selectedHashtags: List<String>,
    followersOnly: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onGoalChanged: (String?) -> Unit,
    onDifficultyChanged: (String?) -> Unit,
    onMinDurationChanged: (Int?) -> Unit,
    onMaxDurationChanged: (Int?) -> Unit,
    onHashtagsChanged: (List<String>) -> Unit,
    onFollowersOnlyChanged: (Boolean) -> Unit,
    onApplyFilters: () -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    var minDurationText by remember { mutableStateOf(minDuration?.toString() ?: "") }
    var maxDurationText by remember { mutableStateOf(maxDuration?.toString() ?: "") }
    var minDurationError by remember { mutableStateOf<String?>(null) }
    var maxDurationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(minDuration) {
        minDurationText = minDuration?.toString() ?: ""
    }
    LaunchedEffect(maxDuration) {
        maxDurationText = maxDuration?.toString() ?: ""
    }

    fun validateMinDuration(value: String) {
        minDurationError = when {
            value.isBlank() -> null
            value.toIntOrNull() == null -> "Podaj prawidłową liczbę"
            value.toInt() < 1 -> "Minimum 1 dzień"
            value.toInt() > 30 -> "Maksymalnie 30 dni"
            maxDurationText.isNotBlank() && maxDurationText.toIntOrNull() != null && value.toInt() > maxDurationText.toInt() -> "Minimum nie może być większe od maksimum"
            else -> null
        }
    }

    fun validateMaxDuration(value: String) {
        maxDurationError = when {
            value.isBlank() -> null
            value.toIntOrNull() == null -> "Podaj prawidłową liczbę"
            value.toInt() < 1 -> "Minimum 1 dzień"
            value.toInt() > 30 -> "Maksymalnie 30 dni"
            minDurationText.isNotBlank() && minDurationText.toIntOrNull() != null && value.toInt() < minDurationText.toInt() -> "Maksimum nie może być mniejsze od minimum"
            else -> null
        }
    }

    fun applyDurationChanges() {
        if (minDurationError == null) {
            onMinDurationChanged(minDurationText.toIntOrNull())
        }
        if (maxDurationError == null) {
            onMaxDurationChanged(maxDurationText.toIntOrNull())
        }
    }

    val canApplyFilters = minDurationError == null && maxDurationError == null

    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .heightIn(max = 600.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filtry planów treningowych",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = getOnSurfaceColor()
                        )

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Zamknij",
                                tint = getOnSurfaceColor()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Dostosuj filtry aby znaleźć idealne plany treningowe.",
                        fontSize = 14.sp,
                        color = getOnSurfaceSecondaryColor()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Wyszukaj:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SearchInput(
                        value = searchQuery,
                        onValueChanged = onSearchQueryChanged
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Cel treningowy:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    GoalDropdown(
                        selectedGoal = selectedGoal,
                        onSelectionChanged = onGoalChanged
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Poziom trudności:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DifficultyDropdown(
                        selectedDifficulty = selectedDifficulty,
                        onSelectionChanged = onDifficultyChanged
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Liczba dni",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = getOnSurfaceColor()
                    )

                    Text(
                        text = "Zakres od 1 do 30 dni",
                        fontSize = 12.sp,
                        color = getOnSurfaceSecondaryColor(),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = minDurationText,
                                onValueChange = { newValue ->
                                    val filteredValue = newValue.filter { it.isDigit() }.take(2)
                                    minDurationText = filteredValue
                                    validateMinDuration(filteredValue)
                                    if (maxDurationText.isNotBlank()) {
                                        validateMaxDuration(maxDurationText)
                                    }
                                },
                                label = { Text("Min") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = minDurationError != null,
                                supportingText = minDurationError?.let { error ->
                                    {
                                        Text(
                                            text = error,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = if (minDurationError != null) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        getPrimaryColor()
                                    },
                                    focusedLabelColor = if (minDurationError != null) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        getPrimaryColor()
                                    },
                                    cursorColor = getPrimaryColor()
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = maxDurationText,
                                onValueChange = { newValue ->
                                    val filteredValue = newValue.filter { it.isDigit() }.take(2)
                                    maxDurationText = filteredValue
                                    validateMaxDuration(filteredValue)
                                    if (minDurationText.isNotBlank()) {
                                        validateMinDuration(minDurationText)
                                    }
                                },
                                label = { Text("Max") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = maxDurationError != null,
                                supportingText = maxDurationError?.let { error ->
                                    {
                                        Text(
                                            text = error,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = if (maxDurationError != null) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        getPrimaryColor()
                                    },
                                    focusedLabelColor = if (maxDurationError != null) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        getPrimaryColor()
                                    },
                                    cursorColor = getPrimaryColor()
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Hashtagi:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    HashtagsInput(
                        hashtags = selectedHashtags,
                        onHashtagsChanged = onHashtagsChanged
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Tylko obserwowani użytkownicy:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pokaż tylko plany od obserwowanych użytkowników",
                            fontSize = 14.sp,
                            color = getOnSurfaceSecondaryColor(),
                            modifier = Modifier.weight(1f)
                        )

                        Switch(
                            checked = followersOnly,
                            onCheckedChange = onFollowersOnlyChanged,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = getPrimaryColor(),
                                checkedTrackColor = getPrimaryColor().copy(alpha = 0.5f),
                                uncheckedThumbColor = getOnSurfaceSecondaryColor(),
                                uncheckedTrackColor = getInputBackgroundColor()
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .border(
                                    width = 1.dp,
                                    color = getInputBorderColor(),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onClearFilters()
                                    minDurationText = ""
                                    maxDurationText = ""
                                    minDurationError = null
                                    maxDurationError = null
                                }
                                .background(getBackgroundColor()),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Wyczyść",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = getOnSurfaceColor()
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(enabled = canApplyFilters) {
                                    if (canApplyFilters) {
                                        applyDurationChanges()
                                        onApplyFilters()
                                    }
                                }
                                .background(
                                    if (canApplyFilters) {
                                        getPrimaryColor()
                                    } else {
                                        getOnSurfaceSecondaryColor().copy(alpha = 0.3f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Zastosuj",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (canApplyFilters) {
                                    getBackgroundColor()
                                } else {
                                    getOnSurfaceSecondaryColor()
                                }
                            )
                        }
                    }

                    if (!canApplyFilters) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Popraw błędy walidacji aby zastosować filtry",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun SearchInput(
    value: String,
    onValueChanged: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(
                width = 1.dp,
                color = getInputBorderColor(),
                shape = RoundedCornerShape(8.dp)
            )
            .background(getInputBackgroundColor())
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(
                text = "Wpisz nazwę planu treningowego...",
                fontSize = 14.sp,
                color = getOnSurfaceSecondaryColor()
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChanged,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                color = getOnSurfaceColor()
            ),
            cursorBrush = SolidColor(getPrimaryColor()),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun GoalDropdown(
    selectedGoal: String?,
    onSelectionChanged: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val goals = listOf(
        null to "Wszystkie cele",
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

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    width = 1.dp,
                    color = getInputBorderColor(),
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .background(getInputBackgroundColor())
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goals.find { it.first == selectedGoal }?.second ?: "Wszystkie cele",
                    fontSize = 14.sp,
                    color = getOnSurfaceColor()
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = getOnSurfaceSecondaryColor()
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(getSurfaceColor())
        ) {
            goals.forEach { (value, label) ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (selectedGoal == value) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = getPrimaryColor(),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            } else {
                                Spacer(modifier = Modifier.width(28.dp))
                            }

                            Text(
                                text = label,
                                fontSize = 14.sp,
                                color = getOnSurfaceColor()
                            )
                        }
                    },
                    onClick = {
                        onSelectionChanged(value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DifficultyDropdown(
    selectedDifficulty: String?,
    onSelectionChanged: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val difficulties = listOf(
        null to "Wszystkie poziomy",
        AdvancedLevel.EASY to "Początkujący",
        AdvancedLevel.MEDIUM to "Średniozaawansowany",
        AdvancedLevel.HARD to "Zaawansowany"
    )

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    width = 1.dp,
                    color = getInputBorderColor(),
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .background(getInputBackgroundColor())
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = difficulties.find { it.first == selectedDifficulty }?.second ?: "Wszystkie poziomy",
                    fontSize = 14.sp,
                    color = getOnSurfaceColor()
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = getOnSurfaceSecondaryColor()
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(getSurfaceColor())
        ) {
            difficulties.forEach { (value, label) ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (selectedDifficulty == value) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = getPrimaryColor(),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            } else {
                                Spacer(modifier = Modifier.width(28.dp))
                            }

                            Text(
                                text = label,
                                fontSize = 14.sp,
                                color = getOnSurfaceColor()
                            )
                        }
                    },
                    onClick = {
                        onSelectionChanged(value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun HashtagsInput(
    hashtags: List<String>,
    onHashtagsChanged: (List<String>) -> Unit
) {
    var inputText by remember { mutableStateOf("") }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    width = 1.dp,
                    color = getInputBorderColor(),
                    shape = RoundedCornerShape(8.dp)
                )
                .background(getInputBackgroundColor())
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (inputText.isEmpty()) {
                Text(
                    text = "np. masa, siła, kondycja...",
                    fontSize = 14.sp,
                    color = getOnSurfaceSecondaryColor()
                )
            }

            BasicTextField(
                value = inputText,
                onValueChange = { newValue ->
                    if (newValue.contains(',') || newValue.contains('\n')) {
                        val hashtagToAdd = inputText.trim().lowercase()
                        if (hashtagToAdd.isNotEmpty() && hashtagToAdd !in hashtags && hashtags.size < 10) {
                            onHashtagsChanged(hashtags + hashtagToAdd)
                        }
                        inputText = ""
                    } else {
                        inputText = newValue.take(30)
                    }
                },
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    color = getOnSurfaceColor()
                ),
                cursorBrush = SolidColor(getPrimaryColor()),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (hashtags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                hashtags.forEach { hashtag ->
                    HashtagChip(
                        text = hashtag,
                        onRemove = {
                            onHashtagsChanged(hashtags - hashtag)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Użyj przecinka lub Enter aby dodać hashtag (max 10)",
            fontSize = 10.sp,
            color = getOnSurfaceSecondaryColor()
        )
    }
}

@Composable
private fun HashtagChip(
    text: String,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = getPrimaryColor().copy(alpha = 0.1f),
        modifier = Modifier.border(1.dp, getPrimaryColor().copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#$text",
                color = getPrimaryColor(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "×",
                color = getPrimaryColor(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onRemove() }
                    .padding(2.dp)
            )
        }
    }
}