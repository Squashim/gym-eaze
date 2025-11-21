package com.example.gymeaze.ui.screens.trainingsPlan.guest

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.R
import com.example.gymeaze.ui.components.BackgroundWithDotsAndCircles
import com.example.gymeaze.ui.components.LoadingScreen
import com.example.gymeaze.ui.screens.exercises.logggedIn.SearchField
import com.example.gymeaze.ui.screens.posts.guest.components.LoginToLikeDialog
import com.example.gymeaze.ui.screens.trainingsPlan.components.TrainingPlanCard
import com.example.gymeaze.ui.screens.trainingsPlan.components.TrainingPlanFiltersDialog
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.trainingsPlan.TrainingPlansViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsNotLoggedInScreen(
    onSettingsClick: () -> Unit,
    onPlanClick: (String) -> Unit = {},
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
    var showLoginDialog by remember { mutableStateOf(false) }
    val isDarkTheme = LocalDarkTheme.current
    val listState = rememberLazyListState()
    val context = LocalContext.current

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
        viewModel.refreshTrainingPlans()
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

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundWithDotsAndCircles(modifier = Modifier.fillMaxSize())

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(
                                    id = if (isDarkTheme) {
                                        R.drawable.gym_eaze_logo_light
                                    } else {
                                        R.drawable.gym_eaze_logo_dark
                                    }
                                ),
                                contentDescription = stringResource(R.string.logo),
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    },
                    actions = {
                        BadgedBox(
                            badge = {
                                if (hasFilters) {
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
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = getBackgroundColor()
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            if (uiState.isLoading && uiState.trainingPlans.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    LoadingScreen()
                }
            } else {
                PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { viewModel.refreshTrainingPlans() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        SearchField(
                            query = searchQuery,
                            onQueryChange = { query ->
                                viewModel.updateSearchQuery(query)
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        when {
                            uiState.error != null -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = uiState.error ?: "Wystąpił nieznany błąd",
                                        color = MaterialTheme.colorScheme.error,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { viewModel.refreshTrainingPlans() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = getPrimaryColor()
                                        )
                                    ) {
                                        Text("Spróbuj ponownie")
                                    }
                                }
                            }

                            uiState.trainingPlans.isEmpty() -> {
                                EmptyTrainingPlansMessage()
                            }

                            else -> {
                                LazyColumn(
                                    state = listState,
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(
                                        items = uiState.trainingPlans,
                                        key = { plan -> plan.id }
                                    ) { plan ->
                                        TrainingPlanCard(
                                            trainingPlan = plan,
                                            onCardClick = onPlanClick,
                                            onLikeClick = { showLoginDialog = true },
                                            onFavoriteClick = { showLoginDialog = true },
                                            onEditClick = {},
                                            onDeleteClick = {},
                                            showOwnerActions = false
                                        )
                                    }

                                    if (uiState.hasMorePages) {
                                        item {
                                            LaunchedEffect(Unit) {
                                                viewModel.loadMoreTrainingPlans()
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
            }
        }

        if (showFilters) {
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
                },
                onDismiss = { showFilters = false },
                isUserLoggedIn = false
            )
        }

        if (showLoginDialog) {
            LoginToLikeDialog(
                onDismiss = { showLoginDialog = false }
            )
        }
    }
}

@Composable
private fun EmptyTrainingPlansMessage() {
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
                tint = getOnSurfaceSecondaryColor(),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Brak planów treningowych",
                fontSize = 18.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
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