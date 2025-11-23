package com.example.gymeaze.ui.screens.profile.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.R
import com.example.gymeaze.ui.components.LoadingScreen
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsScreen(
    onReturnClick: () -> Unit,
    viewModel: UserViewModel
) {
    val privacySettings by viewModel.privacySettings.observeAsState()
    val isLoading by viewModel.privacySettingsLoading.observeAsState(false)
    val error by viewModel.privacySettingsError.observeAsState()
    val success by viewModel.privacySettingsSuccess.observeAsState()

    val personalDataEnabled by viewModel.personalDataEnabled.observeAsState(true)
    val physicalInfoEnabled by viewModel.physicalInfoEnabled.observeAsState(true)
    val trainingGoalsEnabled by viewModel.trainingGoalsEnabled.observeAsState(true)
    val badgeRemindersEnabled by viewModel.badgeRemindersEnabled.observeAsState(true)
    val trainingStatsEnabled by viewModel.trainingStatsEnabled.observeAsState(true)

    LaunchedEffect(Unit) {
        viewModel.fetchPrivacySettings()
    }

    val context = LocalContext.current

    LaunchedEffect(success) {
        success?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearPrivacySettingsSuccess()
        }
    }

    LaunchedEffect(error) {
        error?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearPrivacySettingsError()
        }
    }

    if (isLoading) {
        LoadingScreen()
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.privacy_settings),
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
                    .verticalScroll(rememberScrollState())
                    .background(getBackgroundColor())
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.privacy_settings_change),
                        color = getOnSurfaceColor(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = stringResource(R.string.privacy_settings_description),
                        color = getOnSurfaceSecondaryColor(),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }

                Divider(
                    color = getOnSurfaceSecondaryColor().copy(alpha = 0.2f),
                    thickness = 1.dp
                )

                PrivacySwitchItem(
                    title = stringResource(R.string.personal_data),
                    description = stringResource(R.string.personal_data_description_settings),
                    isChecked = personalDataEnabled,
                    isLoading = isLoading,
                    onCheckedChange = {
                        viewModel.updatePersonalDataEnabled(it)
                        viewModel.updatePrivacySettings()
                    }
                )

                PrivacySwitchItem(
                    title = stringResource(R.string.physical_info),
                    description = stringResource(R.string.physical_info_description_settings),
                    isChecked = physicalInfoEnabled,
                    isLoading = isLoading,
                    onCheckedChange = {
                        viewModel.updatePhysicalInfoEnabled(it)
                        viewModel.updatePrivacySettings()
                    }
                )

                PrivacySwitchItem(
                    title = stringResource(R.string.training_goals),
                    description = stringResource(R.string.training_goals_description_settings),
                    isChecked = trainingGoalsEnabled,
                    isLoading = isLoading,
                    onCheckedChange = {
                        viewModel.updateTrainingGoalsEnabled(it)
                        viewModel.updatePrivacySettings()
                    }
                )

                PrivacySwitchItem(
                    title = stringResource(R.string.badge_reminders),
                    description = stringResource(R.string.badge_reminders_description_settings),
                    isChecked = badgeRemindersEnabled,
                    isLoading = isLoading,
                    onCheckedChange = {
                        viewModel.updateBadgeRemindersEnabled(it)
                        viewModel.updatePrivacySettings()
                    }
                )

                PrivacySwitchItem(
                    title = stringResource(R.string.training_stats),
                    description = stringResource(R.string.training_stats_description_settings),
                    isChecked = trainingStatsEnabled,
                    isLoading = isLoading,
                    onCheckedChange = {
                        viewModel.updateTrainingStatsEnabled(it)
                        viewModel.updatePrivacySettings()
                    }
                )
            }
        }
    }
}

@Composable
private fun PrivacySwitchItem(
    title: String,
    description: String,
    isChecked: Boolean,
    isLoading: Boolean = false,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = if (isLoading) getOnSurfaceSecondaryColor() else getOnSurfaceColor(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = getPrimaryColor(),
                    strokeWidth = 2.dp
                )
            } else {
                Switch(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = getPrimaryColor(),
                        checkedTrackColor = getPrimaryColor().copy(alpha = 0.5f),
                        uncheckedThumbColor = getOnSurfaceSecondaryColor(),
                        uncheckedTrackColor = getOnSurfaceSecondaryColor().copy(alpha = 0.3f)
                    )
                )
            }
        }

        Text(
            text = description,
            color = getOnSurfaceSecondaryColor(),
            fontSize = 14.sp,
            lineHeight = 18.sp,
            modifier = Modifier.padding(top = 4.dp, end = 56.dp)
        )
    }
}