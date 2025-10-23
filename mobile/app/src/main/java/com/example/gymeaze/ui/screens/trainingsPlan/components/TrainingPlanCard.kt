package com.example.gymeaze.ui.screens.trainingsPlan.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.data.models.TrainingPlan
import com.example.gymeaze.data.models.User
import com.example.gymeaze.data.repository.AuthRepository
import com.example.gymeaze.ui.theme.getErrorColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getOnYellowColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor
import com.example.gymeaze.ui.theme.getYellowColor
import com.example.gymeaze.util.GlobalFavoriteState
import com.example.gymeaze.util.formatToRelativeTime

@Composable
fun TrainingPlanCard(
    trainingPlan: TrainingPlan,
    onCardClick: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit = {},
    onEditClick: ((String) -> Unit)? = null,
    onDeleteClick: ((TrainingPlan) -> Unit)? = null,
    showOwnerActions: Boolean = false,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val favoriteIds by GlobalFavoriteState.getInstance(context).favoriteTrainingPlanIds.collectAsState()
    val isFavoriteGlobal = favoriteIds.contains(trainingPlan.id)

    val authRepository = AuthRepository(context)
    val currentUser by produceState<User?>(initialValue = null) {
        value = try {
            authRepository.getCurrentUser().getOrNull()
        } catch (e: Exception) {
            null
        }
    }

    // Sprawdź czy plan należy do aktualnego użytkownika
    val isOwner = currentUser?._id == trainingPlan.author?.id

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
        onClick = {
            try {
                onCardClick(trainingPlan.id)
            } catch (e: Exception) {
            }
        }
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
                    model = trainingPlan.image,
                    contentDescription = trainingPlan.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                trainingPlan.daysCount?.let { daysCount ->
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${daysCount.total} dni",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (showOwnerActions && isOwner && (onEditClick != null || onDeleteClick != null)) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        onEditClick?.let { editClick ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        getOnSurfaceColor().copy(alpha = 0.8f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { onEditClick(trainingPlan.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edytuj plan",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        onDeleteClick?.let { deleteClick ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        Color(0xFFF44336).copy(alpha = 0.9f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { onDeleteClick(trainingPlan) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Usuń plan",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = trainingPlan.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = getOnSurfaceColor(),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Autor: ${trainingPlan.author?.username}",
                fontSize = 12.sp,
                color = getOnSurfaceSecondaryColor(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Dodano: ${trainingPlan.createdAt.formatToRelativeTime()}",
                fontSize = 10.sp,
                color = getOnSurfaceSecondaryColor()
            )
            Spacer(modifier = Modifier.height(4.dp))

            GoalChip(goal = trainingPlan.goal)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    trainingPlan.totalExercises?.let { totalExercises ->
                        Text(
                            text = "Liczba użytych ćwiczeń: $totalExercises",
                            fontSize = 10.sp,
                            color = getOnSurfaceSecondaryColor()
                        )
                    }
                    trainingPlan.totalDuration?.let { totalDuration ->
                        Text(
                            text = "Czas trwania całego treningu: ${formatDuration(totalDuration)}",
                            fontSize = 10.sp,
                            color = getOnSurfaceSecondaryColor()
                        )
                    }
                }

            }

            Spacer(modifier = Modifier.height(8.dp))

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
                            onClick = {
                                try {
                                    onLikeClick(trainingPlan.id)
                                } catch (e: Exception) {
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (trainingPlan.likes > 0) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                                contentDescription = "Polub",
                                tint = getPrimaryColor(),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = trainingPlan.likes.toString(),
                            fontSize = 14.sp,
                            color = getOnSurfaceColor()
                        )
                    }

                    // DODAJ WARUNEK: pokazuj ikonkę serduszka tylko jeśli to NIE jest plan użytkownika
                    if (!isOwner) {
                        IconButton(
                            onClick = {
                                onFavoriteClick(trainingPlan.id)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavoriteGlobal) {
                                    Icons.Default.Favorite
                                } else {
                                    Icons.Default.FavoriteBorder
                                },
                                contentDescription = if (isFavoriteGlobal) {
                                    "Usuń z ulubionych"
                                } else {
                                    "Dodaj do ulubionych"
                                },
                                tint = if (isFavoriteGlobal) {
                                    Color(0xFFE91E63)
                                } else {
                                    getOnSurfaceSecondaryColor()
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                DifficultyBadge(
                    difficulty = trainingPlan.advancedLevel,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun DifficultyBadge(
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
private fun GoalChip(
    goal: String,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier,
        color = getPrimaryColor().copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = goal,
            fontSize = 10.sp,
            color = getPrimaryColor(),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val hours = minutes / 60
    val remainingMinutes = minutes % 60

    return when {
        hours == 0 -> "${minutes}min"
        remainingMinutes == 0 -> "${hours}h"
        else -> "${hours}h ${remainingMinutes}min"
    }
}