package com.example.gymeaze.ui.screens.posts.components

import androidx.compose.ui.Alignment
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.stringResource
import com.example.gymeaze.R
import com.example.gymeaze.ui.theme.getPrimaryColor

@Composable
fun HashtagChip(
    text: String,
    onClick: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = getPrimaryColor().copy(alpha = 0.1f),
        modifier = Modifier
            .border(1.dp, getPrimaryColor().copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .then(
                if (onClick != null)
                    Modifier.clickable { onClick() }
                else Modifier
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#$text",
                color = getPrimaryColor(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            if (onRemove != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.delete_hashtag),
                    tint = getPrimaryColor(),
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onRemove() }
                        .padding(2.dp)
                )
            }
        }
    }
}