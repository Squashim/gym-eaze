package com.example.gymeaze.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.ui.components.*
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.FavoriteTrainingPlansViewModel
import com.example.gymeaze.viewmodel.SnackbarType
import com.example.gymeaze.util.GlobalFavoriteState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteTrainingPlansScreen(
    onBackClick: () -> Unit,
    onPlanClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: FavoriteTrainingPlansViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val globalFavoriteStates by GlobalFavoriteState.getInstance(context).favoriteStates.collectAsState()

    val actualFavoritePlans = uiState.trainingPlans.filter { plan ->
        globalFavoriteStates[plan.id] == true
    }

    LaunchedEffect(Unit) {
        viewModel.refreshFavoritePlansFromOtherScreens()
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
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            )
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ulubione plany",
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
                                    text = "Ładowanie ulubionych planów...",
                                    color = getOnSurfaceSecondaryColor(),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    actualFavoritePlans.isEmpty() -> {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = null,
                                    tint = getOnSurfaceSecondaryColor(),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Brak ulubionych planów",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = getOnSurfaceColor()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Dodaj plany do ulubionych, aby mieć je pod ręką",
                                    fontSize = 14.sp,
                                    color = getOnSurfaceSecondaryColor(),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                OutlinedButton(
                                    onClick = onBackClick,
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = getPrimaryColor()
                                    ),
                                    border = BorderStroke(1.dp, getPrimaryColor())
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Przeglądaj plany")
                                }
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
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = getPrimaryColor().copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Bookmark,
                                            contentDescription = null,
                                            tint = getPrimaryColor(),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Twoje ulubione plany",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = getOnSurfaceColor()
                                            )
                                            Text(
                                                text = "${actualFavoritePlans.size} planów w kolekcji",
                                                fontSize = 12.sp,
                                                color = getOnSurfaceSecondaryColor()
                                            )
                                        }
                                    }
                                }
                            }

                            items(
                                items = actualFavoritePlans,
                                key = { it.id }
                            ) { plan ->
                                TrainingPlanCard(
                                    trainingPlan = plan,
                                    onCardClick = onPlanClick,
                                    onLikeClick = viewModel::toggleLike,
                                    onFavoriteClick = { planId ->
                                        viewModel.removeFavorite(planId)
                                    }
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

                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}