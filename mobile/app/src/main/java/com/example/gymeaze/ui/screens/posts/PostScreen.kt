package com.example.gymeaze.ui.screens.posts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.gymeaze.ui.screens.ExercisesMenuScreen
import com.example.gymeaze.ui.screens.exercises.guest.ExercisesOverviewScreen
import com.example.gymeaze.ui.screens.posts.guest.PostsOverviewScreen
import com.example.gymeaze.ui.screens.posts.loggedIn.CreatePostScreen
import com.example.gymeaze.ui.screens.posts.loggedIn.MyPosts
import com.example.gymeaze.ui.screens.posts.loggedIn.PostsFollowedUsers
import com.example.gymeaze.ui.screens.posts.loggedIn.PostsMenuScreen
import com.example.gymeaze.ui.screens.posts.loggedIn.SeeAllPostsScreen
import com.example.gymeaze.viewmodel.posts.PostsViewModel

@Composable
fun PostScreen(
    onSettingsClick: () -> Unit,
    isUserLoggedIn: Boolean,
    createPostViewModel: PostsViewModel,
    setShowBottomBar: (Boolean) -> Unit
) {
    if (!isUserLoggedIn) {
        PostsOverviewScreen()
        return
    }

    var postSection by remember { mutableStateOf("menu") }

    LaunchedEffect(postSection) {
        setShowBottomBar(postSection == "menu")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when (postSection) {
            "menu" -> PostsMenuScreen(
                onSettingsClick = onSettingsClick,
                onCreatePostClick = { postSection = "create" },
                onMyPostClick = { postSection = "myPosts" },
                onBrowsePostClick = { postSection = "browse" },
                onFollowedUsersPosts = { postSection = "followed" }
            )
            "create" -> CreatePostScreen(
                viewModel = createPostViewModel,
                onBackClick = { postSection = "menu" },
                onCreated = { postSection = "menu" }
            )
            "myPosts" -> MyPosts(
                onBackClick = { postSection = "menu" }
            )
            "browse" -> SeeAllPostsScreen(
                onBackClick = { postSection = "menu" }
            )
            "followed" -> PostsFollowedUsers(
                onBackClick = { postSection = "menu" }
            )
        }
    }
}