package com.example.gymeaze.ui.screens.profile.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.R
import com.example.gymeaze.ui.components.LogoutConfirmationDialog
import com.example.gymeaze.ui.components.settings.SettingsItem
import com.example.gymeaze.ui.components.settings.SettingsSection
import com.example.gymeaze.ui.theme.GymEazeTheme
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onReturnClick: () -> Unit,
    onThemeClick: () -> Unit,
    onPrivacySettingsClick: (() -> Unit)? = null,
    onNotificationsClick: (() -> Unit)? = null,
    onHelpClick: (() -> Unit)? = null,
    onLogoutClick: (() -> Unit)? = null,
    isUserLoggedIn: Boolean = false,
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_settings),
                        color = getOnSurfaceColor(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onReturnClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = getOnSurfaceColor()
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = getBackgroundColor()
                )
            )
        },
        bottomBar = {},
        containerColor = getBackgroundColor()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(getBackgroundColor())
        ) {

            SettingsSection(title = stringResource(R.string.appearance)) {
                SettingsItem(
                    icon = Icons.Default.FormatPaint,
                    title = stringResource(R.string.theme),
                    subtitle = stringResource(R.string.theme_subtitle),
                    onClick = onThemeClick
                )
            }

            if (isUserLoggedIn) {
                SettingsSection(title = stringResource(R.string.account)) {
                    onPrivacySettingsClick?.let { callback ->
                        SettingsItem(
                            icon = Icons.Default.Lock,
                            title = stringResource(R.string.privacy_settings),
                            subtitle = stringResource(R.string.privacy_settings_subtitle),
                            onClick = callback
                        )
                    }
                }

                SettingsSection(title = stringResource(R.string.help_and_support)) {
                    onHelpClick?.let { callback ->
                        SettingsItem(
                            icon = Icons.Default.Help,
                            title = stringResource(R.string.how_to_earn_badges),
                            subtitle = stringResource(R.string.badges_help_subtitle),
                            onClick = callback
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (isUserLoggedIn) {
                Column(
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Divider(
                        color = getOnSurfaceSecondaryColor().copy(alpha = 0.2f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    SettingsItem(
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        title = stringResource(R.string.logout),
                        onClick = { showLogoutDialog = true },
                        isLogoutButton = true
                    )
                }
            }
        }

        if (showLogoutDialog) {
            LogoutConfirmationDialog(
                onDismiss = { showLogoutDialog = false },
                onConfirm = {
                    showLogoutDialog = false
                    onLogoutClick?.invoke()
                    onReturnClick()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    GymEazeTheme {
        SettingsScreen(
            onReturnClick = {},
            onThemeClick = {},
            isUserLoggedIn = true,
            onPrivacySettingsClick = {},
            onNotificationsClick = {},
            onHelpClick = {},
            onLogoutClick = {}
        )
    }
}