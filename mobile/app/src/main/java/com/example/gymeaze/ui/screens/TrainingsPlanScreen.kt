package com.example.gymeaze.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*


@Composable
fun TrainingsPlanScreen(
    onSettingsClick: () -> Unit,
    onCreateTrainingPlanClick: () -> Unit
) {
    TrainingPlansMenuScreen(
        onSettingsClick = onSettingsClick,
        onCreateTrainingPlanClick = onCreateTrainingPlanClick
    )
}