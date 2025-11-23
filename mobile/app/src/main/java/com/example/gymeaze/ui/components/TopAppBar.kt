package com.example.gymeaze.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.R
import com.example.gymeaze.ui.theme.LocalDarkTheme
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getPrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymEazeTopAppBar(
    onSettingsClick: () -> Unit,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    showNotifications: Boolean = false,
    onNotificationsClick: () -> Unit = {},
    notificationCount: Int = 0
) {
    val isDarkTheme = LocalDarkTheme.current

    TopAppBar(
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = getOnSurfaceColor()
                    )
                }
            }
        },
        title = {
            if (showBackButton) {
                Text(
                    text = stringResource(R.string.user_profile),
                    color = getOnSurfaceColor(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(
                            id = if (isDarkTheme) {
                                R.drawable.gym_eaze_logo_light
                            } else {
                                R.drawable.gym_eaze_logo_dark
                            }
                        ),
                        contentDescription = stringResource(R.string.logo),
                        modifier = Modifier.height(64.dp)
                    )
                }
            }
        },
        actions = {
            if (showNotifications) {
                BadgedBox(
                    badge = {
                        if (notificationCount > 0) {
                            Badge(
                                containerColor = getPrimaryColor(),
                                contentColor = getOnSurfaceColor()
                            ) {
                                Text(
                                    text = if (notificationCount > 99) "99+" else notificationCount.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                ) {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = stringResource(R.string.notifications),
                            tint = getOnSurfaceColor()
                        )
                    }
                }
            }

            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings),
                    tint = getOnSurfaceColor()
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = getBackgroundColor()
        )
    )
}