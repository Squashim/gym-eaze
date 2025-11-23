package com.example.gymeaze.ui.screens.posts.loggedIn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.data.models.PostUi
import com.example.gymeaze.ui.components.LoadingScreen
import com.example.gymeaze.ui.screens.posts.components.DeletePostDialog
import com.example.gymeaze.ui.screens.posts.components.SeeAllPostsContent
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor
import com.example.gymeaze.viewmodel.AuthViewModel
import com.example.gymeaze.viewmodel.posts.PostsViewModel
import com.example.gymeaze.R

@Composable
fun SeeAllPostsScreen(
    onBackClick: () -> Unit = {},
    onUserClick: (String) -> Unit = {}
) {
    val viewModel: PostsViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val currentUsername = currentUser?.username ?: ""
    var editingPost by remember { mutableStateOf<PostUi?>(null) }
    var deletingPost by remember { mutableStateOf<PostUi?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }


    val onEditClick: (PostUi) -> Unit = { post ->
        editingPost = post
    }

    val onDeleteClick: (PostUi) -> Unit = { post ->
        deletingPost = post
        showDeleteDialog = true
    }

    LaunchedEffect(Unit) {
        val result = authViewModel.getCurrentUser()
        result.onSuccess { user ->
            viewModel.setCurrentUser(user)
            viewModel.fetchPosts(refresh = true)
        }
    }

    Box(Modifier.fillMaxSize()) {

        if (showDeleteDialog && deletingPost != null) {
            DeletePostDialog(
                onConfirm = {
                    viewModel.deletePost(deletingPost!!.id)
                    showDeleteDialog = false
                    deletingPost = null
                },
                onDismiss = {
                    showDeleteDialog = false
                    deletingPost = null
                }
            )
        }

        if (editingPost != null) {
            EditPostScreen(
                viewModel = viewModel,
                postToEdit = editingPost!!,
                onBackClick = { editingPost = null },
                onEdited = { editingPost = null },
                onRefreshPosts = { viewModel.refreshPosts() }
            )
        } else {
            SeeAllPostsContent(
                posts = posts,
                currentUsername = currentUsername,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                onBackClick = onBackClick,
                onSearchHashtags = { hashtags -> viewModel.searchByHashtags(hashtags) },
                onClearSearch = { viewModel.clearSearch() },
                onLoadMore = { viewModel.loadMore() },
                isLoading = isLoading,
                onRefresh = { viewModel.refreshPosts() },
                isRefreshing = isRefreshing,
                onToggleLike = { postId -> viewModel.toggleLike(postId) },
                onToggleSeeMore = { true },
                onRefreshPosts = { viewModel.refreshPosts() },
                topBarTitle = "Wszystkie posty",
                onUserClick = onUserClick
            )
        }

        if (isLoading && posts.isEmpty()) {
            LoadingScreen()
        }

        error?.let {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(32.dp),
                    colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.error_occured),
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it,
                            color = getOnSurfaceColor(),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.clearError() },
                            colors = ButtonDefaults.buttonColors(containerColor = getPrimaryColor())
                        ) {
                            Text(
                                text = stringResource(R.string.ok)
                            )
                        }
                    }
                }
            }
        }
    }
}