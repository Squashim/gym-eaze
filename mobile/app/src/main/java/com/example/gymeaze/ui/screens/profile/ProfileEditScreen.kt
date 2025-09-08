package com.example.gymeaze.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.R
import com.example.gymeaze.ui.components.GymEazeBottomNavigationBar
import com.example.gymeaze.ui.screens.profile.sections.LoginDataSection
import com.example.gymeaze.ui.screens.profile.sections.PersonalDataSection
import com.example.gymeaze.ui.screens.profile.sections.PhysicalInfoSection
import com.example.gymeaze.ui.screens.profile.sections.ProfileInfoSection
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.UserViewModel

enum class ProfileTab {
    PROFILE_INFO,
    LOGIN_DATA,
    PERSONAL_DATA,
    PHYSICAL_INFO
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    viewModel: UserViewModel? = null,
    onReturnClick: () -> Unit,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    selectedRoute: String = "profile",
    onNavigationItemSelected: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(ProfileTab.PROFILE_INFO) }

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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(getBackgroundColor())
                .padding(paddingValues)
        ) {
            ProfileTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    ProfileTab.PROFILE_INFO -> {
                        if (viewModel != null) {
                            ProfileInfoSection(viewModel = viewModel)
                        } else {
                            ProfileInfoPreviewScreen()
                        }
                    }
                    ProfileTab.LOGIN_DATA -> {
                        LoginDataSection(viewModel = viewModel)
                    }
                    ProfileTab.PERSONAL_DATA -> {
                        PersonalDataSection(viewModel = viewModel)
                    }
                    ProfileTab.PHYSICAL_INFO -> {
                        PhysicalInfoSection(viewModel = viewModel)
                    }
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
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
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
private fun ProfileInfoPreviewScreen() {
    var biography by remember { mutableStateOf("") }
    var selectedGoal by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Profile Info Preview - Connect with ViewModel for full functionality",
            color = getOnSurfaceSecondaryColor()
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