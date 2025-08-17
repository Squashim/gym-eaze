package com.example.gymeaze.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.R
import com.example.gymeaze.ui.theme.BackgroundDark
import com.example.gymeaze.ui.theme.GymEazeTheme
import com.example.gymeaze.ui.theme.OnSurfaceDark
import com.example.gymeaze.ui.theme.OnSurfaceSecondary
import com.example.gymeaze.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectionScreen(
    isDarkTheme: Boolean,
    onReturnClick: () -> Unit,
    onThemeSelected: (Boolean) -> Unit,
    selectedRoute: String = "settings",
    onNavigationItemSelected: (String) -> Unit = {}
){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.theme),
                        color = OnSurfaceDark,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onReturnClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = OnSurfaceDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(BackgroundDark)
        ) {
            Text(
                text = stringResource(R.string.theme_choice),
                color = OnSurfaceDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(bottom=8.dp)
            )

            Text(
                text = stringResource(R.string.theme_choice_desc),
                color = OnSurfaceSecondary,
                lineHeight = 20.sp,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(bottom=10.dp)
            )
            ThemeOption(
                title=stringResource(R.string.dark),
                onClick = {onThemeSelected(true)},
                isSelected = isDarkTheme
            )

            ThemeOption(
                title=stringResource(R.string.light),
                onClick = {onThemeSelected(false)},
                isSelected = !isDarkTheme
            )
        }
    }
}

@Composable
private fun ThemeOption(
    title: String,
    onClick: () -> Unit,
    isSelected: Boolean

){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.scale(0.7f)) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = PrimaryGreen,
                    unselectedColor = OnSurfaceSecondary
                )
            )
        }

        Text(
            text = title,
            color = OnSurfaceDark,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ThemeSelectionScreenPreview() {
    GymEazeTheme {
        ThemeSelectionScreen(
            isDarkTheme = true,
            onReturnClick = {},
            onThemeSelected = {}
        )
    }
}