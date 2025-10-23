package com.example.gymeaze.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.gymeaze.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymeaze.ui.theme.GymEazeTheme
import com.example.gymeaze.ui.theme.LocalDarkTheme
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymEazeTopAppBar(
    onSettingsClick: () -> Unit
){

    val isDarkTheme = LocalDarkTheme.current

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Image(
                    painter = painterResource(
                        id = if(isDarkTheme){
                            R.drawable.gym_eaze_logo_light
                        } else {
                            R.drawable.gym_eaze_logo_dark
                        }),
                    contentDescription = stringResource(R.string.logo),
                    modifier = Modifier
                        .height(64.dp)
                    ,
                )
            }
        },
        actions = {
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

@Preview(showBackground = true)
@Composable
fun GymEazeTopAppBarPreview() {
    GymEazeTheme {
        GymEazeTopAppBar(onSettingsClick = {})
    }
}