package com.example.gymeaze.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor
import com.example.gymeaze.ui.theme.getYellowColor
import com.example.gymeaze.ui.theme.getOnYellowColor

@Composable
fun ExerciseCard(
    exercise: Exercise,
    onLikeClick: (String) -> Unit,
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

                DifficultyBadge(
                    difficulty = exercise.advancedLevel,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onLikeClick(exercise.id) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (exercise.likes > 0) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Polub",
                            tint = getPrimaryColor(),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = exercise.likes.toString(),
                        fontSize = 14.sp,
                        color = getOnSurfaceColor()
                    )
                }

                if (exercise.equipment.isNotEmpty()) {
                    Text(
                        text = exercise.equipment.first().name,
                        fontSize = 10.sp,
                        color = getOnSurfaceSecondaryColor(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
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
        AdvancedLevel.NONE -> Triple("B", getOnSurfaceColor().copy(alpha = 0.1f), getOnSurfaceSecondaryColor())
        AdvancedLevel.EASY -> Triple("P", getPrimaryColor().copy(alpha = 0.2f), getPrimaryColor())
        AdvancedLevel.MEDIUM -> Triple("Ś", getYellowColor(), getOnYellowColor())
        AdvancedLevel.HARD -> Triple("Z", getOnSurfaceColor().copy(alpha = 0.2f), getOnSurfaceColor())
        else -> Triple("?", getOnSurfaceColor().copy(alpha = 0.1f), getOnSurfaceSecondaryColor())
    }

    Box(
        modifier = modifier
            .size(24.dp)
            .background(backgroundColor, RoundedCornerShape(6.dp)),
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