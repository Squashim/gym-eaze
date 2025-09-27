package com.example.gymeaze.ui.screens.posts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymeaze.R
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.viewmodel.posts.PostsViewModel

@Composable
fun CommentsPanel(postId: String, onClose: () -> Unit) {
    val viewModel: PostsViewModel = viewModel()
    val comments by viewModel.comments.collectAsState()
    val commentsCount by viewModel.commentsCount.collectAsState()

    LaunchedEffect(postId) {
        viewModel.loadComments(postId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Komentarze ($commentsCount)", fontWeight = FontWeight.Bold)
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Zamknij")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(comments) { comment ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UserAvatar(
                        imageUrl = comment.author.profileDetails?.profilePicture,
                        username = comment.author.username
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = comment.author.username,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = getOnSurfaceColor()
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = comment.content,
                            fontSize = 14.sp,
                            color = getOnSurfaceColor()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
