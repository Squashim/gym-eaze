package com.example.gymeaze.ui.screens.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.data.models.Comment
import com.example.gymeaze.data.models.timeAgo
import com.example.gymeaze.ui.screens.posts.guest.components.LoginToLikeDialog
import com.example.gymeaze.ui.components.user.UserAvatar
import com.example.gymeaze.ui.theme.getErrorColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSecondaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor
import com.example.gymeaze.viewmodel.ReportViewModel
import com.example.gymeaze.viewmodel.posts.PostsViewModel
import com.example.gymeaze.viewmodel.posts.cleanedCommentLength
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsPanel(
    postId: String,
    initialCommentsCount: Int,
    onClose: () -> Unit,
    currentUsername: String,
    reportViewModel: ReportViewModel,
    onReportComment: (String) -> Unit,
    isUserLoggedIn: Boolean = true,
    onUserClick: (String) -> Unit = {}
) {
    val viewModel: PostsViewModel = viewModel()
    val comments by viewModel.comments.collectAsState()
    val hasMore by viewModel.hasMoreComment.collectAsState()
    val isLoading by viewModel.isLoadingComments.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val currentUsername = currentUser?.username.orEmpty()

    var newCommentText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<String?>(null) }
    var replyingToUsername by remember { mutableStateOf<String?>(null) }
    var expandedReplies by remember { mutableStateOf<Set<String>>(emptySet()) }

    var editingComment by remember { mutableStateOf<Comment?>(null) }
    var editingContent by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Comment?>(null) }
    var deleteMessage by remember { mutableStateOf("") }
    var editMessage by remember { mutableStateOf("") }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var shouldScrollToTop by remember { mutableStateOf(false) }

    var commentsCount by remember { mutableStateOf(initialCommentsCount) }

    var showLoginDialog by remember { mutableStateOf(false) }

    LaunchedEffect(postId) {
        viewModel.loadComments(postId, refresh = true)
        viewModel.fetchUserCommentLikes()
        commentsCount = initialCommentsCount
    }

    LaunchedEffect(comments) {
        if (shouldScrollToTop) {
            listState.animateScrollToItem(0)
            shouldScrollToTop = false
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                if (lastIndex != null && lastIndex >= comments.size - 2 && hasMore && !isLoading) {
                    viewModel.loadMoreComments(postId)
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(getSurfaceColor())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Komentarze ($commentsCount)",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = getOnSurfaceColor()
            )
            IconButton(onClick = onClose, modifier = Modifier.offset(x = (8).dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Zamknij",
                    tint = getOnSurfaceColor()
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f)
        ) {
            items(comments, key = { it._id }) { comment ->
                CommentItem(
                    comment = comment,
                    viewModel = viewModel,
                    onReply = { commentId, username ->
                        replyingTo = commentId
                        replyingToUsername = username
                    },
                    isExpanded = expandedReplies.contains(comment._id),
                    onToggleReplies = {
                        val wasExpanded = expandedReplies.contains(comment._id)
                        expandedReplies = if (wasExpanded) {
                            expandedReplies - comment._id
                        } else {
                            expandedReplies + comment._id
                        }
                        if (!wasExpanded) {
                            viewModel.loadReplies(comment._id)
                        }
                    },
                    currentUsername = currentUsername,
                    onEditClick = { commentToEdit ->
                        editingComment = commentToEdit
                        editingContent = commentToEdit.content
                        editMessage = ""
                        showEditDialog = true
                    },
                    onDeleteClick = { commentToDelete ->
                        showDeleteDialog = commentToDelete
                        deleteMessage = ""
                    },
                    onReportClick = { commentToReport ->
                        onReportComment(commentToReport._id)
                    },
                    onLoginRequired = { showLoginDialog = true },
                    isUserLoggedIn = isUserLoggedIn,
                    onUserClick = onUserClick
                )

                if (expandedReplies.contains(comment._id)) {
                    val replies by viewModel.getRepliesForComment(comment._id).collectAsState()
                    replies.forEach { reply ->
                        ReplyItem(
                            reply = reply,
                            viewModel = viewModel,
                            currentUsername = currentUsername,
                            onEditClick = { replyToEdit ->
                                editingComment = replyToEdit
                                editingContent = replyToEdit.content
                                editMessage = ""
                                showEditDialog = true
                            },
                            onDeleteClick = { replyToDelete ->
                                showDeleteDialog = replyToDelete
                                deleteMessage = ""
                            },
                            onReportClick = { replyToReport ->
                                onReportComment(replyToReport._id)
                            },
                            onLoginRequired = { showLoginDialog = true },
                            isUserLoggedIn = isUserLoggedIn,
                            onUserClick = onUserClick
                        )
                        Spacer(Modifier.padding(3.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        if (showLoginDialog) {
            LoginToLikeDialog(
                onDismiss = { showLoginDialog = false },
            )
        }

        if (showDeleteDialog != null) {
            DeleteCommentDialog(
                onConfirm = {
                    scope.launch {
                        viewModel.deleteComment(showDeleteDialog!!) { success, msg ->
                            deleteMessage = msg
                            if (success) {
                                showDeleteDialog = null
                                viewModel.loadComments(postId, refresh = true)
                                expandedReplies.forEach { id ->
                                    viewModel.loadReplies(id)
                                }
                                commentsCount = (commentsCount - 1).coerceAtLeast(0)
                            }
                        }
                    }
                },
                onDismiss = { showDeleteDialog = null }
            )
            if (deleteMessage.isNotBlank()) {
                Text(
                    deleteMessage,
                    color = if (deleteMessage.contains("Błąd")) getErrorColor() else getPrimaryColor(),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        if (showEditDialog && editingComment != null) {
            AlertDialog(
                onDismissRequest = {
                    showEditDialog = false
                    editingComment = null
                    editingContent = ""
                    editMessage = ""
                },
                containerColor = getSurfaceColor(),
                title = {
                    Text(
                        text = if (editingComment?.parentId != null)
                            "Edytuj odpowiedź"
                        else
                            "Edytuj komentarz",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = editingContent,
                            onValueChange = { editingContent = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp, max = 300.dp),
                            placeholder = { Text("Napisz swój komentarz...") },
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 10,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = getPrimaryColor(),
                                unfocusedBorderColor = getOnSurfaceSecondaryColor(),
                                cursorColor = getPrimaryColor()
                            )
                        )

                        if (editMessage.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                editMessage,
                                color = if (editMessage.contains("Błąd"))
                                    getErrorColor()
                                else
                                    getPrimaryColor(),
                                fontSize = 13.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.editComment(
                                    editingComment!!,
                                    editingContent.trim()
                                ) { success, msg ->
                                    editMessage = msg
                                    if (success) {
                                        showEditDialog = false
                                        editingComment = null
                                        editingContent = ""
                                        viewModel.loadComments(postId, refresh = true)
                                        expandedReplies.forEach { id ->
                                            viewModel.loadReplies(id)
                                        }
                                    }
                                }
                            }
                        },
                        enabled = editingContent.trim().isNotBlank() &&
                                editingContent.trim() != editingComment?.content,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getPrimaryColor()
                        )
                    ) {
                        Text("Zapisz", fontWeight = FontWeight.Medium)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showEditDialog = false
                            editingComment = null
                            editingContent = ""
                            editMessage = ""
                        }
                    ) {
                        Text(
                            "Anuluj",
                            color = getOnSurfaceSecondaryColor(),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            )
        }

        if (isUserLoggedIn) {
            if (replyingTo != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Odpowiadasz @$replyingToUsername",
                        fontSize = 16.sp,
                        color = getOnSurfaceSecondaryColor()
                    )
                    IconButton(
                        onClick = {
                            replyingTo = null
                            replyingToUsername = null
                        },
                        modifier = Modifier.size(20.dp),
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Anuluj",
                            modifier = Modifier.size(18.dp),
                            tint = getOnSurfaceColor()
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            if (replyingTo != null) "Napisz odpowiedź..."
                            else "Dodaj komentarz..."
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = getPrimaryColor(),
                        unfocusedBorderColor = getOnSurfaceSecondaryColor(),
                        cursorColor = getPrimaryColor()
                    )
                )

                IconButton(
                    onClick = {
                        if (newCommentText.isNotBlank()) {
                            scope.launch {
                                if (replyingTo != null) {
                                    viewModel.addReply(replyingTo!!, newCommentText.trim())
                                    viewModel.loadReplies(replyingTo!!)
                                } else {
                                    viewModel.addComment(postId, newCommentText.trim())
                                    commentsCount += 1
                                    shouldScrollToTop = true
                                }
                                newCommentText = ""
                                replyingTo = null
                                replyingToUsername = null
                            }
                        }
                    },
                    enabled = newCommentText.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Wyślij", tint = getOnSurfaceColor())
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .background(
                        color = getSecondaryColor().copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Zaloguj się",
                    modifier = Modifier.size(32.dp),
                    tint = getOnSurfaceSecondaryColor()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Zaloguj się, aby dodawać komentarze",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = getOnSurfaceColor(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dołącz do dyskusji i dołącz do społeczności",
                    fontSize = 14.sp,
                    color = getOnSurfaceSecondaryColor(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    viewModel: PostsViewModel,
    onReply: (String, String) -> Unit,
    isExpanded: Boolean,
    onToggleReplies: () -> Unit,
    currentUsername: String,
    onEditClick: (Comment) -> Unit = {},
    onDeleteClick: (Comment) -> Unit = {},
    onReportClick: (Comment) -> Unit = {},
    onLoginRequired: () -> Unit = {},
    isUserLoggedIn: Boolean = true,
    onUserClick: (String) -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    val isLiked by viewModel.isCommentLiked(comment._id).collectAsState(initial = false)

    val maxChars = 250
    var expanded by remember { mutableStateOf(false) }
    val cleanedLength = cleanedCommentLength(comment.content)
    val shouldShowSeeAll = cleanedLength > maxChars

    val displayText = if (!expanded && shouldShowSeeAll) {
        comment.content.trim().take(maxChars) + "..."
    } else {
        comment.content.trim()
    }

    fun requireLogin(action: () -> Unit) {
        if (!isUserLoggedIn) {
            onLoginRequired()
        } else {
            action()
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserAvatar(
                imageUrl = comment.author.profileDetails?.profilePicture,
                username = comment.author.username,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onUserClick(comment.author.username) }
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = comment.author.username,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = getOnSurfaceColor()
                    )

                    Box {
                        IconButton(
                            onClick = {
                                requireLogin { showMenu = true }
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Więcej",
                                modifier = Modifier.size(18.dp),
                                tint = getOnSurfaceColor()
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                        ) {
                            if (comment.author.username == currentUsername) {
                                DropdownMenuItem(
                                    onClick = {
                                        showMenu = false
                                        onEditClick(comment)
                                    },
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp),
                                                tint = getOnSurfaceColor()
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                "Edytuj komentarz",
                                                color = getOnSurfaceColor()
                                            )
                                        }
                                    }
                                )

                                HorizontalDivider(
                                    color = getOnSurfaceColor().copy(alpha = 0.3f),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )

                                DropdownMenuItem(
                                    onClick = {
                                        showMenu = false
                                        onDeleteClick(comment)
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp),
                                                tint = getErrorColor()
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                "Usuń komentarz",
                                                color = getErrorColor()
                                            )
                                        }
                                    }
                                )
                            } else {
                                DropdownMenuItem(
                                    onClick = {
                                        showMenu = false
                                        onReportClick(comment)
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Flag,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp),
                                                tint = getOnSurfaceColor()
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                "Zgłoś komentarz",
                                                color = getOnSurfaceColor()
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = displayText,
                    fontSize = 14.sp,
                    color = getOnSurfaceColor()
                )

                if (shouldShowSeeAll) {
                    TextButton(
                        onClick = { expanded = !expanded },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            if (expanded) "Zobacz mniej" else "Zobacz wszystko",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = getOnSurfaceSecondaryColor()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.clickable {
                            requireLogin {
                                viewModel.toggleCommentLike(comment._id)
                            }
                        }
                    ) {
                        Text(
                            text = " ${comment.timeAgo()}",
                            color = getOnSurfaceSecondaryColor(),
                            fontSize = 11.sp
                        )
                        if (comment.isEdited == true) {
                            Text(
                                text = "(edytowany)",
                                color = getOnSurfaceSecondaryColor(),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        Spacer(Modifier.padding(2.dp))
                        Icon(
                            if (isLiked) Icons.Filled.ThumbUpAlt else Icons.Default.ThumbUpOffAlt,
                            contentDescription = "Polub",
                            tint = if (isLiked) getPrimaryColor() else getOnSurfaceColor(),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${comment.likes ?: 0}",
                            fontSize = 12.sp,
                            color = getOnSurfaceColor()
                        )
                    }

                    TextButton(
                        onClick = {
                            requireLogin {
                                onReply(comment._id, comment.author.username)
                            }
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            "Odpowiedz",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = getOnSurfaceColor()
                        )
                    }

                    if (comment.repliesCount != null && comment.repliesCount > 0) {
                        TextButton(
                            onClick = onToggleReplies,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                if (isExpanded) "Ukryj odp."
                                else "Zobacz odp. (${comment.repliesCount})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = getOnSurfaceColor()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReplyItem(
    reply: Comment,
    viewModel: PostsViewModel,
    currentUsername: String,
    onEditClick: (Comment) -> Unit = {},
    onDeleteClick: (Comment) -> Unit = {},
    onReportClick: (Comment) -> Unit = {},
    onLoginRequired: () -> Unit = {},
    isUserLoggedIn: Boolean = true,
    onUserClick: (String) -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    val isLiked by viewModel.isCommentLiked(reply._id).collectAsState(initial = false)

    fun requireLogin(action: () -> Unit) {
        if (!isUserLoggedIn) {
            onLoginRequired()
        } else {
            action()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 52.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        UserAvatar(
            imageUrl = reply.author.profileDetails?.profilePicture,
            username = reply.author.username,
            modifier = Modifier
                .size(32.dp)
                .clickable { onUserClick(reply.author.username) }
        )

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reply.author.username,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = getOnSurfaceColor()
                )

                Box {
                    IconButton(
                        onClick = {
                            requireLogin { showMenu = true }
                        },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Więcej",
                            modifier = Modifier.size(16.dp),
                            tint = getOnSurfaceColor()
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                    ) {
                        if (reply.author.username == currentUsername) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Edytuj odpowiedź",
                                        color = getOnSurfaceColor()
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onEditClick(reply)
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = getOnSurfaceColor()
                                    )
                                }
                            )

                            HorizontalDivider(
                                color = getOnSurfaceColor().copy(alpha = 0.3f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Usuń odpowiedź",
                                        color = getErrorColor()
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onDeleteClick(reply)
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = getErrorColor()
                                    )
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Zgłoś",
                                        color = getOnSurfaceColor()
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onReportClick(reply)
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Flag,
                                        contentDescription = null,
                                        tint = getOnSurfaceColor()
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = reply.content,
                fontSize = 14.sp,
                color = getOnSurfaceColor()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.clickable {
                    requireLogin {
                        viewModel.toggleCommentLike(reply._id)
                    }
                }
            ) {
                Text(
                    text = " ${reply.timeAgo()}",
                    color = getOnSurfaceSecondaryColor(),
                    fontSize = 12.sp
                )
                if (reply.isEdited == true) {
                    Text(
                        text = "(edytowany)",
                        color = getOnSurfaceSecondaryColor(),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(Modifier.padding(3.dp))
                Icon(
                    if (isLiked) Icons.Filled.ThumbUpAlt else Icons.Default.ThumbUpOffAlt,
                    contentDescription = "Polub",
                    tint = if (isLiked) getPrimaryColor() else getOnSurfaceColor(),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${reply.likes ?: 0}",
                    fontSize = 11.sp,
                    color = getOnSurfaceColor()
                )
            }
        }
    }
}