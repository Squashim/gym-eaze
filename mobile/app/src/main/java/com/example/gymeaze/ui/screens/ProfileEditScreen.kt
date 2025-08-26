package com.example.gymeaze.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.R
import com.example.gymeaze.ui.components.GymEazeBottomNavigationBar
import com.example.gymeaze.ui.theme.*

enum class ProfileTab {
    PROFILE_INFO,
    LOGIN_DATA,
    PERSONAL_DATA,
    PHYSICAL_INFO
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    onReturnClick: () -> Unit,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    selectedRoute: String = "profile",
    onNavigationItemSelected: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(ProfileTab.PROFILE_INFO) }
    var biography by remember { mutableStateOf("")}
    var selectedGoal by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.profile_edit),
                        color = getOnSurfaceColor(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onReturnClick){
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
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(getBackgroundColor())
                .padding(paddingValues)
        ) {
            ProfileTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it}
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when(selectedTab) {
                    ProfileTab.PROFILE_INFO -> {
                        ProfileInfoContent(
                            biography = biography,
                            onBiographyChange = {biography = it},
                            selectedGoal = selectedGoal,
                            onGoalChange = { selectedGoal = it}
                        )
                    }
                    ProfileTab.LOGIN_DATA -> {
                        LoginDataContent()
                    }
                    ProfileTab.PERSONAL_DATA -> {
                        PersonalDataContent()
                    }
                    ProfileTab.PHYSICAL_INFO -> {
                        PhysicalInfoContent()
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = Modifier
                        .weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = getOnSurfaceColor().copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        color = getOnSurfaceColor()
                    )
                }

                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getOnSurfaceColor()
                    )
                ) {
                    Text(
                        text = stringResource(R.string.save),
                        color = getBackgroundColor()
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileTabs(
    selectedTab: ProfileTab,
    onTabSelected: (ProfileTab) -> Unit
) {
    val scrollState = rememberScrollState()
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(getBackgroundColor())
                .horizontalScroll(scrollState)
                .padding(horizontal = 8.dp)
        ) {
            ProfileTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab

                Box(
                    modifier = Modifier
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 4.dp)
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    width = 2.dp,
                                    color = getPrimaryColor(),
                                    shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
                                )
                            } else Modifier
                        )
                ) {
                    Text(
                        text = when (tab) {
                            ProfileTab.PROFILE_INFO -> stringResource(R.string.profile_info)
                            ProfileTab.LOGIN_DATA -> stringResource(R.string.login_data)
                            ProfileTab.PERSONAL_DATA -> stringResource(R.string.personal_data)
                            ProfileTab.PHYSICAL_INFO -> stringResource(R.string.physical_info)
                        },
                        modifier = Modifier
                            .padding(vertical = 12.dp, horizontal = 12.dp),
                        color = if (isSelected) getPrimaryColor() else getOnSurfaceSecondaryColor(),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = getOnSurfaceSecondaryColor().copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun ProfileInfoContent(
    biography: String,
    onBiographyChange: (String) -> Unit,
    selectedGoal: String,
    onGoalChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ProfilePictureSection()

        BiographySection(
            biography = biography,
            onBiographyChange = onBiographyChange
        )

        TrainingGoalSection(
            selectedGoal = selectedGoal,
            onGoalChange = onGoalChange
        )

        BadgesSection()
    }
}

@Composable
private fun ProfilePictureSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.profile_picture),
            color = getOnSurfaceColor(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(getSurfaceColor())
                    .border(1.dp, getOnSurfaceSecondaryColor().copy(alpha = 0.3f), CircleShape)
                    .clickable {
                        //TODO Implementacja wyboru zdjecia
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = getOnSurfaceSecondaryColor(),
                    modifier = Modifier
                        .size(24.dp)
                )
            }

            Text(
                text = stringResource(R.string.no_picture_selected),
                color = getOnSurfaceSecondaryColor(),
                fontSize = 14.sp
            )
        }

        Text(
            text = stringResource(R.string.picture_requirements),
            color = getOnSurfaceSecondaryColor(),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun BiographySection(
    biography: String,
    onBiographyChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.biography),
            color = getOnSurfaceColor(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        OutlinedTextField(
            value = biography,
            onValueChange = { newValue ->
                if(newValue.length <= 250) {
                    onBiographyChange(newValue)
                }
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.biography_placeholder),
                    color = getOnSurfaceSecondaryColor()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = getPrimaryColor(),
                unfocusedBorderColor = getInputBorderColor(),
                focusedTextColor = getOnSurfaceColor(),
                unfocusedTextColor = getOnSurfaceColor(),
                cursorColor = getPrimaryColor()
            ),
            shape = RoundedCornerShape(8.dp),
            maxLines = 5
        )

        Text(
            text = stringResource(R.string.biography_limit_current, biography.length),
            color = getOnSurfaceSecondaryColor(),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun TrainingGoalSection(
    selectedGoal: String,
    onGoalChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.training_goal),
                color = getOnSurfaceColor(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = getOnSurfaceSecondaryColor(),
                modifier = Modifier
                    .size(16.dp)
            )
        }

        OutlinedTextField(
            value = selectedGoal,
            onValueChange = onGoalChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.select_training_goal),
                    color = getOnSurfaceSecondaryColor()
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = getPrimaryColor(),
                unfocusedBorderColor = getInputBorderColor(),
                focusedTextColor = getOnSurfaceColor(),
                unfocusedTextColor = getOnSurfaceColor(),
                cursorColor = getPrimaryColor()
            ),
            shape = RoundedCornerShape(8.dp),
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = getOnSurfaceSecondaryColor(),
                    modifier = Modifier
                        .clickable {
                            //TODO Implementacja wyboru celu treningowego
                        }
                        .size(24.dp)
                )
            }
        )
    }
}

@Composable
private fun BadgesSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.pinned_badges),
                color = getOnSurfaceColor(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = getOnSurfaceSecondaryColor(),
                modifier = Modifier.size(16.dp)
            )
        }

        OutlinedButton(
            onClick = {
                //TODO Implementacja przypiecia odznak
            },
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent
            ),
            border = BorderStroke(
                color = getOnSurfaceColor().copy(alpha = 0.5f),
                width = 1.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = getOnSurfaceColor(),
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = stringResource(R.string.no_pinned_badges),
                    color = getOnSurfaceColor()
                )
            }
        }

        Text(
            text = stringResource(R.string.badges_limit),
            color = getOnSurfaceSecondaryColor(),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun LoginDataContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "TODO: Dane logowania",
            color = getOnSurfaceSecondaryColor(),
            fontSize = 16.sp
        )
    }
}

@Composable
private fun PersonalDataContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "TODO: Dane personalne",
            color = getOnSurfaceSecondaryColor(),
            fontSize = 16.sp
        )
    }
}

@Composable
private fun PhysicalInfoContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "TODO: Informacje fizyczne",
            color = getOnSurfaceSecondaryColor(),
            fontSize = 16.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileEditScreenPreview() {
    GymEazeTheme {
        ProfileEditScreen(
            onReturnClick = {},
            onCancelClick = {},
            onSaveClick = {}
        )
    }
}