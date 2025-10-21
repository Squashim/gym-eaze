package com.example.gymeaze.ui.screens.exercises.loggedIn

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.ui.components.BackgroundWithDotsAndCircles
import com.example.gymeaze.ui.components.ExerciseCard
import com.example.gymeaze.ui.components.LoadingScreen
import com.example.gymeaze.ui.screens.exercises.logggedIn.DeleteExerciseDialog
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.viewmodel.ExercisesViewModel




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyExercisesScreen(
    onBackClick: (() -> Unit)? = null,
    onExerciseClick: (String) -> Unit = {},
    onEditExercise: (Exercise) -> Unit = {},
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

    var lastLoadedUserId by remember { mutableStateOf<String?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var exerciseToDelete by remember { mutableStateOf<Exercise?>(null) }

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
            viewModel.switchToMode(ExercisesViewModel.ExerciseMode.MY_EXERCISES, userId)
            viewModel.reloadUserDataForExercises()
            viewModel.loadUserExercises(userId)
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
                            text = "Moje ćwiczenia",
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
            if (currentUser == null || uiState.isLoading) {
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
                        currentUser?._id?.let { viewModel.refreshUserExercises(it) }
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
                                Button(onClick = {
                                    currentUser?._id?.let { viewModel.refreshUserExercises(it) }
                                }) {
                                    Text("Spróbuj ponownie")
                                }
                            }
                        }

                        uiState.exercises.isEmpty() -> {
                            EmptyMyExercisesMessage(modifier = Modifier.padding(paddingValues))
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
                                        isCurrentUserExercise = true,
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
                                            }
                                        },
                                        onEditClick = { exerciseId ->
                                            val exerciseToEdit = uiState.exercises.find { it.id == exerciseId }
                                            exerciseToEdit?.let { onEditExercise(it) }
                                        },
                                        onDeleteClick = { exerciseId ->
                                            val exercise = uiState.exercises.find { it.id == exerciseId }
                                            exercise?.let {
                                                exerciseToDelete = it
                                                showDeleteDialog = true
                                            }
                                        },
                                        onExerciseClick = { onExerciseClick(exercise.id) }
                                    )
                                }

                                if (uiState.hasMorePages) {
                                    item {
                                        LaunchedEffect(Unit) {
                                            currentUser?._id?.let { viewModel.loadMoreUserExercises(it) }
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

        if (showDeleteDialog && exerciseToDelete != null) {
            DeleteExerciseDialog(
                exerciseName = exerciseToDelete!!.name,
                onDismiss = {
                    showDeleteDialog = false
                    exerciseToDelete = null
                },
                onConfirm = {
                    val exerciseId = exerciseToDelete!!.id
                    showDeleteDialog = false
                    exerciseToDelete = null

                    viewModel.deleteExercise(exerciseId) {
                        currentUser?._id?.let { viewModel.refreshUserExercises(it) }
                    }
                }
            )
        }
    }
}


@Composable
private fun EmptyMyExercisesMessage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FitnessCenter,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = getOnSurfaceSecondaryColor()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nie masz jeszcze ćwiczeń",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = getOnSurfaceColor(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Dodaj swoje pierwsze ćwiczenie, aby było widoczne tutaj",
            fontSize = 14.sp,
            color = getOnSurfaceSecondaryColor(),
            textAlign = TextAlign.Center
        )
    }
}