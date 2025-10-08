package com.example.gymeaze.ui.screens.posts.components

import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.gymeaze.data.models.PostUi
import com.example.gymeaze.ui.components.VideoPlayer
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor

@Composable
fun MediaFullScreenDialog(
    post: PostUi,
    showVideo: Boolean,
    onClose: () -> Unit
) {
    val mediaUrl = post.mediaUrl ?: return
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(getSurfaceColor())
                .clickable { onClose() }
        ) {
            if (showVideo) {
                VideoPlayer(
                    url = mediaUrl,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 28.dp, vertical = 128.dp)
                        .align(Alignment.Center)
                )
            } else {
                var imageLoading by remember { mutableStateOf(true) }
                AsyncImage(
                    model = mediaUrl,
                    contentDescription = "Zdjęcie w poście (pełny ekran)",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 28.dp, vertical = 128.dp)
                        .align(Alignment.Center),
                    onSuccess = { imageLoading = false },
                    onLoading = { imageLoading = true },
                    onError = { imageLoading = false }
                )
                if (imageLoading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = getPrimaryColor(),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 64.dp, end = 5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Zamknij",
                    tint = getOnSurfaceColor()
                )
            }
        }
    }
}