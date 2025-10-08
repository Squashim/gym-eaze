package com.example.gymeaze.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.ui.components.*
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.SnackbarType
import com.example.gymeaze.viewmodel.TrainingPlansViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingPlansScreen(
    onBackClick: () -> Unit,
    onPlanClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: TrainingPlansViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedGoal by viewModel.selectedGoal.collectAsState()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsState()
    val minDuration by viewModel.minDuration.collectAsState()
    val maxDuration by viewModel.maxDuration.collectAsState()
    val selectedHashtags by viewModel.selectedHashtags.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()
    val followersOnly by viewModel.followersOnly.collectAsState()

    var showFilters by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.refreshPlansFromOtherScreens()
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

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex > (totalItemsNumber - 3)
        }.collect { shouldLoadMore ->
            if (shouldLoadMore && uiState.hasMorePages && !uiState.isLoadingMore) {
                viewModel.loadMoreTrainingPlans()
            }
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
                    )
                }
            )
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Plany treningowe",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Wróć",
                            tint = getOnSurfaceColor()
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ustawienia",
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            BackgroundWithDotsAndCircles(modifier = Modifier.fillMaxSize())

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = viewModel::updateSearchQuery,
                        placeholder = { Text("Szukaj planów treningowych...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Szukaj",
                                tint = getOnSurfaceSecondaryColor()
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = getPrimaryColor(),
                            unfocusedBorderColor = getInputBorderColor()
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    IconButton(
                        onClick = { showFilters = true },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = if (hasActiveFilters(selectedGoal, selectedDifficulty, minDuration, maxDuration, selectedHashtags, followersOnly))
                                    getPrimaryColor().copy(alpha = 0.1f)
                                else getSurfaceColor(),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtry",
                            tint = if (hasActiveFilters(selectedGoal, selectedDifficulty, minDuration, maxDuration, selectedHashtags, followersOnly))
                                getPrimaryColor()
                            else getOnSurfaceColor()
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showSortMenu = true },
                            modifier = Modifier
                                .size(48.dp)
                                .background(getSurfaceColor(), RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Sortuj",
                                tint = getOnSurfaceColor()
                            )
                        }

                        SortDropdownMenu(
                            expanded = showSortMenu,
                            onDismiss = { showSortMenu = false },
                            currentSort = sortBy,
                            onSortSelected = { sort ->
                                viewModel.updateSortBy(sort)
                                showSortMenu = false
                            }
                        )
                    }
                }

                if (hasActiveFilters(selectedGoal, selectedDifficulty, minDuration, maxDuration, selectedHashtags, followersOnly)) {
                    ActiveFiltersRow(
                        selectedGoal = selectedGoal,
                        selectedDifficulty = selectedDifficulty,
                        minDuration = minDuration,
                        maxDuration = maxDuration,
                        selectedHashtags = selectedHashtags,
                        followersOnly = followersOnly,
                        onClearFilter = { filterType ->
                            when (filterType) {
                                "goal" -> viewModel.updateSelectedGoal(null)
                                "difficulty" -> viewModel.updateSelectedDifficulty(null)
                                "duration" -> {
                                    viewModel.updateMinDuration(null)
                                    viewModel.updateMaxDuration(null)
                                }
                                "hashtags" -> viewModel.updateSelectedHashtags(emptyList())
                                "followers" -> viewModel.updateFollowersOnly(false)
                            }
                        },
                        onClearAll = viewModel::clearAllFilters
                    )
                }

                when {
                    uiState.isLoading && uiState.trainingPlans.isEmpty() -> {
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
                                    text = "Ładowanie planów treningowych...",
                                    color = getOnSurfaceSecondaryColor(),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    uiState.trainingPlans.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    tint = getOnSurfaceSecondaryColor(),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Brak planów treningowych",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = getOnSurfaceColor()
                                )
                                Text(
                                    text = "Spróbuj zmienić filtry wyszukiwania",
                                    fontSize = 14.sp,
                                    color = getOnSurfaceSecondaryColor()
                                )
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                items = uiState.trainingPlans,
                                key = { it.id }
                            ) { plan ->
                                TrainingPlanCard(
                                    trainingPlan = plan,
                                    onCardClick = onPlanClick,
                                    onLikeClick = viewModel::toggleLike,
                                    onFavoriteClick = viewModel::toggleFavorite
                                )
                            }

                            if (uiState.isLoadingMore) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = getPrimaryColor(),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            TrainingPlanFiltersDialog(
                isVisible = showFilters,
                searchQuery = searchQuery,
                selectedGoal = selectedGoal,
                selectedDifficulty = selectedDifficulty,
                minDuration = minDuration,
                maxDuration = maxDuration,
                selectedHashtags = selectedHashtags,
                followersOnly = followersOnly,
                onSearchQueryChanged = viewModel::updateSearchQuery,
                onGoalChanged = viewModel::updateSelectedGoal,
                onDifficultyChanged = viewModel::updateSelectedDifficulty,
                onMinDurationChanged = viewModel::updateMinDuration,
                onMaxDurationChanged = viewModel::updateMaxDuration,
                onHashtagsChanged = viewModel::updateSelectedHashtags,
                onFollowersOnlyChanged = viewModel::updateFollowersOnly,
                onApplyFilters = { showFilters = false },
                onClearFilters = {
                    viewModel.clearAllFilters()
                    showFilters = false
                },
                onDismiss = { showFilters = false }
            )
        }
    }
}

@Composable
private fun SortDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    currentSort: String,
    onSortSelected: (String) -> Unit
) {
    val sortOptions = listOf(
        "latest" to "Najnowsze",
        "popular" to "Popularne (24h)",
        "top-rated" to "Najwyżej ocenione"
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier.background(getSurfaceColor())
    ) {
        sortOptions.forEach { (value, label) ->
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentSort == value) {
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
                onClick = { onSortSelected(value) }
            )
        }
    }
}

@Composable
private fun ActiveFiltersRow(
    selectedGoal: String?,
    selectedDifficulty: String?,
    minDuration: Int?,
    maxDuration: Int?,
    selectedHashtags: List<String>,
    followersOnly: Boolean,
    onClearFilter: (String) -> Unit,
    onClearAll: () -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (selectedGoal != null) {
            item {
                FilterChip(
                    text = "Cel: ${getGoalDisplayName(selectedGoal)}",
                    onRemove = { onClearFilter("goal") }
                )
            }
        }

        if (selectedDifficulty != null) {
            item {
                FilterChip(
                    text = "Poziom: ${getDifficultyDisplayName(selectedDifficulty)}",
                    onRemove = { onClearFilter("difficulty") }
                )
            }
        }

        if (minDuration != null || maxDuration != null) {
            item {
                val text = when {
                    minDuration != null && maxDuration != null -> "Dni: $minDuration-$maxDuration"
                    minDuration != null -> "Dni: od $minDuration"
                    maxDuration != null -> "Dni: do $maxDuration"
                    else -> ""
                }
                FilterChip(
                    text = text,
                    onRemove = { onClearFilter("duration") }
                )
            }
        }

        if (selectedHashtags.isNotEmpty()) {
            item {
                FilterChip(
                    text = "Hashtagi: ${selectedHashtags.size}",
                    onRemove = { onClearFilter("hashtags") }
                )
            }
        }

        if (followersOnly) {
            item {
                FilterChip(
                    text = "Obserwowani",
                    onRemove = { onClearFilter("followers") }
                )
            }
        }

        item {
            TextButton(
                onClick = onClearAll,
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "Wyczyść wszystkie",
                    fontSize = 12.sp,
                    color = getPrimaryColor()
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = getPrimaryColor().copy(alpha = 0.1f),
        modifier = Modifier.height(32.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = getPrimaryColor(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "×",
                color = getPrimaryColor(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onRemove() }
                    .padding(2.dp)
            )
        }
    }
}

private fun hasActiveFilters(
    selectedGoal: String?,
    selectedDifficulty: String?,
    minDuration: Int?,
    maxDuration: Int?,
    selectedHashtags: List<String>,
    followersOnly: Boolean
): Boolean {
    return selectedGoal != null ||
            selectedDifficulty != null ||
            minDuration != null ||
            maxDuration != null ||
            selectedHashtags.isNotEmpty() ||
            followersOnly
}

private fun getGoalDisplayName(goal: String): String {
    return when (goal) {
        "Budowanie masy mięśniowej" -> "Masa"
        "Zwiększanie siły" -> "Siła"
        "Poprawa wytrzymałości" -> "Wytrzymałość"
        "Redukcja tkanki tłuszczowej" -> "Redukcja"
        "Spalanie kalorii" -> "Spalanie"
        "Poprawa mobilności" -> "Mobilność"
        "Poprawa gibkości" -> "Gibkość"
        else -> goal
    }
}

private fun getDifficultyDisplayName(difficulty: String): String {
    return when (difficulty) {
        "Początkujący" -> "Początkujący"
        "Średniozaawansowany" -> "Średni"
        "Zaawansowany" -> "Zaawansowany"
        else -> difficulty
    }
}