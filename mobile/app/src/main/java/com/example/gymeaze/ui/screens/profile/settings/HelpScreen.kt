package com.example.gymeaze.ui.screens.profile.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.R
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onReturnClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.how_to_earn_badges),
                        color = getOnSurfaceColor(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onReturnClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = getOnSurfaceColor()
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = getBackgroundColor()
                )
            )
        },
        bottomBar = {},
        containerColor = getBackgroundColor()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(getBackgroundColor())
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.how_to_earn_badges_title),
                    color = getOnSurfaceColor(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = stringResource(R.string.how_to_earn_badges_description),
                    color = getOnSurfaceSecondaryColor(),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }


            HelpSection(
                title = stringResource(R.string.activity_and_usage),
                description = stringResource(R.string.activity_and_usage_description),
                items = listOf(
                    stringResource(R.string.first_activity_badge),
                    stringResource(R.string.workout_frequency_badges),
                    stringResource(R.string.consistency_badges)
                )

            )

            HelpSection(
                title = stringResource(R.string.training_with_detection),
                items = listOf(
                    stringResource(R.string.training_with_detection_example),
                )
            )

            HelpSection(
                title = stringResource(R.string.social_activity),
                description = stringResource(R.string.social_activity_description),
                items = listOf(
                    stringResource(R.string.community_badges),
                    stringResource(R.string.sharing_badges),
                )
            )

            HelpSection(
                title = stringResource(R.string.exercises_activity),
                description = stringResource(R.string.exercises_activity_description),
                items = listOf(
                    stringResource(R.string.milestone_exercises),
                    stringResource(R.string.improvement_exercises)
                )
            )

            HelpSection(
                title = stringResource(R.string.exploration_badges),
                items = listOf(
                    stringResource(R.string.feature_discovery_badges),
                    stringResource(R.string.profile_completion_badges),
                )
            )

            HelpSection(
                title = stringResource(R.string.popularity_profile),
                description = stringResource(R.string.popularity_profile_description),
                items = listOf(
                    stringResource(R.string.milestone_profile),
                    stringResource(R.string.second_milestone_profile),
                )
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = getPrimaryColor().copy(alpha = 0.1f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = getPrimaryColor(),
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.tips_title),
                            color = getOnSurfaceColor(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = stringResource(R.string.badges_tips),
                        color = getOnSurfaceSecondaryColor(),
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun HelpSection(
    title: String,
    items: List<String>,
    description: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            color = getOnSurfaceColor(),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        description?.let {
            Text(
                text = it,
                color = getOnSurfaceSecondaryColor(),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "•",
                    color = getPrimaryColor(),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(end = 8.dp, top = 2.dp)
                )

                Text(
                    text = item,
                    color = getOnSurfaceSecondaryColor(),
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
