package com.example.gymeaze.ui.screens.exercises.logggedIn

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymeaze.data.models.Equipment
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.data.models.MuscleGroup
import com.example.gymeaze.ui.theme.*
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import java.text.SimpleDateFormat
import com.example.gymeaze.R
import com.example.gymeaze.data.models.Comment
import com.example.gymeaze.data.models.ExerciseAuthor
import com.example.gymeaze.data.models.User
import com.example.gymeaze.data.models.timeAgo
import com.example.gymeaze.ui.components.DifficultyBadgeExercise
import com.example.gymeaze.ui.components.ReportDialog
import com.example.gymeaze.ui.components.user.UserAvatar
import com.example.gymeaze.ui.screens.posts.guest.components.LoginToLikeDialog
import com.example.gymeaze.viewmodel.ExercisesViewModel
import com.example.gymeaze.viewmodel.ReportItemType
import com.example.gymeaze.viewmodel.ReportViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exerciseId: String,
    onBackClick: () -> Unit,
    onEditClick: ((Exercise) -> Unit)? = null,
    onDeleteSuccess: () -> Unit = {},
    exercisesViewModel: ExercisesViewModel,
    isUserLoggedIn: Boolean = true,
    onUserClick: (String) -> Unit = {}
) {
    val reportViewModel: ReportViewModel = viewModel()

    val context = LocalContext.current
    val currentExercise by exercisesViewModel.currentExercise.collectAsState()
    val isLoadingDetail by exercisesViewModel.isLoadingExerciseDetail.collectAsState()
    val uiState by exercisesViewModel.uiState.collectAsState()
    val favoriteExercises by exercisesViewModel.favoriteExercises.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLoginDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }

    LaunchedEffect(exerciseId) {
        exercisesViewModel.loadExerciseDetail(exerciseId)
    }

    DisposableEffect(Unit) {
        onDispose {
            exercisesViewModel.clearCurrentExercise()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.exericse_detail),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.return_button),
                            tint = getOnSurfaceColor()
                        )
                    }
                },
                actions = {
                    currentExercise?.let { exercise ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (isUserLoggedIn) {
                                        exercisesViewModel.toggleLike(exercise.id) { success, message ->
                                            if (success) {
                                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        showLoginDialog = true
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isUserLoggedIn && exercisesViewModel.isExerciseLiked(exercise.id)) {
                                        Icons.Default.ThumbUpAlt
                                    } else {
                                        Icons.Default.ThumbUpOffAlt
                                    },
                                    contentDescription = stringResource(R.string.like),
                                    tint = if (isUserLoggedIn && exercisesViewModel.isExerciseLiked(exercise.id)) {
                                        getPrimaryColor()
                                    } else {
                                        getOnSurfaceSecondaryColor()
                                    }
                                )
                            }

                            Text(
                                text = exercise.likes.toString(),
                                fontSize = 14.sp,
                                color = getOnSurfaceColor(),
                                modifier = Modifier.padding(end = 8.dp)
                            )

                            if (isUserLoggedIn && exercisesViewModel.isCurrentUserExercise(exercise.author.username)) {
                                IconButton(
                                    onClick = {
                                        onEditClick?.invoke(exercise)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = stringResource(R.string.edit),
                                        tint = getOnSurfaceColor()
                                    )
                                }

                                IconButton(
                                    onClick = { showDeleteDialog = true }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.delete),
                                        tint = Color(0xFFF44336)
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = {
                                        if (isUserLoggedIn) {
                                            exercisesViewModel.toggleFavorite(exercise.id) { success, message ->
                                                if (success) {
                                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } else {
                                            showLoginDialog = true
                                        }
                                    }
                                ) {
                                    val isFavorite = isUserLoggedIn && favoriteExercises.contains(exercise.id)
                                    Icon(
                                        imageVector = if (isFavorite) {
                                            Icons.Default.Favorite
                                        } else {
                                            Icons.Default.FavoriteBorder
                                        },
                                        contentDescription = stringResource(R.string.add_to_favorite),
                                        tint = if (isFavorite) {
                                            Color(0xFFE91E63)
                                        } else {
                                            getOnSurfaceSecondaryColor()
                                        }
                                    )
                                }

                                IconButton(onClick = {
                                    if (!isUserLoggedIn) {
                                        showLoginDialog = true
                                    } else {
                                        showReportDialog = true
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Flag,
                                        contentDescription = stringResource(R.string.report),
                                        tint = getOnSurfaceSecondaryColor()
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = getBackgroundColor()
                )
            )
        },
        containerColor = getBackgroundColor()
    ) { paddingValues ->
        when {
            isLoadingDetail -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = getPrimaryColor())
                }
            }
            uiState.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.error ?: stringResource(R.string.error),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { exercisesViewModel.loadExerciseDetail(exerciseId) }) {
                        Text(text = stringResource(R.string.try_again))
                    }
                }
            }
            currentExercise != null -> {
                ExerciseDetailContent(
                    exercise = currentExercise!!,
                    exercisesViewModel = exercisesViewModel,
                    reportViewModel = reportViewModel,
                    isUserLoggedIn = isUserLoggedIn,
                    onShowLoginDialog = { showLoginDialog = true },
                    onUserClick = onUserClick,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }

    if (showDeleteDialog && currentExercise != null) {
        DeleteExerciseDialog(
            exerciseName = currentExercise!!.name,
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                exercisesViewModel.deleteExercise(exerciseId) {
                    onDeleteSuccess()
                }
            }
        )
    }

    if (showLoginDialog) {
        LoginToLikeDialog(
            onDismiss = { showLoginDialog = false }
        )
    }

    if (showReportDialog) {
        ReportDialog(
            itemId = exerciseId,
            itemType = ReportItemType.EXERCISE,
            onDismiss = {
                showReportDialog = false
                reportViewModel.resetState()
            },
            viewModel = reportViewModel
        )
    }
}

@Composable
fun DeleteExerciseDialog(
    exerciseName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = getSurfaceColor(),
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(R.string.delete_exercise),
                    color = getOnSurfaceColor(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(R.string.delete_exercise_confirm),
                    color = getOnSurfaceColor(),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"$exerciseName\"",
                    color = getOnSurfaceColor(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.cant_rewind_operation),
                    color = getOnSurfaceSecondaryColor(),
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, getOnSurfaceColor().copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        color = getOnSurfaceColor(),
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    )
}

@Composable
private fun ExerciseDetailContent(
    exercise: Exercise,
    exercisesViewModel: ExercisesViewModel,
    reportViewModel: ReportViewModel,
    isUserLoggedIn: Boolean = true,
    onShowLoginDialog: () -> Unit = {},
    onUserClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val comments by exercisesViewModel.exerciseComments.collectAsState()
    val hasMore by exercisesViewModel.hasMoreComments.collectAsState()
    val isLoading by exercisesViewModel.isLoadingComments.collectAsState()
    val currentUser by exercisesViewModel.currentUser.collectAsState()
    val currentUsername = if (isUserLoggedIn) currentUser?.username.orEmpty() else ""

    var newCommentText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<String?>(null) }
    var replyingToUsername by remember { mutableStateOf<String?>(null) }
    var expandedReplies by remember { mutableStateOf<Set<String>>(emptySet()) }

    var editingComment by remember { mutableStateOf<Comment?>(null) }
    var editingContent by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Comment?>(null) }

    var showReportDialog by remember { mutableStateOf(false) }
    var commentToReport by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(exercise.id) {
        exercisesViewModel.loadExerciseComments(exercise.id, refresh = true)
        if (isUserLoggedIn) {
            exercisesViewModel.fetchUserCommentLikes()
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = if (isUserLoggedIn) 20.dp else 80.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text(
                text = exercise.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = getOnSurfaceColor()
            )
        }

        item {
            VideoPlayerSection(
                videoUrl = exercise.video,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            DifficultyBadgeExercise(difficulty = exercise.advancedLevel)
        }

        item {
            DescriptionSection(description = exercise.description)
        }

        item {
            MuscleGroupsSection(muscleGroups = exercise.muscleGroup)
        }

        item {
            EquipmentSection(equipment = exercise.equipment)
        }

        if (exercise.hashtags != null && exercise.hashtags.isNotEmpty()) {
            item {
                HashtagsSection(hashtags = exercise.hashtags)
            }
        }

        item {
            AuthorSection(
                author = exercise.author,
                createdAt = exercise.createdAt,
                onUserClick = onUserClick
            )
        }

        item {
            HorizontalDivider(
                color = getOnSurfaceSecondaryColor().copy(alpha = 0.3f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = stringResource(R.string.comments),
                    tint = getPrimaryColor(),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = stringResource(R.string.comments_header_format, comments.size),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = getOnSurfaceColor()
                )
            }
        }

        item {
            if (isUserLoggedIn && currentUsername.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (replyingTo != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.comments_replying_to_format, replyingToUsername.orEmpty()),
                                fontSize = 14.sp,
                                color = getOnSurfaceSecondaryColor()
                            )
                            IconButton(
                                onClick = {
                                    replyingTo = null
                                    replyingToUsername = null
                                },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(R.string.cancel),
                                    modifier = Modifier.size(18.dp),
                                    tint = getOnSurfaceColor()
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newCommentText,
                            onValueChange = { newCommentText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = {
                                Text(
                                    if (replyingTo != null) stringResource(R.string.write_reply)
                                    else stringResource(R.string.add_comment)
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
                                            exercisesViewModel.addExerciseReply(
                                                replyingTo!!,
                                                newCommentText.trim()
                                            )
                                            exercisesViewModel.loadRepliesForExerciseComment(replyingTo!!)
                                        } else {
                                            exercisesViewModel.addExerciseComment(
                                                exercise.id,
                                                newCommentText.trim()
                                            )
                                        }
                                        newCommentText = ""
                                        replyingTo = null
                                        replyingToUsername = null
                                    }
                                }
                            },
                            enabled = newCommentText.isNotBlank()
                        ) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = null,
                                tint = if (newCommentText.isNotBlank())
                                    getPrimaryColor()
                                else
                                    getOnSurfaceSecondaryColor()
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = getSecondaryColor().copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = stringResource(R.string.login),
                        modifier = Modifier.size(32.dp),
                        tint = getOnSurfaceSecondaryColor()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.login_to_comment),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (comments.isEmpty() && !isLoading) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        tint = getOnSurfaceSecondaryColor(),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.no_comments),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.be_first_to_comment),
                        fontSize = 14.sp,
                        color = getOnSurfaceSecondaryColor(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        items(comments, key = { it._id }) { comment ->
            ExerciseCommentItem(
                comment = comment,
                viewModel = exercisesViewModel,
                reportViewModel = reportViewModel,
                isUserLoggedIn = isUserLoggedIn,
                onReply = { commentId, username ->
                    if (isUserLoggedIn) {
                        replyingTo = commentId
                        replyingToUsername = username
                    } else {
                        onShowLoginDialog()
                    }
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
                        exercisesViewModel.loadRepliesForExerciseComment(comment._id)
                    }
                },
                currentUsername = currentUsername,
                onEditClick = { commentToEdit ->
                    editingComment = commentToEdit
                    editingContent = commentToEdit.content
                    showEditDialog = true
                },
                onDeleteClick = { commentToDelete ->
                    showDeleteDialog = commentToDelete
                },
                onShowLoginDialog = onShowLoginDialog,
                onReportClick = { commentId ->
                    commentToReport = commentId
                    showReportDialog = true
                },
                onUserClick = onUserClick
            )

            if (expandedReplies.contains(comment._id)) {
                val replies by exercisesViewModel.getRepliesForExerciseComment(comment._id).collectAsState()
                replies.forEach { reply ->
                    ExerciseReplyItem(
                        reply = reply,
                        viewModel = exercisesViewModel,
                        reportViewModel = reportViewModel,
                        isUserLoggedIn = isUserLoggedIn,
                        currentUsername = currentUsername,
                        onEditClick = { replyToEdit ->
                            editingComment = replyToEdit
                            editingContent = replyToEdit.content
                            showEditDialog = true
                        },
                        onDeleteClick = { replyToDelete ->
                            showDeleteDialog = replyToDelete
                        },
                        onShowLoginDialog = onShowLoginDialog,
                        onReportClick = { replyId ->
                            commentToReport = replyId
                            showReportDialog = true
                        },
                        onUserClick = onUserClick
                    )
                }
            }
        }

        if (hasMore && !isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = {
                            exercisesViewModel.loadMoreExerciseComments(exercise.id)
                        }
                    ) {
                        Text(stringResource(R.string.load_more_comments), color = getPrimaryColor())
                    }
                }
            }
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = getPrimaryColor())
                }
            }
        }
    }

    if (showDeleteDialog != null) {
        DeleteCommentDialog(
            onConfirm = {
                scope.launch {
                    exercisesViewModel.deleteExerciseComment(showDeleteDialog!!) { success, _ ->
                        if (success) {
                            showDeleteDialog = null
                            exercisesViewModel.loadExerciseComments(exercise.id, refresh = true)
                        }
                    }
                }
            },
            onDismiss = { showDeleteDialog = null }
        )
    }

    if (showEditDialog && editingComment != null) {
        EditCommentDialog(
            editingContent = editingContent,
            onContentChange = { editingContent = it },
            editMessage = "",
            isReply = editingComment?.parentId != null,
            onConfirm = {
                scope.launch {
                    exercisesViewModel.editExerciseComment(
                        editingComment!!,
                        editingContent.trim()
                    ) { success, _ ->
                        if (success) {
                            showEditDialog = false
                            editingComment = null
                            editingContent = ""
                            exercisesViewModel.loadExerciseComments(exercise.id, refresh = true)
                        }
                    }
                }
            },
            onDismiss = {
                showEditDialog = false
                editingComment = null
                editingContent = ""
            },
            canSave = editingContent.trim().isNotBlank() &&
                    editingContent.trim() != editingComment?.content
        )
    }

    if (showReportDialog && commentToReport != null) {
        ReportDialog(
            itemId = commentToReport!!,
            itemType = ReportItemType.COMMENT,
            onDismiss = {
                showReportDialog = false
                commentToReport = null
                reportViewModel.resetState()
            },
            viewModel = reportViewModel
        )
    }
}

@Composable
private fun DeleteCommentDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = getSurfaceColor(),
        shape = RoundedCornerShape(20.dp),
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = getErrorColor(),
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = stringResource(R.string.delete_comment),
                color = getOnSurfaceColor(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = stringResource(R.string.delete_comment_confirm),
                color = getOnSurfaceColor(),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = getErrorColor()
                )
            ) {
                Text(stringResource(R.string.delete), color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = getOnSurfaceSecondaryColor())
            }
        }
    )
}

@Composable
fun EditCommentDialog(
    editingContent: String,
    onContentChange: (String) -> Unit,
    editMessage: String,
    isReply: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    canSave: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = getSurfaceColor(),
        title = {
            Text(
                text = if (isReply) stringResource(R.string.edit_reply) else stringResource(R.string.edit_comment),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = editingContent,
                    onValueChange = onContentChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp, max = 300.dp),
                    placeholder = { Text(stringResource(R.string.write_your_comment)) },
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
                onClick = onConfirm,
                enabled = canSave,
                colors = ButtonDefaults.buttonColors(
                    containerColor = getPrimaryColor()
                )
            ) {
                Text(stringResource(R.string.save), fontWeight = FontWeight.Medium)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(R.string.cancel),
                    color = getOnSurfaceSecondaryColor(),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}

@Composable
private fun VideoPlayerSection(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
        }
    }

    DisposableEffect(
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp)),
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = true
                    setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                }
            }
        )
    ) {
        onDispose {
            exoPlayer.release()
        }
    }
}

@Composable
private fun DescriptionSection(description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.exercise_description),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = getOnSurfaceColor()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = getOnSurfaceColor(),
                lineHeight = 20.sp
            )
        }
    }
}


@Composable
private fun MuscleGroupsSection(muscleGroups: List<MuscleGroup>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.active_muscle_groups),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = getOnSurfaceColor()
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(muscleGroups) { muscleGroup ->
                    MuscleGroupItem(muscleGroup = muscleGroup)
                }
            }
        }
    }
}

@Composable
private fun MuscleGroupItem(muscleGroup: MuscleGroup) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    getPrimaryColor().copy(alpha = 0.1f),
                    CircleShape
                )
                .border(2.dp, getPrimaryColor(), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = muscleGroup.image,
                contentDescription = muscleGroup.name,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.gym_eaze_logo_light),
                error = painterResource(R.drawable.gym_eaze_logo_dark)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = muscleGroup.name,
            fontSize = 12.sp,
            color = getOnSurfaceColor(),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun EquipmentSection(equipment: List<Equipment>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.required_equipment),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = getOnSurfaceColor()
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(equipment) { equipmentItem ->
                    EquipmentItem(equipment = equipmentItem)
                }
            }
        }
    }
}

@Composable
private fun EquipmentItem(equipment: Equipment) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    getOnSurfaceColor().copy(alpha = 0.1f),
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = equipment.image,
                contentDescription = equipment.name,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.gym_eaze_logo_light),
                error = painterResource(R.drawable.gym_eaze_logo_dark)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = equipment.name,
            fontSize = 12.sp,
            color = getOnSurfaceColor(),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun HashtagsSection(hashtags: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.hashtags2),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = getOnSurfaceColor()
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(hashtags) { hashtag ->
                    HashtagChip(hashtag = hashtag)
                }
            }
        }
    }
}

@Composable
private fun HashtagChip(hashtag: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = getPrimaryColor().copy(alpha = 0.1f),
        modifier = Modifier
            .border(1.dp, getPrimaryColor().copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    ) {
        Text(
            text = "#$hashtag",
            fontSize = 12.sp,
            color = getPrimaryColor(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun AuthorSection(
    author: ExerciseAuthor,
    createdAt: String,
    onUserClick: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUserClick(author.username) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.added_by),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = getOnSurfaceColor()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(
                    imageUrl = author.profileDetails?.profilePicture,
                    username = author.username,
                    modifier = Modifier
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = author.username,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor()
                    )

                    Text(
                        text = formatDate(createdAt),
                        fontSize = 12.sp,
                        color = getOnSurfaceSecondaryColor()
                    )
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

@Composable
private fun ExerciseCommentItem(
    comment: Comment,
    viewModel: ExercisesViewModel,
    reportViewModel: ReportViewModel,
    isUserLoggedIn: Boolean = true,
    onReply: (String, String) -> Unit,
    isExpanded: Boolean,
    onToggleReplies: () -> Unit,
    currentUsername: String,
    onEditClick: (Comment) -> Unit = {},
    onDeleteClick: (Comment) -> Unit = {},
    onShowLoginDialog: () -> Unit = {},
    onReportClick: (String) -> Unit = {},
    onUserClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    val isLiked by viewModel.isExerciseCommentLiked(comment._id).collectAsState(initial = false)

    val maxChars = 250
    var expanded by remember { mutableStateOf(false) }
    val cleanedLength = comment.content.replace(Regex("\\s+"), " ").trim().length
    val shouldShowSeeAll = cleanedLength > maxChars

    val displayText = if (!expanded && shouldShowSeeAll) {
        comment.content.trim().take(maxChars) + "..."
    } else {
        comment.content.trim()
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
                        color = getOnSurfaceColor(),
                        modifier = Modifier.clickable { onUserClick(comment.author.username) }
                    )

                    if (isUserLoggedIn) {
                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = stringResource(R.string.more),
                                    modifier = Modifier.size(18.dp),
                                    tint = getOnSurfaceColor()
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                if (comment.author.username == currentUsername) {
                                    DropdownMenuItem(
                                        onClick = {
                                            showMenu = false
                                            onEditClick(comment)
                                        },
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp),
                                                    tint = getOnSurfaceColor()
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(stringResource(R.string.edit_comment), color = getOnSurfaceColor())
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
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp),
                                                    tint = getErrorColor()
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(stringResource(R.string.delete_comment), color = getErrorColor())
                                            }
                                        }
                                    )
                                } else {
                                    DropdownMenuItem(
                                        onClick = {
                                            showMenu = false
                                            onReportClick(comment._id)
                                        },
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Flag,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp),
                                                    tint = getOnSurfaceColor()
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(stringResource(R.string.report_comment), color = getOnSurfaceColor())
                                            }
                                        }
                                    )
                                }
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
                            if (expanded) stringResource(R.string.see_less) else stringResource(R.string.see_all),
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
                            if (isUserLoggedIn) {
                                viewModel.toggleExerciseCommentLike(comment._id) { success, message ->
                                    if (success) {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                onShowLoginDialog()
                            }
                        }
                    ) {
                        Text(
                            text = comment.timeAgo(),
                            color = getOnSurfaceSecondaryColor(),
                            fontSize = 11.sp
                        )
                        if (comment.isEdited == true) {
                            Text(
                                text = stringResource(R.string.edited),
                                color = getOnSurfaceSecondaryColor(),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        Spacer(Modifier.padding(2.dp))
                        Icon(
                            if (isUserLoggedIn && isLiked) Icons.Filled.ThumbUpAlt else Icons.Default.ThumbUpOffAlt,
                            contentDescription = stringResource(R.string.like),
                            tint = if (isUserLoggedIn && isLiked) getPrimaryColor() else getOnSurfaceColor(),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${comment.likes ?: 0}",
                            fontSize = 12.sp,
                            color = getOnSurfaceColor()
                        )
                    }

                    TextButton(
                        onClick = { onReply(comment._id, comment.author.username) },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            stringResource(R.string.reply),
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
                                if (isExpanded) stringResource(R.string.hide_reply)
                                else stringResource(R.string.comments_show_replies_format, comment.repliesCount ?: 0),
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
private fun ExerciseReplyItem(
    reply: Comment,
    viewModel: ExercisesViewModel,
    reportViewModel: ReportViewModel,
    isUserLoggedIn: Boolean = true,
    currentUsername: String,
    onEditClick: (Comment) -> Unit = {},
    onDeleteClick: (Comment) -> Unit = {},
    onShowLoginDialog: () -> Unit = {},
    onReportClick: (String) -> Unit = {},
    onUserClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    val isLiked by viewModel.isExerciseCommentLiked(reply._id).collectAsState(initial = false)

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
                    color = getOnSurfaceColor(),
                    modifier = Modifier.clickable { onUserClick(reply.author.username) }
                )

                if (isUserLoggedIn) {
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = getOnSurfaceColor()
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            if (reply.author.username == currentUsername) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.edit_reply), color = getOnSurfaceColor()) },
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
                                    text = { Text(stringResource(R.string.delete_reply), color = getErrorColor()) },
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
                                    text = { Text(stringResource(R.string.report), color = getOnSurfaceColor()) },
                                    onClick = {
                                        showMenu = false
                                        onReportClick(reply._id)
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
                    if (isUserLoggedIn) {
                        viewModel.toggleExerciseCommentLike(reply._id) { success, message ->
                            if (success) {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        onShowLoginDialog()
                    }
                }
            ) {
                Text(
                    text = reply.timeAgo(),
                    color = getOnSurfaceSecondaryColor(),
                    fontSize = 12.sp
                )
                if (reply.isEdited == true) {
                    Text(
                        text = stringResource(R.string.edited),
                        color = getOnSurfaceSecondaryColor(),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(Modifier.padding(3.dp))
                Icon(
                    if (isUserLoggedIn && isLiked) Icons.Filled.ThumbUpAlt else Icons.Default.ThumbUpOffAlt,
                    contentDescription = null,
                    tint = if (isUserLoggedIn && isLiked) getPrimaryColor() else getOnSurfaceColor(),
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