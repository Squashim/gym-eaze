package com.example.gymeaze.ui.screens.posts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gymeaze.ui.theme.getOnPrimaryColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getSurfaceVariantColor

@Composable
fun UserAvatar(
    imageUrl: String?,
    username: String,
    modifier: Modifier = Modifier
) {
    if (!imageUrl.isNullOrEmpty()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Avatar użytkownika",
            modifier = modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentScale = ContentScale.Crop
        )
    } else {
        val initials = username.take(2).uppercase()
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(getSurfaceVariantColor())
        ) {
            Text(
                text = initials,
                color = getOnSurfaceColor(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}