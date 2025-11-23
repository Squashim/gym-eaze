package com.example.gymeaze.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.R
import com.example.gymeaze.ui.components.BackgroundWithDotsAndCircles
import com.example.gymeaze.ui.components.GymEazeBottomNavigationBar
import com.example.gymeaze.ui.components.GymEazeTopAppBar
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor
import com.example.gymeaze.ui.theme.getSurfaceVariantColor

@Composable
fun ExercisesMenuScreen(
    onSettingsClick: () -> Unit,
    onCreateExerciseClick: () -> Unit = {},
    onMyExercisesClick: () -> Unit = {},
    onBrowseExercisesClick: () -> Unit = {},
    onFavoriteExercisesClick: () -> Unit = {},
) {
    var selectedRoute by remember { mutableStateOf("exercises") }

    Scaffold(
        topBar = {
            GymEazeTopAppBar(onSettingsClick = onSettingsClick)
        },
        bottomBar = {
            GymEazeBottomNavigationBar(
                selectedRoute = selectedRoute,
                onItemSelected = { route -> selectedRoute = route }
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.exercises),
                    modifier = Modifier.padding(top = 32.dp, bottom = 16.dp),
                    color = getOnSurfaceColor(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.join_our_community),
                    modifier = Modifier.padding(bottom = 48.dp),
                    color = getOnSurfaceColor().copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SectionButton(
                            title = stringResource(R.string.exercises_menu_create_title),
                            subtitle = stringResource(R.string.exercises_menu_create_subtitle),
                            imageRes = R.drawable.create_exercise,
                            backgroundColor = getSurfaceColor(),
                            innerBackgroundColor = getPrimaryColor().copy(alpha = 0.3f),
                            innerBorderColor= getPrimaryColor(),
                            modifier = Modifier.weight(1f),
                            onClick = onCreateExerciseClick
                        )

                        SectionButton(
                            title = stringResource(R.string.exercises_menu_my_title),
                            subtitle = stringResource(R.string.exercises_menu_my_subtitle),
                            imageRes = R.drawable.my_exercises,
                            backgroundColor = getSurfaceColor(),
                            modifier = Modifier.weight(1f),
                            onClick = onMyExercisesClick
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SectionButton(
                            title = stringResource(R.string.exercises_menu_browse_title),
                            subtitle = stringResource(R.string.exercises_menu_browse_subtitle),
                            imageRes = R.drawable.overview_exercises,
                            backgroundColor = getSurfaceColor(),
                            modifier = Modifier.weight(1f),
                            onClick = onBrowseExercisesClick
                        )

                        SectionButton(
                            title = stringResource(R.string.exercises_menu_favorites_title),
                            subtitle = stringResource(R.string.exercises_menu_favorites_subtitle),
                            imageRes = R.drawable.favorites_exercises,
                            backgroundColor = getSurfaceColor(),
                            modifier = Modifier.weight(1f),
                            onClick = onFavoriteExercisesClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionButton(
    title: String,
    subtitle: String,
    imageRes: Int,
    backgroundColor: Color,
    borderColor: Color = getOnSurfaceSecondaryColor().copy(alpha = 0.3f),
    innerBackgroundColor: Color = getSurfaceVariantColor(),
    innerBorderColor: Color = getOnSurfaceSecondaryColor().copy(alpha = 0.3f),
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(220.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .height(150.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = 10.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = innerBackgroundColor
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    innerBorderColor
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = title,
                        color = getOnSurfaceColor(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = subtitle,
                        color = getOnSurfaceColor(),
                        fontSize = 11.sp,
                        lineHeight = 13.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 12.dp, y = -6.dp)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}