package com.example.gymeaze.viewmodel.posts

import android.util.Log
import com.example.gymeaze.data.models.Post
import com.example.gymeaze.data.models.PostUi
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
        hashtags = hashtags
    )
}

private fun String.formatToRelativeTime(): String {
    return try {
        val instant = Instant.parse(this)
        val now = Instant.now()
        val minutesAgo = ChronoUnit.MINUTES.between(instant, now)
        val hoursAgo = ChronoUnit.HOURS.between(instant, now)
        val daysAgo = ChronoUnit.DAYS.between(instant, now)

        when {
            minutesAgo < 1 -> "przed chwilą"
            minutesAgo < 60 -> "${minutesAgo}min"
            hoursAgo < 24 -> "${hoursAgo}h"
            daysAgo < 7 -> "${daysAgo}d"
            daysAgo < 30 -> "${daysAgo / 7}tyg"
            else -> DateTimeFormatter.ofPattern("dd.MM.yyyy")
                .withZone(ZoneId.systemDefault())
                .format(instant)
        }
    } catch (e: Exception) {
        Log.e("PostsViewModel", "Error formatting date: $this", e)
        this
    }
}