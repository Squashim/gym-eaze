package com.example.gymeaze.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getInputBorderColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor
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
                        text = "Wybierz ćwiczenia",
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
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(getOnSurfaceColor().copy(alpha = 0.1f))
            ) {
                AsyncImage(
                    model = exercise.image,
                    contentDescription = exercise.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(20.dp)
                        .background(
                            when (exercise.advancedLevel) {
                                "easy" -> Color(0xFF4CAF50).copy(alpha = 0.9f)
                                "medium" -> Color(0xFFFF9800).copy(alpha = 0.9f)
                                "hard" -> Color(0xFFF44336).copy(alpha = 0.9f)
                                else -> getOnSurfaceColor().copy(alpha = 0.2f)
                            },
                            RoundedCornerShape(4.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val difficultyText = when (exercise.advancedLevel) {
                        "easy" -> "P"
                        "medium" -> "Ś"
                        "hard" -> "Z"
                        else -> "?"
                    }
                    Text(
                        text = difficultyText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))


            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = exercise.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Autor: ${exercise.author.username}",
                        fontSize = 12.sp,
                        color = getOnSurfaceSecondaryColor()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))


                if (exercise.muscleGroup.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(exercise.muscleGroup.take(2)) { muscleGroup ->
                            Surface(
                                color = getPrimaryColor().copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = muscleGroup.name,
                                    fontSize = 10.sp,
                                    color = getPrimaryColor(),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        if (exercise.muscleGroup.size > 2) {
                            item {
                                Surface(
                                    color = getOnSurfaceColor().copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "+${exercise.muscleGroup.size - 2}",
                                        fontSize = 10.sp,
                                        color = getOnSurfaceSecondaryColor(),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }


            IconButton(
                onClick = onSelect,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Wybierz ćwiczenie",
                    tint = getPrimaryColor()
                )
            }
        }
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
            text = "Spróbuj zmienić wyszukiwanie lub sprawdź połączenie z internetem",
            fontSize = 14.sp,
            color = getOnSurfaceSecondaryColor(),
            textAlign = TextAlign.Center
        )
    }
}