package com.example.gymeaze.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.R
import com.example.gymeaze.ui.components.GymEazeBottomNavigationBar
import com.example.gymeaze.ui.components.LogoutConfirmationDialog
import com.example.gymeaze.ui.theme.GymEazeTheme
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getErrorColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import androidx.compose.runtime.setValue
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
    selectedRoute: String = "settings",
    onNavigationItemSelected: (String) -> Unit = {}
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
        bottomBar = {
            if (isUserLoggedIn) {
                GymEazeBottomNavigationBar(
                    selectedRoute = selectedRoute,
                    onItemSelected = onNavigationItemSelected
                )
            }
        },
        containerColor = getBackgroundColor()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(getBackgroundColor())
        ) {
            // Górne opcje w Column
            Column {
                // Motyw aplikacji - zawsze dostępny
                SettingsItem(
                    icon = Icons.Default.FormatPaint,
                    title = stringResource(R.string.theme), // "Motyw aplikacji"
                    onClick = onThemeClick
                )

                // Opcje tylko dla zalogowanych użytkowników
                if (isUserLoggedIn) {

                    onPrivacySettingsClick?.let { callback ->
                        SettingsItem(
                            icon = Icons.Default.Lock,
                            title = "Ustawienia prywatności",
                            onClick = callback
                        )
                    }

                    onHelpClick?.let { callback ->
                        SettingsItem(
                            icon = Icons.Default.Help,
                            title = "Jak zdobywać odznaki?",
                            onClick = callback
                        )
                    }

                    onNotificationsClick?.let { callback ->
                        SettingsItem(
                            icon = Icons.Default.Notifications,
                            title = "Powiadomienia",
                            onClick = callback
                        )
                    }
                }
            }

            // Spacer który wypycha "Wyloguj się" na dół
            if (isUserLoggedIn && onLogoutClick != null) {
                Spacer(modifier = Modifier.weight(1f))

                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = "Wyloguj się",
                    onClick = { showLogoutDialog = true },
                    isLogoutButton = true
                )
            }
        }

        // Dialog potwierdzenia wylogowania
        if (showLogoutDialog) {
            LogoutConfirmationDialog(
                onDismiss = { showLogoutDialog = false },
                onConfirm = {
                    showLogoutDialog = false
                    onLogoutClick?.invoke() // To wyloguje użytkownika
                    onReturnClick() // To zamknie SettingsScreen i wróci do głównego widoku
                }
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    isLogoutButton: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isLogoutButton) getErrorColor() else getOnSurfaceColor(),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            color = if (isLogoutButton) getErrorColor() else getOnSurfaceColor(),
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = getOnSurfaceSecondaryColor(),
            modifier = Modifier.size(24.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    GymEazeTheme {
        SettingsScreen(
            onReturnClick = {},
            onThemeClick = {}
        )
    }
}