package com.example.gymeaze.ui.screens.exercises

import androidx.compose.runtime.Composable
import com.example.gymeaze.ui.screens.ExercisesMenuScreen
import com.example.gymeaze.ui.screens.exercises.guest.ExercisesOverviewScreen

@Composable
fun ExerciseScreen(
    onSettingsClick: () -> Unit,
    isUserLoggedIn: Boolean,
    onCreateExerciseClick: () -> Unit = {}
) {
    if (isUserLoggedIn){
        ExercisesMenuScreen(onSettingsClick=onSettingsClick, onCreateExerciseClick =onCreateExerciseClick)
    } else {
        ExercisesOverviewScreen(onSettingsClick=onSettingsClick)
    }
}