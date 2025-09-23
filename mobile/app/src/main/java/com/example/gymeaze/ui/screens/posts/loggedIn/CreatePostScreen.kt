package com.example.gymeaze.ui.screens.posts.loggedIn

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.res.stringResource
import com.example.gymeaze.R
import com.example.gymeaze.ui.components.BackgroundWithDotsAndCircles
import com.example.gymeaze.ui.components.HashtagInputField
import com.example.gymeaze.ui.screens.ExitConfirmationDialog
import com.example.gymeaze.ui.screens.UploadCard
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor
import com.example.gymeaze.viewmodel.ExerciseCreatorViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    viewModel: ExerciseCreatorViewModel,
    onBackClick: () -> Unit,
    onCreated: () -> Unit
) {
    val ui by viewModel.uiState.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }
    var postContent by remember { mutableStateOf("") }

    val canCreatePost = postContent.isNotBlank()

    LaunchedEffect(ui.success) {
        if (ui.success) {
            onCreated()
            viewModel.clearSuccess()
            viewModel.clearData()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nowy post",
                        color = getOnSurfaceColor(),
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = getOnSurfaceColor()
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            // Tutaj dodaj logikę publikowania posta
                            // np. viewModel.createPost(postContent)
                        },
                        enabled = canCreatePost && !ui.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canCreatePost && !ui.isLoading) getPrimaryColor() else getPrimaryColor().copy(alpha = 0.3f),
                            contentColor = Color.White,
                            disabledContainerColor = getPrimaryColor().copy(alpha = 0.3f),
                            disabledContentColor = Color.White.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        if (ui.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = if (ui.isLoading) "..." else "Opublikuj",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = getBackgroundColor()
                )
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = "Publiczny post",
                        tint = getOnSurfaceSecondaryColor(),
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Wszyscy to zobaczą!",
                        color = getOnSurfaceColor(),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                OutlinedTextField(
                    value = postContent,
                    onValueChange = { postContent = it },
                    placeholder = {
                        Text(
                            text = "Co chcesz udostępnić?",
                            color = getOnSurfaceColor().copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = getOnSurfaceColor().copy(alpha = 0.3f),
                        unfocusedBorderColor = getOnSurfaceColor().copy(alpha = 0.3f),
                        focusedTextColor = getOnSurfaceColor(),
                        unfocusedTextColor = getOnSurfaceColor(),
                        focusedContainerColor = getSurfaceColor(),
                        unfocusedContainerColor = getSurfaceColor(),
                        disabledContainerColor = getSurfaceColor()
                    ),
                    maxLines = 5
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UploadCard(
                        title = "Zdjęcie",
                        subtitle = stringResource(R.string.click_to_upload),
                        hint = stringResource(R.string.image_upload_requirements),
                        selected = ui.imageUri != null,
                        enabled = ui.videoUri == null,
                        onPick = { uri ->
                            viewModel.setImage(uri)
                            if (uri != null) viewModel.setVideo(null)
                        },
                        mime = "image/*",
                        modifier = Modifier.weight(1f)
                    )

                    UploadCard(
                        title = "Film",
                        subtitle = stringResource(R.string.click_to_upload),
                        hint = stringResource(R.string.video_upload_requirements),
                        selected = ui.videoUri != null,
                        enabled = ui.imageUri == null,
                        onPick = { uri ->
                            viewModel.setVideo(uri)
                            if (uri != null) viewModel.setImage(null)
                        },
                        mime = "video/*",
                        modifier = Modifier.weight(1f)
                    )
                }

                HashtagInputField(
                    selectedHashtags = ui.hashtags,
                    onHashtagsChange = viewModel::setHashtags,
                    maxHashtags = 50,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = getOnSurfaceColor().copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(getSurfaceColor(), shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                )
            }
        }
    }

    if (ui.error != null) {
        AlertDialog(
            onDismissRequest = viewModel::clearError,
            title = { Text(stringResource(R.string.error)) },
            text = { Text(ui.error!!) },
            confirmButton = {
                TextButton(onClick = viewModel::clearError) {
                    Text("OK", color = getPrimaryColor())
                }
            }
        )
    }

    if (showExitDialog) {
        ExitConfirmationDialog(
            onDismiss = { showExitDialog = false },
            onConfirm = {
                showExitDialog = false
                viewModel.clearData()
                onBackClick()
            }
        )
    }
}