package com.example.gymeaze.viewmodel.posts

import android.util.Log
import com.example.gymeaze.data.models.Post
import com.example.gymeaze.data.models.PostUi
import com.example.gymeaze.util.formatToRelativeTime
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun Post.toUiModel(isLiked: Boolean = false): PostUi {
    return PostUi(
        id = _id,
        username = author.username,
        userAvatar = author.profileDetails?.profilePicture.orEmpty(),
        timeAgo = createdAt.formatToRelativeTime(),
        content = content,
        mediaUrl = video ?: image,
        likes = likes ?: 0,
        isLiked = isLiked,
        hashtags = hashtags,
        commentsCount = commentsCount ?: 0
    )
}

