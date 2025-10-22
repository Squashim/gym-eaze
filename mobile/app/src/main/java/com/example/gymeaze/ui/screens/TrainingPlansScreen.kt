package com.example.gymeaze.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.ui.components.*
import com.example.gymeaze.ui.screens.trainingsPlan.components.TrainingPlanFiltersDialog
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.util.GlobalFavoriteState
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

    val listState = rememberLazyListState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val hasFilters = hasActiveFilters(
        selectedGoal,
        selectedDifficulty,
        minDuration,
        maxDuration,
        selectedHashtags,
        sortBy,
        followersOnly
    )

    LaunchedEffect(Unit) {
        viewModel.refreshPlansFromOtherScreens()
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

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex > (totalItemsNumber - 3)
        }.collect { shouldLoadMore ->
            if (shouldLoadMore && uiState.hasMorePages && !uiState.isLoadingMore && !uiState.isRefreshing) {
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
                    Box {
                        IconButton(onClick = { showFilters = true }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filtry i sortowanie",
                                tint = getOnSurfaceColor()
                            )
                        }

                        if (hasFilters) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(getPrimaryColor(), CircleShape)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-4).dp, y = 4.dp)
                            )
                        }
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

            // UPROSZCZONE: Używamy prostszego PullToRefreshBox z Material3
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.refreshTrainingPlans() },
                modifier = Modifier.fillMaxSize()
            ) {
                // JEŚLI TRWA ODSWIEŻANIE, POKAŻ LOADING SCREEN
                if (uiState.isRefreshing) {
                    LoadingScreen()
                } else {
                    // W PRZECIWNYM WYPADKU POKAŻ NORMALNĄ ZAWARTOŚĆ
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
                                placeholder = {
                                    Text(
                                        "Szukaj planów treningowych...",
                                        color = getOnSurfaceSecondaryColor()
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Szukaj",
                                        tint = getOnSurfaceSecondaryColor()
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Wyczyść",
                                                tint = getOnSurfaceSecondaryColor()
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = getPrimaryColor(),
                                    unfocusedBorderColor = getInputBorderColor(),
                                    focusedTextColor = getOnSurfaceColor(),
                                    unfocusedTextColor = getOnSurfaceColor(),
                                    cursorColor = getPrimaryColor()
                                ),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }

                        when {
                            uiState.isLoading && uiState.trainingPlans.isEmpty() -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    LoadingScreen()
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
                sortBy = sortBy,
                followersOnly = followersOnly,
                onSearchQueryChanged = viewModel::updateSearchQuery,
                onGoalChanged = viewModel::updateSelectedGoal,
                onDifficultyChanged = viewModel::updateSelectedDifficulty,
                onMinDurationChanged = viewModel::updateMinDuration,
                onMaxDurationChanged = viewModel::updateMaxDuration,
                onHashtagsChanged = viewModel::updateSelectedHashtags,
                onSortByChanged = viewModel::updateSortBy,
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

private fun hasActiveFilters(
    selectedGoal: String?,
    selectedDifficulty: String?,
    minDuration: Int?,
    maxDuration: Int?,
    selectedHashtags: List<String>,
    sortBy: String,
    followersOnly: Boolean
): Boolean {
    return selectedGoal != null ||
            selectedDifficulty != null ||
            minDuration != null ||
            maxDuration != null ||
            selectedHashtags.isNotEmpty() ||
            sortBy != "latest" ||
            followersOnly
}