package com.example.gymeaze.ui.screens.trainingsPlan.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gymeaze.ui.components.HashtagInputField
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
    sortBy: String,
    followersOnly: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onGoalChanged: (String?) -> Unit,
    onDifficultyChanged: (String?) -> Unit,
    onMinDurationChanged: (Int?) -> Unit,
    onMaxDurationChanged: (Int?) -> Unit,
    onHashtagsChanged: (List<String>) -> Unit,
    onSortByChanged: (String) -> Unit,
    onFollowersOnlyChanged: (Boolean) -> Unit,
    onApplyFilters: () -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit,
    isUserLoggedIn: Boolean = true // DODAJ TEN PARAMETR
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
            onDismissRequest = {
                if (canApplyFilters) {
                    applyDurationChanges()
                }
                onDismiss()
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header z tytułem i przyciskami
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Filtry i sortowanie",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = getOnSurfaceColor()
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    onClearFilters()
                                    minDurationText = ""
                                    maxDurationText = ""
                                    minDurationError = null
                                    maxDurationError = null
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = getOnSurfaceSecondaryColor().copy(alpha = 0.1f)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Wyczyść filtry",
                                    tint = getOnSurfaceSecondaryColor()
                                )
                            }
                            IconButton(onClick = {
                                if (canApplyFilters) {
                                    applyDurationChanges()
                                }
                                onDismiss()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Zamknij",
                                    tint = getOnSurfaceColor()
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = getOnSurfaceSecondaryColor().copy(alpha = 0.2f))

                    // Zawartość dialogu
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        // Sortowanie
                        item {
                            SortingSection(
                                selectedSort = sortBy,
                                onSortChange = onSortByChanged
                            )
                        }

                        // Toggle "Tylko obserwowani" - TYLKO DLA ZALOGOWANYCH
                        if (isUserLoggedIn) {
                            item {
                                FollowersOnlyToggle(
                                    enabled = followersOnly,
                                    onToggle = onFollowersOnlyChanged
                                )
                            }
                        }

                        // Cel treningowy
                        item {
                            Column {
                                Text(
                                    text = "Cel treningowy",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = getOnSurfaceColor()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                GoalDropdown(
                                    selectedGoal = selectedGoal,
                                    onSelectionChanged = onGoalChanged
                                )
                            }
                        }

                        // Poziom trudności
                        item {
                            Column {
                                Text(
                                    text = "Poziom trudności",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = getOnSurfaceColor()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                DifficultyDropdown(
                                    selectedDifficulty = selectedDifficulty,
                                    onSelectionChanged = onDifficultyChanged
                                )
                            }
                        }

                        // Liczba dni
                        item {
                            Column {
                                Text(
                                    text = "Liczba dni",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = getOnSurfaceColor()
                                )
                                Text(
                                    text = "Zakres od 1 do 30 dni",
                                    fontSize = 12.sp,
                                    color = getOnSurfaceSecondaryColor(),
                                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                                )

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
                                                cursorColor = getPrimaryColor(),
                                                focusedTextColor = getOnSurfaceColor(),
                                                unfocusedTextColor = getOnSurfaceColor()
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
                                                cursorColor = getPrimaryColor(),
                                                focusedTextColor = getOnSurfaceColor(),
                                                unfocusedTextColor = getOnSurfaceColor()
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Hashtagi
                        item {
                            HashtagInputField(
                                selectedHashtags = selectedHashtags,
                                onHashtagsChange = onHashtagsChanged,
                                maxHashtags = 10
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SortingSection(
    selectedSort: String,
    onSortChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Sortowanie",
            color = getOnSurfaceColor(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SortChip(
                text = "Najnowsze",
                isSelected = selectedSort == "latest",
                onClick = { onSortChange("latest") }
            )
            SortChip(
                text = "Popularne",
                isSelected = selectedSort == "popular",
                onClick = { onSortChange("popular") }
            )
            SortChip(
                text = "Najwyżej ocenione",
                isSelected = selectedSort == "top-rated",
                onClick = { onSortChange("top-rated") }
            )
        }
    }
}

@Composable
private fun SortChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                fontSize = 12.sp
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = getPrimaryColor(),
            selectedLabelColor = getOnPrimaryColor(),
            containerColor = getSurfaceColor(),
            labelColor = getOnSurfaceColor()
        )
    )
}

@Composable
private fun FollowersOnlyToggle(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = getOnSurfaceSecondaryColor().copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Tylko obserwowani",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = getOnSurfaceColor()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pokaż plany tylko od obserwowanych użytkowników",
                    fontSize = 14.sp,
                    color = getOnSurfaceSecondaryColor(),
                    lineHeight = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = getPrimaryColor(),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = getOnSurfaceSecondaryColor().copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
private fun GoalDropdown(
    selectedGoal: String?,
    onSelectionChanged: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val goals = listOf(
        "Nie wybrano" to "Nie wybrano",
        "Budowanie masy mięśniowej" to "Budowanie masy mięśniowej",
        "Zwiększanie siły" to "Zwiększanie siły",
        "Poprawa wytrzymałości" to "Poprawa wytrzymałości",
        "Redukcja tkanki tłuszczowej" to "Redukcja tkanki tłuszczowej",
        "Spalanie kalorii" to "Spalanie kalorii",
        "Zwiększenie masy ciała" to "Zwiększenie masy ciała",
        "Poprawa koordynacji" to "Poprawa koordynacji",
        "Poprawa mobilności" to "Poprawa mobilności",
        "Poprawa gibkości" to "Poprawa gibkości",
        "Rozwój nóg i pośladków" to "Rozwój nóg i pośladków",
        "Poprawa postawy ciała" to "Poprawa postawy ciała",
        "Regeneracja i odpoczynek" to "Regeneracja i odpoczynek"
    )

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    width = 1.dp,
                    color = getInputBorderColor(),
                    shape = RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp))
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
                    text = selectedGoal ?: "Nie wybrano",
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
            modifier = Modifier
                .background(getSurfaceColor())
                .heightIn(max = 400.dp)
        ) {
            goals.forEach { (value, label) ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (selectedGoal == value || (selectedGoal == null && value == "Nie wybrano")) {
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
                        onSelectionChanged(if (value == "Nie wybrano") null else value)
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
        "Nie wybrano" to "Nie wybrano",
        "Początkujący" to "Początkujący",
        "Średniozaawansowany" to "Średniozaawansowany",
        "Zaawansowany" to "Zaawansowany"
    )

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    width = 1.dp,
                    color = getInputBorderColor(),
                    shape = RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp))
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
                    text = selectedDifficulty ?: "Nie wybrano",
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
                            if (selectedDifficulty == value || (selectedDifficulty == null && value == "Nie wybrano")) {
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
                        onSelectionChanged(if (value == "Nie wybrano") null else value)
                        expanded = false
                    }
                )
            }
        }
    }
}