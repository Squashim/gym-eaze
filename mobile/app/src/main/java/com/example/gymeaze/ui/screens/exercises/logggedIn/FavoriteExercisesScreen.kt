package com.example.gymeaze.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.ui.components.BackgroundWithDotsAndCircles
import com.example.gymeaze.ui.components.ExerciseCard
import com.example.gymeaze.ui.components.LoadingScreen
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.ExercisesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteExercisesScreen(
    onBackClick: (() -> Unit)? = null,
    onExerciseClick: (String) -> Unit = {},
    viewModel: ExercisesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val favoriteExercises by viewModel.favoriteExercises.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val scrollIndex by viewModel.scrollIndex
    val scrollOffset by viewModel.scrollOffset

    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = scrollIndex,
        initialFirstVisibleItemScrollOffset = scrollOffset
    )
    val coroutineScope = rememberCoroutineScope()

    var lastLoadedUserId by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(Unit) {
        viewModel.reloadCurrentUser()
    }

    LaunchedEffect(currentUser?._id) {
        val userId = currentUser?._id
        if (userId != null && userId != lastLoadedUserId) {
            viewModel.clearUserExercisesState()
            viewModel.reloadUserDataForExercises()
            viewModel.switchToMode(ExercisesViewModel.ExerciseMode.FAVORITES)
            viewModel.loadFavoriteExercises()
            lastLoadedUserId = userId
        }
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow {
            lazyListState.firstVisibleItemIndex to lazyListState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            viewModel.updateScrollPosition(index, offset)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearUserExercisesState()
            lastLoadedUserId = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundWithDotsAndCircles(modifier = Modifier.fillMaxSize())

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Ulubione ćwiczenia",
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
                    onRefresh = {
                        viewModel.refreshFavoriteExercises()
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    when {
                        uiState.error != null -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
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
                                Button(onClick = { viewModel.refreshFavoriteExercises() }) {
                                    Text("Spróbuj ponownie")
                                }
                            }
                        }

                        uiState.exercises.isEmpty() -> {
                            EmptyFavoritesMessage()
                        }

                        else -> {
                            LazyColumn(
                                state = lazyListState,
                                contentPadding = PaddingValues(
                                    top = paddingValues.calculateTopPadding() + 16.dp,
                                    bottom = 16.dp,
                                    start = 16.dp,
                                    end = 16.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = uiState.exercises,
                                    key = { exercise -> exercise.id }
                                ) { exercise ->
                                    ExerciseCard(
                                        exercise = exercise,
                                        isLiked = viewModel.isExerciseLiked(exercise.id),
                                        isFavorite = favoriteExercises.contains(exercise.id),
                                        isCurrentUserExercise = viewModel.isCurrentUserExercise(exercise.author.username),
                                        onLikeClick = {
                                            viewModel.toggleLike(exercise.id) { success, message ->
                                                if (success) {
                                                    showToast(message)
                                                }
                                            }
                                        },
                                        onFavoriteClick = {
                                            viewModel.toggleFavorite(exercise.id) { success, message ->
                                                if (success) {
                                                    showToast(message)
                                                }
                                                coroutineScope.launch {
                                                    kotlinx.coroutines.delay(500)
                                                    viewModel.refreshFavoriteExercises()
                                                }
                                            }
                                        },
                                        onEditClick = { exerciseId ->
                                            // TODO: Implementacja edycji
                                        },
                                        onDeleteClick = { exerciseId ->
                                            // TODO: Implementacja usuwania
                                        },
                                        onExerciseClick = { onExerciseClick(exercise.id) }
                                    )
                                }

                                if (uiState.hasMorePages) {
                                    item {
                                        LaunchedEffect(Unit) {
                                            viewModel.loadMoreFavoriteExercises()
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
}

@Composable
private fun EmptyFavoritesMessage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = getOnSurfaceSecondaryColor()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Brak ulubionych ćwiczeń",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = getOnSurfaceColor(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Dodaj ćwiczenia do ulubionych, aby były tutaj widoczne",
            fontSize = 14.sp,
            color = getOnSurfaceSecondaryColor(),
            textAlign = TextAlign.Center
        )
    }
}