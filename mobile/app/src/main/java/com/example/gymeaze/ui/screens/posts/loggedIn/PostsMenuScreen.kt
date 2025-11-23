package com.example.gymeaze.ui.screens.posts.loggedIn

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.R
import com.example.gymeaze.ui.components.BackgroundWithDotsAndCircles
import com.example.gymeaze.ui.components.GymEazeTopAppBar
import com.example.gymeaze.ui.screens.SectionButton
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor

@Composable
fun PostsMenuScreen(
    onSettingsClick: () -> Unit,
    onCreatePostClick: () -> Unit = {},
    onMyPostClick: () -> Unit = {},
    onBrowsePostClick: () -> Unit = {},
    onFollowedUsersPosts: () -> Unit ={}
) {
    var selectedRoute by remember { mutableStateOf("community") }

    Scaffold(
        topBar = {
            GymEazeTopAppBar(onSettingsClick = onSettingsClick)
        },
        bottomBar = {
            /*
            GymEazeBottomNavigationBar(
                selectedRoute = selectedRoute,
                onItemSelected = { route -> selectedRoute = route }
            )*/
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
                    text = stringResource(R.string.community),
                    modifier = Modifier.padding(top = 32.dp, bottom = 16.dp),
                    color = getOnSurfaceColor(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.join_share_inspire),
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
                            title = stringResource(R.string.new_post),
                            subtitle = stringResource(R.string.posts_menu_create_subtitle),
                            imageRes = R.drawable.created_posts,
                            backgroundColor = getSurfaceColor(),
                            innerBackgroundColor = getPrimaryColor().copy(alpha = 0.3f),
                            innerBorderColor= getPrimaryColor(),
                            modifier = Modifier.weight(1f),
                            onClick = onCreatePostClick
                        )

                        SectionButton(
                            title = stringResource(R.string.posts_menu_my_title),
                            subtitle = stringResource(R.string.posts_menu_my_subtitle),
                            imageRes = R.drawable.my_posts,
                            backgroundColor = getSurfaceColor(),
                            modifier = Modifier.weight(1f),
                            onClick = onMyPostClick
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SectionButton(
                            title = stringResource(R.string.posts_menu_browse_title),
                            subtitle = stringResource(R.string.posts_menu_browse_subtitle),
                            imageRes = R.drawable.search_posts,
                            backgroundColor = getSurfaceColor(),
                            modifier = Modifier.weight(1f),
                            onClick = onBrowsePostClick
                        )

                        SectionButton(
                            title = stringResource(R.string.posts_menu_followed_title),
                            subtitle = stringResource(R.string.posts_menu_followed_subtitle),
                            imageRes = R.drawable.followed_posts,
                            backgroundColor = getSurfaceColor(),
                            modifier = Modifier.weight(1f),
                            onClick = onFollowedUsersPosts
                        )
                    }
                }
            }
        }
    }
}