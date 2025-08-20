package com.example.gymeaze.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.example.gymeaze.ui.theme.GymEazeTheme
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onReturnClick: () -> Unit,
    onThemeClick: () -> Unit,
    selectedRoute: String = "settings",
    onNavigationItemSelected: (String) -> Unit = {}
) {
    Scaffold(topBar = {
        TopAppBar(title = {
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
            GymEazeBottomNavigationBar(
                selectedRoute = selectedRoute,
                onItemSelected = onNavigationItemSelected
            )
    },
        containerColor = getBackgroundColor()
    ) { paddingValues -> Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(getBackgroundColor())
    ) {
        SettingsItem(
            icon = Icons.Default.FormatPaint,
            title = stringResource(R.string.theme),
            onClick = onThemeClick
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{ onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = getOnSurfaceColor(),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            color = getOnSurfaceColor(),
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