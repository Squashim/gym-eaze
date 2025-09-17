package com.example.gymeaze.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.data.models.Equipment
import com.example.gymeaze.data.models.MuscleGroup
import com.example.gymeaze.ui.components.ExerciseCard
import com.example.gymeaze.ui.components.HashtagInputField
import com.example.gymeaze.ui.components.MultiSelectDropdown
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.ExercisesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(
    onSettingsClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    onExerciseClick: (String) -> Unit = {},
    viewModel: ExercisesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedMuscleGroups by viewModel.selectedMuscleGroups.collectAsState()
    val selectedEquipment by viewModel.selectedEquipment.collectAsState()
    val selectedHashtags by viewModel.selectedHashtags.collectAsState()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()
    val availableMuscleGroups by viewModel.availableMuscleGroups.collectAsState()
    val availableEquipment by viewModel.availableEquipment.collectAsState()

    var showFilters by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Przeglądaj ćwiczenia",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor()
                    )
                },
                navigationIcon = {
                    onBackClick?.let { backCallback ->
                        IconButton(onClick = backCallback) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Wstecz",
                                tint = getOnSurfaceColor()
                            )
                        }
                    }
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (hasActiveFilters(selectedMuscleGroups, selectedEquipment, selectedHashtags, selectedDifficulty, sortBy)) {
                                Badge(
                                    containerColor = getPrimaryColor(),
                                    modifier = Modifier.size(8.dp)
                                )
                            }
                        }
                    ) {
                        IconButton(onClick = { showFilters = true }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filtry",
                                tint = getOnSurfaceColor()
                            )
                        }
                    }

                    IconButton(onClick = { viewModel.refreshExercises() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Odśwież",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchField(
                query = searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = getPrimaryColor()
                        )
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
                            text = uiState.error ?: "Wystąpił nieznany błąd",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.refreshExercises() }) {
                            Text("Spróbuj ponownie")
                        }
                    }
                }
                uiState.exercises.isEmpty() -> {
                    EmptyExercisesMessage()
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.exercises) { exercise ->
                            ExerciseCard(
                                exercise = exercise,
                                onExerciseClick = { onExerciseClick(exercise.id) },
                                onLikeClick = { viewModel.toggleLike(exercise.id) }
                            )
                        }

                        if (uiState.hasMorePages) {
                            item(span = { GridItemSpan(2) }) {
                                LaunchedEffect(Unit) {
                                    viewModel.loadMoreExercises()
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (uiState.isLoadingMore) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = getPrimaryColor(),
                                            strokeWidth = 2.dp
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

    if (showFilters) {
        FilterDialog(
            selectedMuscleGroups = selectedMuscleGroups,
            availableMuscleGroups = availableMuscleGroups,
            onMuscleGroupsChange = viewModel::updateSelectedMuscleGroups,
            selectedEquipment = selectedEquipment,
            availableEquipment = availableEquipment,
            onEquipmentChange = viewModel::updateSelectedEquipment,
            selectedHashtags = selectedHashtags,
            onHashtagsChange = viewModel::updateSelectedHashtags,
            selectedDifficulty = selectedDifficulty,
            onDifficultyChange = viewModel::updateSelectedDifficulty,
            sortBy = sortBy,
            onSortByChange = viewModel::updateSortBy,
            onClearAll = viewModel::clearAllFilters,
            onDismiss = { showFilters = false }
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Wyszukaj ćwiczenia...",
                color = getOnSurfaceSecondaryColor()
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Wyszukaj",
                tint = getOnSurfaceSecondaryColor()
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Wyczyść",
                        tint = getOnSurfaceSecondaryColor()
                    )
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = getPrimaryColor(),
            unfocusedBorderColor = getInputBorderColor(),
            focusedTextColor = getOnSurfaceColor(),
            unfocusedTextColor = getOnSurfaceColor(),
            cursorColor = getPrimaryColor()
        ),
        singleLine = true
    )
}

@Composable
private fun FilterDialog(
    selectedMuscleGroups: List<MuscleGroup>,
    availableMuscleGroups: List<MuscleGroup>,
    onMuscleGroupsChange: (List<MuscleGroup>) -> Unit,
    selectedEquipment: List<Equipment>,
    availableEquipment: List<Equipment>,
    onEquipmentChange: (List<Equipment>) -> Unit,
    selectedHashtags: List<String>,
    onHashtagsChange: (List<String>) -> Unit,
    selectedDifficulty: String?,
    onDifficultyChange: (String?) -> Unit,
    sortBy: String,
    onSortByChange: (String) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 650.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Filtry i sortowanie",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor()
                    )
                    Row {
                        TextButton(onClick = onClearAll) {
                            Text(
                                text = "Wyczyść",
                                color = getOnSurfaceSecondaryColor()
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Zamknij",
                                tint = getOnSurfaceColor()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    item {
                        SortingSection(
                            selectedSort = sortBy,
                            onSortChange = onSortByChange
                        )
                    }

                    item {
                        MultiSelectDropdown(
                            label = "Partie mięśniowe",
                            selectedItems = selectedMuscleGroups,
                            availableItems = availableMuscleGroups,
                            onSelectionChange = onMuscleGroupsChange,
                            itemDisplayText = { it.name },
                            placeholder = "Wybierz partie mięśniowe",
                            isLoading = availableMuscleGroups.isEmpty()
                        )
                    }

                    item {
                        MultiSelectDropdown(
                            label = "Sprzęt",
                            selectedItems = selectedEquipment,
                            availableItems = availableEquipment,
                            onSelectionChange = onEquipmentChange,
                            itemDisplayText = { it.name },
                            placeholder = "Wybierz sprzęt",
                            isLoading = availableEquipment.isEmpty()
                        )
                    }

                    item {
                        DifficultySelector(
                            selectedDifficulty = selectedDifficulty,
                            onDifficultyChange = onDifficultyChange
                        )
                    }

                    item {
                        HashtagInputField(
                            selectedHashtags = selectedHashtags,
                            onHashtagsChange = onHashtagsChange,
                            maxHashtags = 10
                        )
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
                text = "Najwyżej oceniane",
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
private fun DifficultySelector(
    selectedDifficulty: String?,
    onDifficultyChange: (String?) -> Unit
) {
    Column {
        Text(
            text = "Poziom trudności",
            color = getOnSurfaceColor(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val difficultyOptions = listOf(
                Triple(AdvancedLevel.EASY, "Łatwy", Color(0xFF4CAF50)),
                Triple(AdvancedLevel.MEDIUM, "Średni", Color(0xFFFF9800)),
                Triple(AdvancedLevel.HARD, "Trudny", Color(0xFFF44336))
            )

            difficultyOptions.forEach { (backendValue, displayText, chipColor) ->
                FilterChip(
                    selected = selectedDifficulty == backendValue,
                    onClick = {
                        onDifficultyChange(
                            if (selectedDifficulty == backendValue) null else backendValue
                        )
                    },
                    label = {
                        Text(
                            text = displayText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = chipColor,
                        selectedLabelColor = Color.White,
                        containerColor = chipColor.copy(alpha = 0.3f),
                        labelColor = chipColor.copy(alpha = 0.9f)
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private fun hasActiveFilters(
    muscleGroups: List<MuscleGroup>,
    equipment: List<Equipment>,
    hashtags: List<String>,
    difficulty: String?,
    sortBy: String
): Boolean {
    return muscleGroups.isNotEmpty() ||
            equipment.isNotEmpty() ||
            hashtags.isNotEmpty() ||
            difficulty != null ||
            sortBy != "latest"
}

@Composable
private fun EmptyExercisesMessage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = getOnSurfaceSecondaryColor()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nie znaleziono ćwiczeń",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = getOnSurfaceColor(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Spróbuj zmienić filtry wyszukiwania lub wyczyść filtry",
            fontSize = 14.sp,
            color = getOnSurfaceSecondaryColor(),
            textAlign = TextAlign.Center
        )
    }
}