package com.example.gymeaze.ui.screens.posts.loggedIn

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.gymeaze.R
import com.example.gymeaze.ui.components.BackgroundWithDotsAndCircles
import com.example.gymeaze.ui.components.HashtagInputField
import com.example.gymeaze.ui.components.LoadingScreen
import com.example.gymeaze.ui.screens.exercises.logggedIn.UploadCard
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getErrorColor
import com.example.gymeaze.ui.theme.getOnPrimaryColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor

import com.example.gymeaze.viewmodel.posts.PostsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    viewModel: PostsViewModel,
    onBackClick: () -> Unit,
    onCreated: () -> Unit
) {
    val ui by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val postContent = ui.content
    val canCreatePost = postContent.isNotBlank()

    LaunchedEffect(ui.success) {
        if (ui.success) {
            Toast.makeText(context, context.getString(R.string.succes_created_post), Toast.LENGTH_SHORT).show()
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
                            viewModel.createPost()
                        },
                        enabled = canCreatePost && !ui.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canCreatePost && !ui.isLoading) getPrimaryColor() else getPrimaryColor().copy(alpha = 0.3f),
                            contentColor = Color.White,
                            disabledContainerColor = getPrimaryColor().copy(alpha = 0.3f),
                            disabledContentColor = getOnPrimaryColor().copy(alpha = 0.6f)
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

            if (ui.isLoading) {
                LoadingScreen()
            } else {
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

                    ui.error?.let { errorMessage ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(getSurfaceColor(), shape = RoundedCornerShape(8.dp))
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = getErrorColor().copy(alpha = 0.1f)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, getErrorColor().copy(alpha = 0.3f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = getErrorColor(),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = errorMessage,
                                        color = getErrorColor(),
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    IconButton(onClick = { viewModel.clearError() }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Zamknij",
                                            tint = getErrorColor()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    PostContentSection(
                        postContent = postContent,
                        onContentChange = { viewModel.updateContent(it) }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        UploadCard(
                            title = "Zdjęcie",
                            subtitle = stringResource(R.string.click_to_upload),
                            hint = stringResource(R.string.image_upload_requirements),
                            selected = ui.imageFile != null,
                            enabled = ui.videoFile == null,
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
                            selected = ui.videoFile != null,
                            enabled = ui.imageFile == null,
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
    }
}

@Composable
fun PostContentSection(
    postContent: String,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxLength: Int = 800
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = postContent,
            onValueChange = { newValue ->
                if (newValue.length <= maxLength) {
                    onContentChange(newValue)
                }
            },
            placeholder = {
                Text(
                    text = "Co chcesz udostępnić?",
                    color = getOnSurfaceColor().copy(alpha = 0.6f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp),
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

        Text(
            text = "${postContent.length} / $maxLength znaków",
            color = getOnSurfaceColor(),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}