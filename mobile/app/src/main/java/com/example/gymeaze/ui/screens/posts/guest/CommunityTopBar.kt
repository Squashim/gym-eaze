package com.example.gymeaze.ui.screens.posts.guest

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getBackgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.gymeaze.R
import com.example.gymeaze.ui.theme.LocalDarkTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityTopBar(
    isSearchActive: Boolean,
    onBackClick: () -> Unit,
    onSearchToggle: () -> Unit

) {
    val isDarkTheme = LocalDarkTheme.current

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Image(
                    painter = painterResource(
                        id = if(isDarkTheme){
                            com.example.gymeaze.R.drawable.gym_eaze_logo_light
                        } else {
                            com.example.gymeaze.R.drawable.gym_eaze_logo_dark
                        }),
                    contentDescription = stringResource(com.example.gymeaze.R.string.logo)
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchToggle) {
                Icon(
                    imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = if (isSearchActive) "Zamknij wyszukiwanie" else "Szukaj",
                    tint = getOnSurfaceColor()
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = getBackgroundColor()
        )
    )
}
