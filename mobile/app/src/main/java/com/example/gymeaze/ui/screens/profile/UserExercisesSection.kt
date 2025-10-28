package com.example.gymeaze.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.ui.components.ExerciseCard
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor
import com.example.gymeaze.viewmodel.ExercisesViewModel

@Composable
fun UserExercisesSection(
    username: String,
    userExercisesViewModel: ExercisesViewModel,
    onExerciseClick: (String) -> Unit,
    onBrowseExercisesClick: () -> Unit,
    isOwnProfile: Boolean
) {
    val uiState by userExercisesViewModel.uiState.collectAsState()
    val userExercises = uiState.exercises
    val isLoading = uiState.isLoading
    val favoriteExercises by userExercisesViewModel.favoriteExercises.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = getPrimaryColor(),
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else if (userExercises.isEmpty()) {
                EmptySectionMessage(
                    message = if (isOwnProfile)
                        "Nie masz jeszcze żadnych ćwiczeń"
                    else
                        "$username nie ma jeszcze żadnych ćwiczeń",
                    icon = Icons.Default.FitnessCenter
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    userExercises.forEach { exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            isLiked = userExercisesViewModel.isExerciseLiked(exercise.id),
                            isFavorite = favoriteExercises.contains(exercise.id),
                            isCurrentUserExercise = userExercisesViewModel.isCurrentUserExercise(exercise.author.username),
                            onLikeClick = { exerciseId ->
                                userExercisesViewModel.toggleLike(exerciseId) { success, message ->

                                }
                            },
                            onFavoriteClick = { exerciseId ->
                                userExercisesViewModel.toggleFavorite(exerciseId) { success, message ->

                                }
                            },
                            onEditClick = { exerciseId ->

                            },
                            onDeleteClick = { exerciseId ->

                            },
                            onExerciseClick = { exerciseId ->
                                onExerciseClick(exerciseId)
                            }
                        )
                    }
                }
            }
        }
    }
}