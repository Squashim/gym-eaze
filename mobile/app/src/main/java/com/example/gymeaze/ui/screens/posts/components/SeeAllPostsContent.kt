package com.example.gymeaze.ui.screens.posts.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.data.models.PostUi
import com.example.gymeaze.ui.components.BackgroundWithDotsAndCircles
import com.example.gymeaze.ui.components.ReportDialog
import com.example.gymeaze.ui.screens.comments.CommentsPanel
import com.example.gymeaze.ui.theme.LocalDarkTheme
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor
import com.example.gymeaze.viewmodel.ReportItemType
import com.example.gymeaze.viewmodel.ReportViewModel
import com.example.gymeaze.viewmodel.posts.PostsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeeAllPostsContent(
    posts: List<PostUi>,
    currentUsername: String,
    onEditClick: (PostUi) -> Unit,
    onDeleteClick: (PostUi) -> Unit,
    onBackClick: () -> Unit,
    onSearchHashtags: (List<String>) -> Unit,
    onClearSearch: () -> Unit,
    onLoadMore: () -> Unit,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    onToggleLike: (String) -> Unit,
    onToggleSeeMore: (String) -> Boolean,
    searchEnabled: Boolean = true,
    showLogoTopBar: Boolean = false,
    onRefreshPosts: () -> Unit,
    topBarTitle: String,
) {
    var selectedHashtags by remember { mutableStateOf<List<String>>(emptyList()) }
    var hashtagInput by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var fullScreenMedia by remember { mutableStateOf<Pair<PostUi, Boolean>?>(null) }

    var showCommentsSheet by remember { mutableStateOf(false) }
    var selectedPostId by remember { mutableStateOf<String?>(null) }

    var showReportDialog by remember { mutableStateOf(false) }
    var itemToReport by remember { mutableStateOf<String?>(null) }
    var reportItemType by remember { mutableStateOf<ReportItemType?>(null) }

    val listState = rememberLazyListState()
    val isDarkTheme = LocalDarkTheme.current

    val viewModel: PostsViewModel = viewModel()
    val reportViewModel: ReportViewModel = viewModel()

    Scaffold(
        topBar = {
            if (showLogoTopBar) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(
                                    id = if(isDarkTheme){
                                        com.example.gymeaze.R.drawable.gym_eaze_logo_light
                                    } else {
                                        com.example.gymeaze.R.drawable.gym_eaze_logo_dark
                                    }),
                                contentDescription = stringResource(com.example.gymeaze.R.string.logo),
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    },
                    actions = {
                        if (searchEnabled) {
                            IconButton(
                                onClick = {
                                    isSearchActive = !isSearchActive
                                    if (!isSearchActive) {
                                        selectedHashtags = emptyList()
                                        hashtagInput = ""
                                        onClearSearch()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                    contentDescription = if (isSearchActive) "Zamknij wyszukiwanie" else "Szukaj",
                                    tint = getOnSurfaceColor()
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = getBackgroundColor()
                    )
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            text = topBarTitle,
                            color = getOnSurfaceColor(),
                            fontWeight = FontWeight.ExtraBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Wróć",
                                tint = getOnSurfaceColor()
                            )
                        }
                    },
                    actions = {
                        if (searchEnabled) {
                            IconButton(
                                onClick = {
                                    isSearchActive = !isSearchActive
                                    if (!isSearchActive) {
                                        selectedHashtags = emptyList()
                                        hashtagInput = ""
                                        onClearSearch()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                    contentDescription = if (isSearchActive) "Zamknij wyszukiwanie" else "Szukaj",
                                    tint = getOnSurfaceColor()
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = getBackgroundColor()
                    )
                )
            }
        },
        containerColor = getBackgroundColor()
    ){ paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                BackgroundWithDotsAndCircles(modifier = Modifier.fillMaxSize())

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {

                    if (isSearchActive) {
                        Box(
                            modifier = Modifier.padding(
                                top = paddingValues.calculateTopPadding()
                            )
                        ) {
                            EnhancedSearchBar(
                                selectedHashtags = selectedHashtags,
                                hashtagInput = hashtagInput,
                                onHashtagInputChange = { hashtagInput = it },
                                onAddHashtag = { hashtag ->
                                    if (hashtag.isNotBlank() && !selectedHashtags.contains(hashtag)) {
                                        selectedHashtags = selectedHashtags + hashtag
                                        hashtagInput = ""
                                    }
                                },
                                onRemoveHashtag = { hashtag ->
                                    selectedHashtags = selectedHashtags - hashtag
                                    if (selectedHashtags.isEmpty()) {
                                        onClearSearch()
                                    } else {
                                        onSearchHashtags(selectedHashtags)
                                    }
                                },
                                onSearch = {
                                    if (selectedHashtags.isNotEmpty()) {
                                        onSearchHashtags(selectedHashtags)
                                    }
                                },
                            )
                        }
                    }


                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        state = listState,
                        contentPadding = PaddingValues(
                            top = if (isSearchActive) 0.dp else paddingValues.calculateTopPadding(),
                            bottom = 16.dp
                        )
                    ) {
                        if (isLoading && posts.isNotEmpty()) {
                            item {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = getPrimaryColor(),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }

                        if (posts.isEmpty() && !isLoading) {
                            item {
                                NoPostsInfo(isSearchActive, selectedHashtags.isNotEmpty())
                            }
                        } else {
                            itemsIndexed(
                                items = posts,
                                key = { _, post -> post.id }
                            ) { index, post ->
                                PostCard(
                                    post = post,
                                    currentUsername = currentUsername,
                                    onEditClick = onEditClick,
                                    onDeleteClick = onDeleteClick,
                                    onHashtagClick = { hashtag ->
                                        if (!selectedHashtags.contains(hashtag)) {
                                            selectedHashtags = selectedHashtags + hashtag
                                            isSearchActive = true
                                            onSearchHashtags(selectedHashtags)
                                        }
                                    },
                                    onImageClick = {
                                        fullScreenMedia = post to false
                                    },
                                    onVideoClick = {
                                        fullScreenMedia = post to true
                                    },
                                    onToggleLike = onToggleLike,
                                    onToggleSeeMore = onToggleSeeMore,
                                    onCommentClick = {
                                        selectedPostId = post.id
                                        showCommentsSheet = true
                                    },
                                    onReportClick = { // ← DODAJ TO
                                        itemToReport = post.id
                                        reportItemType = ReportItemType.POST
                                        showReportDialog = true
                                    }
                                )

                                if (index == posts.size - 3) {
                                    LaunchedEffect(Unit) { onLoadMore() }
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

                val bottomSheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                )


                if (showCommentsSheet && selectedPostId != null) {
                    val selectedPost = posts.find { it.id == selectedPostId }
                    ModalBottomSheet(
                        onDismissRequest = { showCommentsSheet = false },
                        sheetState = bottomSheetState,
                        modifier = Modifier.fillMaxHeight(),
                        containerColor = getSurfaceColor()
                    ) {
                        if (selectedPost != null) {
                            CommentsPanel(
                                postId = selectedPostId!!,
                                initialCommentsCount = selectedPost.commentsCount,
                                onClose = {
                                    showCommentsSheet = false
                                    onRefreshPosts()
                                },
                                currentUsername = currentUsername,
                                reportViewModel = reportViewModel, // ← DODAJ TO
                                onReportComment = { commentId -> // ← DODAJ TO
                                    itemToReport = commentId
                                    reportItemType = ReportItemType.COMMENT
                                    showReportDialog = true
                                }
                            )
                        }
                    }
                }

                // ← DODAJ DIALOG ZGŁASZANIA
                if (showReportDialog && itemToReport != null && reportItemType != null) {
                    ReportDialog(
                        itemId = itemToReport!!,
                        itemType = reportItemType!!,
                        onDismiss = {
                            showReportDialog = false
                            itemToReport = null
                            reportItemType = null
                            reportViewModel.resetState()
                        },
                        viewModel = reportViewModel
                    )
                }
            }
        }
    }
}