package com.example.gymeaze.ui.screens.exercises.guest

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.ui.components.BackgroundWithDotsAndCircles
import com.example.gymeaze.ui.components.ExerciseCard
import com.example.gymeaze.ui.components.LoadingScreen
import com.example.gymeaze.ui.screens.exercises.logggedIn.EmptyExercisesMessage
import com.example.gymeaze.ui.screens.exercises.logggedIn.FilterDialog
import com.example.gymeaze.ui.screens.exercises.logggedIn.SearchField
import com.example.gymeaze.ui.screens.exercises.logggedIn.hasActiveFilters
import com.example.gymeaze.ui.screens.posts.guest.components.LoginToLikeDialog
import com.example.gymeaze.ui.theme.LocalDarkTheme
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.viewmodel.ExercisesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesOverviewScreen(
    onSettingsClick: () -> Unit,
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
    val followersOnly by viewModel.followersOnly.collectAsState()
    val availableMuscleGroups by viewModel.availableMuscleGroups.collectAsState()
    val availableEquipment by viewModel.availableEquipment.collectAsState()

    var showFilters by remember { mutableStateOf(false) }
    var showLoginDialog by remember { mutableStateOf(false) }
    val isDarkTheme = LocalDarkTheme.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.switchToMode(ExercisesViewModel.ExerciseMode.ALL)
        viewModel.refreshExercises()
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
                                    id = if(isDarkTheme){
                                        com.example.gymeaze.R.drawable.gym_eaze_logo_light
                                    } else {
                                        com.example.gymeaze.R.drawable.gym_eaze_logo_dark
                                    }),
                                contentDescription = stringResource(com.example.gymeaze.R.string.logo),
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    },
                    actions = {
                        val hasFilters = hasActiveFilters(
                            selectedMuscleGroups,
                            selectedEquipment,
                            selectedHashtags,
                            selectedDifficulty,
                            sortBy,
                            followersOnly
                        )

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
            if (uiState.isLoading) {
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
                    onRefresh = { viewModel.refreshExercises() },
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
                                LazyColumn(
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(
                                        items = uiState.exercises,
                                        key = { exercise -> exercise.id }
                                    ) { exercise ->
                                        ExerciseCard(
                                            exercise = exercise,
                                            isLiked = false,
                                            isFavorite = false,
                                            isCurrentUserExercise = false,
                                            onLikeClick = { showLoginDialog = true },
                                            onFavoriteClick = { showLoginDialog = true },
                                            onEditClick = {},
                                            onDeleteClick = {},
                                            onExerciseClick = { onExerciseClick(exercise.id) }
                                        )
                                    }

                                    if (uiState.hasMorePages) {
                                        item {
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
            }
        }

        if (showFilters) {
            FilterDialog(
                selectedMuscleGroups = selectedMuscleGroups,
                availableMuscleGroups = availableMuscleGroups,
                onMuscleGroupsChange = { groups ->
                    viewModel.updateSelectedMuscleGroups(groups)
                },
                selectedEquipment = selectedEquipment,
                availableEquipment = availableEquipment,
                onEquipmentChange = { equipment ->
                    viewModel.updateSelectedEquipment(equipment)
                },
                selectedHashtags = selectedHashtags,
                onHashtagsChange = { hashtags ->
                    viewModel.updateSelectedHashtags(hashtags)
                },
                selectedDifficulty = selectedDifficulty,
                onDifficultyChange = { difficulty ->
                    viewModel.updateSelectedDifficulty(difficulty)
                },
                sortBy = sortBy,
                onSortByChange = { sort ->
                    viewModel.updateSortBy(sort)
                },
                followersOnly = followersOnly,
                onFollowersOnlyChange = { enabled ->
                    viewModel.updateFollowersOnly(enabled)
                },
                onClearAll = {
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