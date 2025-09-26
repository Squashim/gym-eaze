package com.example.gymeaze.ui.screens.posts

import androidx.compose.runtime.Composable
import com.example.gymeaze.ui.screens.ExercisesMenuScreen
import com.example.gymeaze.ui.screens.exercises.guest.ExercisesOverviewScreen
import com.example.gymeaze.ui.screens.posts.guest.PostsOverviewScreen
import com.example.gymeaze.ui.screens.posts.loggedIn.PostsMenuScreen

@Composable
fun PostScreen(
    onSettingsClick: () -> Unit,
    isUserLoggedIn: Boolean,
    onCreatePostClick: () -> Unit = {},
    onBrowsePostClick: () -> Unit = {},
    onFollowedUsersPosts: () -> Unit ={},
    onMyPosts: () -> Unit = {}
) {
    if (isUserLoggedIn){
        PostsMenuScreen(onSettingsClick = onSettingsClick, onCreatePostClick = onCreatePostClick, onBrowsePostClick = onBrowsePostClick, onFollowedUsersPosts = onFollowedUsersPosts, onMyPostClick = onMyPosts)
    } else {
        PostsOverviewScreen()

    }
}