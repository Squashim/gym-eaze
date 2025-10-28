package com.example.gymeaze.ui.screens.profile

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.data.models.PostUi
import com.example.gymeaze.ui.components.ReportDialog
import com.example.gymeaze.ui.screens.comments.CommentsPanel
import com.example.gymeaze.ui.screens.posts.components.MediaFullScreenDialog
import com.example.gymeaze.ui.screens.posts.components.PostCard
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor
import com.example.gymeaze.viewmodel.AuthViewModel
import com.example.gymeaze.viewmodel.ReportItemType
import com.example.gymeaze.viewmodel.ReportViewModel
import com.example.gymeaze.viewmodel.posts.PostsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPostsSection(
    username: String,
    onBrowsePostClick: () -> Unit,
    postsViewModel: PostsViewModel = viewModel(),
    currentLoggedUsername: String = "",
    isUserLoggedIn: Boolean = false,
    onEditPost: (PostUi) -> Unit = {},
    onDeletePost: (PostUi) -> Unit = {},
    onHashtagClick: (String) -> Unit = {},
    onCommentClick: (String) -> Unit = {},
    onReportClick: (String) -> Unit = {},
    onToggleLike: (String) -> Unit = {}
) {
    val isLoading by postsViewModel.isLoading.collectAsState(initial = false)
    val userPosts by postsViewModel.userPostsByUsername.collectAsState()

    var fullScreenMedia by remember { mutableStateOf<Pair<PostUi, Boolean>?>(null) }
    var showCommentsSheet by remember { mutableStateOf(false) }
    var selectedPostId by remember { mutableStateOf<String?>(null) }
    var showReportDialog by remember { mutableStateOf(false) }
    var itemToReport by remember { mutableStateOf<String?>(null) }

    val reportViewModel: ReportViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    LaunchedEffect(Unit) {
        postsViewModel.fetchUserPostsByUsername(username, refresh = true)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth())
        {
            Spacer(modifier = Modifier.height(8.dp))

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
            } else if (userPosts.isEmpty()) {
                EmptySectionMessage(
                    message = "$username nie ma jeszcze żadnych postów",
                    icon = Icons.Default.Comment
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    userPosts.forEach { post ->
                        PostCard(
                            post = post,
                            currentUsername = currentLoggedUsername,
                            onEditClick = onEditPost,
                            onDeleteClick = onDeletePost,
                            onHashtagClick = onHashtagClick,
                            onImageClick = {
                                fullScreenMedia = post to false
                            },
                            onVideoClick = {
                                fullScreenMedia = post to true
                            },
                            onToggleLike = { postId ->
                                if (isUserLoggedIn) {
                                    Log.d("UserPostsSection", "Toggling like for post: $postId")
                                    postsViewModel.toggleLike(postId)
                                } else {
                                    Log.d("UserPostsSection", "User not logged in")
                                }
                            },
                            onToggleSeeMore = { true },
                            onCommentClick = {
                                if (isUserLoggedIn) {
                                    selectedPostId = post.id
                                    showCommentsSheet = true
                                }
                            },
                            onReportClick = {
                                if (isUserLoggedIn) {
                                    itemToReport = post.id
                                    showReportDialog = true
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    fullScreenMedia?.let { (post, isVideo) ->
        MediaFullScreenDialog(
            post = post,
            showVideo = isVideo,
            onClose = { fullScreenMedia = null }
        )
    }

    if (showCommentsSheet && selectedPostId != null && isUserLoggedIn) {
        val bottomSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        ModalBottomSheet(
            onDismissRequest = { showCommentsSheet = false },
            sheetState = bottomSheetState,
            modifier = Modifier.fillMaxHeight(),
            containerColor = getSurfaceColor()
        ) {
            CommentsPanel(
                postId = selectedPostId!!,
                initialCommentsCount = userPosts.find { it.id == selectedPostId }?.commentsCount ?: 0,
                onClose = {
                    showCommentsSheet = false
                    postsViewModel.fetchUserPostsByUsername(username, refresh = true)
                },
                currentUsername = currentLoggedUsername,
                reportViewModel = reportViewModel,
                onReportComment = { commentId ->
                    itemToReport = commentId
                    showReportDialog = true
                },
                isUserLoggedIn = isUserLoggedIn
            )
        }
    }

    if (showReportDialog && itemToReport != null && isUserLoggedIn) {
        ReportDialog(
            itemId = itemToReport!!,
            itemType = ReportItemType.POST,
            onDismiss = {
                showReportDialog = false
                itemToReport = null
                reportViewModel.resetState()
            },
            viewModel = reportViewModel
        )
    }
}
