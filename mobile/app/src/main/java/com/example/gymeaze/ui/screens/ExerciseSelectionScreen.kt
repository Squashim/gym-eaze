package com.example.gymeaze.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.ui.components.ExerciseCard
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.ExercisesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSelectionScreen(
    dayIndex: Int,
    onBackClick: () -> Unit,
    onExerciseSelected: (Exercise) -> Unit,
    viewModel: ExercisesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Wybierz ćwiczenie",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wstecz",
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

            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::updateSearchQuery,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = getPrimaryColor())
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
                            text = uiState.error ?: "Wystąpił błąd",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.refreshExercises() }) {
                            Text("Spróbuj ponownie")
                        }
                    }
                }
                uiState.exercises.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Nie znaleziono ćwiczeń",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = getOnSurfaceColor()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Spróbuj zmienić kryteria wyszukiwania",
                            fontSize = 14.sp,
                            color = getOnSurfaceSecondaryColor()
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.exercises) { exercise ->
                            ExerciseSelectionCard(
                                exercise = exercise,
                                onSelect = { onExerciseSelected(exercise) }
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

@Composable
private fun ExerciseSelectionCard(
    exercise: Exercise,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
        onClick = onSelect
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            coil.compose.AsyncImage(
                model = exercise.image,
                contentDescription = exercise.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = exercise.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = getOnSurfaceColor(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Autor: ${exercise.author.username}",
                    fontSize = 12.sp,
                    color = getOnSurfaceSecondaryColor()
                )

                if (exercise.muscleGroup.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = exercise.muscleGroup.joinToString(", ") { it.name },
                        fontSize = 12.sp,
                        color = getOnSurfaceSecondaryColor(),
                        maxLines = 1
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Dodaj ćwiczenie",
                tint = getPrimaryColor(),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}