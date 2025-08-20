package com.example.gymeaze.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.R
import com.example.gymeaze.ui.components.GymEazeBottomNavigationBar
import com.example.gymeaze.ui.components.GymEazeTopAppBar
import com.example.gymeaze.ui.theme.BackgroundDark
import com.example.gymeaze.ui.theme.GymEazeTheme
import com.example.gymeaze.ui.theme.OnSurfaceDark
import com.example.gymeaze.ui.theme.OnSurfaceSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroductionScreen(
    username: String,
    onSkipIntroduction: () -> Unit,
    onCompleteIntroduction: () -> Unit,
    onSettingsClick: () -> Unit
){
    Scaffold(
        topBar = {
            GymEazeTopAppBar(onSettingsClick = onSettingsClick)
        },
        bottomBar = {
            GymEazeBottomNavigationBar(
                selectedRoute = "",
                onItemSelected = {}
            )
        },
        containerColor = BackgroundDark
    ) {
        paddingValues -> Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundDark),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .border(
                        width = 1.dp,
                        color = OnSurfaceDark.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundDark)
            )
                {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(
                            text = stringResource(R.string.welcome_user, username),
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceDark,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = stringResource(R.string.desc),
                            color = OnSurfaceSecondary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Left,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TextButton(
                                onClick = onSkipIntroduction,
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                    width = 1.dp,
                                    color = OnSurfaceDark.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp),
                                ),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BackgroundDark
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.skip),
                                    color = OnSurfaceDark,
                                    fontSize = 14.sp
                                )
                            }

                        TextButton(
                            onClick = onCompleteIntroduction,
                            modifier = Modifier
                                .weight(1.1f)
                                .border(
                                    width = 1.dp,
                                    color = BackgroundDark.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OnSurfaceDark
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ){
                            Text(
                                text = stringResource(R.string.continue_further),
                                color = BackgroundDark,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IntroductionScreenPreview(){
    GymEazeTheme {
        IntroductionScreen(
            username = "TestUser",
            onCompleteIntroduction = {},
            onSkipIntroduction = {},
            onSettingsClick = {}
        )
    }
}