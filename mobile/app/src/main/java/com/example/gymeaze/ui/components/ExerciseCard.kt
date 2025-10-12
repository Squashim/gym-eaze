package com.example.gymeaze.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.ui.theme.getErrorColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor
import com.example.gymeaze.ui.theme.getYellowColor
import com.example.gymeaze.ui.theme.getOnYellowColor

@Composable
fun ExerciseCard(
    exercise: Exercise,
    isLiked: Boolean,
    isFavorite: Boolean,
    isCurrentUserExercise: Boolean,
    onLikeClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onExerciseClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                width = 1.dp,
                color = getOnSurfaceColor().copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
        onClick = { onExerciseClick(exercise.id) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(getOnSurfaceColor().copy(alpha = 0.1f))
            ) {
                AsyncImage(
                    model = exercise.image,
                    contentDescription = exercise.name,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )

                if (isCurrentUserExercise) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    getOnSurfaceColor().copy(alpha = 0.8f),
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { onEditClick(exercise.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edytuj",
                                tint = getSurfaceColor(),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    Color(0xFFF44336).copy(alpha = 0.9f),
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { onDeleteClick(exercise.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Usuń",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = exercise.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = getOnSurfaceColor(),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Autor: ${exercise.author.username}",
                fontSize = 12.sp,
                color = getOnSurfaceSecondaryColor(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (exercise.muscleGroup.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(exercise.muscleGroup.take(3)) { muscleGroup ->
                        MuscleGroupTag(
                            name = muscleGroup.name
                        )
                    }
                    if (exercise.muscleGroup.size > 3) {
                        item {
                            Text(
                                text = "+${exercise.muscleGroup.size - 3}",
                                fontSize = 10.sp,
                                color = getOnSurfaceSecondaryColor(),
                                modifier = Modifier
                                    .background(
                                        getOnSurfaceColor().copy(alpha = 0.1f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onLikeClick(exercise.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isLiked) Icons.Default.ThumbUpAlt else Icons.Default.ThumbUpOffAlt,
                                contentDescription = "Polub",
                                tint = if (isLiked) getPrimaryColor() else getOnSurfaceSecondaryColor(),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = exercise.likes.toString(),
                            fontSize = 14.sp,
                            color = getOnSurfaceColor()
                        )
                    }
                    
                    if (!isCurrentUserExercise) {
                        IconButton(
                            onClick = { onFavoriteClick(exercise.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Dodaj do ulubionych",
                                tint = if (isFavorite) Color(0xFFE91E63) else getOnSurfaceSecondaryColor(),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                DifficultyBadgeExercise(
                    difficulty = exercise.advancedLevel,
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun DifficultyBadgeExercise(
    difficulty: String,
    modifier: Modifier = Modifier
) {
    val (text, backgroundColor, textColor) = when (difficulty) {
        AdvancedLevel.NONE -> Triple("Nie wybrano", getOnSurfaceColor().copy(alpha = 0.1f), getOnSurfaceSecondaryColor())
        AdvancedLevel.EASY -> Triple("Początkujący", getPrimaryColor().copy(alpha = 0.2f), getPrimaryColor())
        AdvancedLevel.MEDIUM -> Triple("Średniozaawansowany", getYellowColor(), getOnYellowColor())
        AdvancedLevel.HARD -> Triple("Zaawansowany", getErrorColor().copy(alpha = 0.2f), getErrorColor())
        else -> Triple("?", getOnSurfaceColor().copy(alpha = 0.1f), getOnSurfaceSecondaryColor())
    }

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}


@Composable
private fun MuscleGroupTag(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = name,
        fontSize = 10.sp,
        color = getOnSurfaceColor(),
        modifier = modifier
            .background(
                getOnSurfaceColor().copy(alpha = 0.1f),
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}