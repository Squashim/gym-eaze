package com.example.gymeaze.ui.screens.posts.components

import android.R.attr.onClick
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymeaze.data.models.PostUi
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getErrorColor
import com.example.gymeaze.ui.theme.getOnPrimaryColor
import com.example.gymeaze.ui.theme.getOnSecondaryColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor

@Composable
fun PostCard(
    post: PostUi,
    currentUsername: String,
    onEditClick: (PostUi) -> Unit,
    onDeleteClick: (PostUi) -> Unit,
    onHashtagClick: (String) -> Unit = {},
    onImageClick: () -> Unit = {},
    onVideoClick: () -> Unit = {},
    onUserClick: (String) -> Unit = {},
    onReportClick: (PostUi) -> Unit = {},
    modifier: Modifier = Modifier,
    onToggleLike: (String) -> Unit,
    onToggleSeeMore: (String) -> Boolean,
    onCommentClick: () -> Unit,
) {
    fun isVideo(url: String): Boolean =
        url.endsWith(".mp4", true) || url.endsWith(".webm", true) || url.contains("video", true)

    var menuExpanded by remember { mutableStateOf(false) }
    var isContentExpanded by remember { mutableStateOf(false) }

    var moreDialog  by remember { mutableStateOf(false) }
    val maxContentLength = 550


    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = getSurfaceColor()
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(
                    imageUrl = post.userAvatar,
                    username = post.username,
                    modifier = Modifier.clickable { onUserClick(post.username) }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onUserClick(post.username) }
                ) {
                    Text(
                        text = post.username,
                        color = getOnSurfaceColor(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = post.timeAgo,
                        color = getOnSurfaceSecondaryColor(),
                        fontSize = 12.sp
                    )
                }

                Box {
                    IconButton(onClick = {
                        if (onToggleSeeMore(post.id) == true) {
                            menuExpanded = true
                        } else {
                            menuExpanded = false
                        }

                    }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Więcej opcji",
                            tint = getOnSurfaceSecondaryColor()
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        containerColor = getBackgroundColor()
                    ) {
                        if (post.username == currentUsername) {
                            DropdownMenuItem(
                                onClick = {
                                    menuExpanded = false
                                    onEditClick(post)
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
                                            "Edytuj post",
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
                                    menuExpanded = false
                                    onDeleteClick(post)
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
                                            "Usuń post",
                                            color = getErrorColor()
                                        )
                                    }
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                onClick = {
                                    menuExpanded = false
                                    onReportClick(post)
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
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
                                            "Zgłoś post",
                                            color = getOnSurfaceColor()
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (post.content.isNotEmpty()) {
                val shouldTruncate = post.content.length > maxContentLength && !isContentExpanded
                val displayText = if (shouldTruncate) {
                    post.content.take(maxContentLength)
                } else {
                    post.content
                }

                Column {
                    Text(
                        text = displayText,
                        color = getOnSurfaceColor(),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    if (post.content.length > maxContentLength) {
                        Text(
                            text = if (isContentExpanded) "Zobacz mniej" else "Zobacz wszystko",
                            color = getOnSurfaceSecondaryColor(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clickable {
                                    isContentExpanded = !isContentExpanded
                                }
                                .padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            if (post.hashtags.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(post.hashtags) { hashtag ->
                        HashtagChip(
                            text = hashtag,
                            onClick = { onHashtagClick(hashtag) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            post.mediaUrl?.let { mediaUrl ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(getSurfaceColor())
                ) {
                    if (isVideo(mediaUrl)) {
                        VideoPlayer(
                            url = mediaUrl,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { onVideoClick() }
                                .background(androidx.compose.ui.graphics.Color.Transparent)
                        )
                    } else {
                        AsyncImage(
                            model = mediaUrl,
                            contentDescription = "Zdjęcie w poście",
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onImageClick() },
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Polubienia
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.clickable { onToggleLike(post.id) }
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.ThumbUpAlt else Icons.Default.ThumbUpOffAlt,
                        contentDescription = if (post.isLiked) "Cofnij polubienie" else "Dodaj polubienie",
                        tint = if (post.isLiked) getPrimaryColor() else getOnSurfaceSecondaryColor(),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Polubienia (${post.likes})",
                        color = if (post.isLiked) getPrimaryColor() else getOnSurfaceSecondaryColor(),
                        fontSize = 14.sp,
                        fontWeight = if (post.isLiked) FontWeight.Medium else FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(20.dp)
                        .background(getOnSurfaceSecondaryColor().copy(alpha = 0.3f))
                )

                Spacer(modifier = Modifier.width(32.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.clickable { onCommentClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Komentarz",
                        tint = getOnSurfaceSecondaryColor(),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Komentarze (${post.commentsCount})",
                        color = getOnSurfaceSecondaryColor(),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}