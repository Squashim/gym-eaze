package com.example.gymeaze.ui.screens.posts.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor

@Composable
fun NoPostsInfo(isSearchActive: Boolean, isAnyHashtag: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(64.dp),
        contentAlignment = Alignment.Center

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isSearchActive && isAnyHashtag)
                    "Nie znaleziono postów" else "Brak postów",
                color = getOnSurfaceSecondaryColor(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isSearchActive && isAnyHashtag)
                    "Spróbuj wyszukać inne hashtagi" else "Sprawdź ponownie później",
                color = getOnSurfaceSecondaryColor(),
                fontSize = 18.sp
            )
        }
    }
}