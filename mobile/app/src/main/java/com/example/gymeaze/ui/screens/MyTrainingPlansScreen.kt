package com.example.gymeaze.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.ui.components.*
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.MyTrainingPlansViewModel
import androidx.compose.ui.window.Dialog
import com.example.gymeaze.viewmodel.SnackbarType
import com.example.gymeaze.viewmodel.TrainingPlansViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTrainingPlansScreen(
    onBackClick: () -> Unit,
    onPlanClick: (String) -> Unit,
    onEditPlanClick: (String) -> Unit,
    onCreatePlanClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: MyTrainingPlansViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSnackbarMessage()
        }
    }


    LaunchedEffect(Unit) {
        viewModel.refreshMyPlansFromOtherScreens()
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
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
                        contentColor = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(getBackgroundColor())
                .padding(paddingValues)
        ) {
            BackgroundWithDotsAndCircles(modifier = Modifier.fillMaxSize())

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Moje plany treningowe",
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
                        IconButton(onClick = onCreatePlanClick) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Dodaj plan",
                                tint = getOnSurfaceColor()
                            )
                        }
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
                                    text = "Ładowanie Twoich planów...",
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
                                    imageVector = Icons.Default.Assignment,
                                    contentDescription = null,
                                    tint = getOnSurfaceSecondaryColor(),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Nie masz jeszcze żadnych planów",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = getOnSurfaceColor()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Stwórz swój pierwszy plan treningowy!",
                                    fontSize = 14.sp,
                                    color = getOnSurfaceSecondaryColor()
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = onCreatePlanClick,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = getPrimaryColor()
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Utwórz plan")
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
                            items(
                                items = uiState.trainingPlans,
                                key = { it.id }
                            ) { plan ->
                                TrainingPlanCard(
                                    trainingPlan = plan,
                                    onCardClick = onPlanClick,
                                    onLikeClick = viewModel::toggleLike,
                                    onFavoriteClick = viewModel::toggleFavorite,
                                    onEditClick = onEditPlanClick,
                                    onDeleteClick = viewModel::showDeleteDialog,
                                    showOwnerActions = true
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

            if (uiState.showDeleteDialog && uiState.planToDelete != null) {
                DeleteConfirmationDialog(
                    planName = uiState.planToDelete!!.name,
                    onConfirm = {
                        viewModel.deletePlan()
                    },
                    onDismiss = {
                        viewModel.hideDeleteDialog()
                    }
                )
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    planName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = "Usuń plan treningowy",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Czy na pewno chcesz usunąć plan \"$planName\"?\n\nTa operacja nie może zostać cofnięta.",
                fontSize = 14.sp
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(
                    text = "Usuń",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Anuluj",
                    color = getOnSurfaceColor()
                )
            }
        },
        containerColor = getSurfaceColor(),
        textContentColor = getOnSurfaceColor()
    )
}