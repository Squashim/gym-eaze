package com.example.gymeaze.ui.screens.exercises

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.ui.screens.ExerciseCreatorScreen
import com.example.gymeaze.ui.screens.ExerciseDetailScreen
import com.example.gymeaze.ui.screens.ExercisesMenuScreen
import com.example.gymeaze.ui.screens.ExercisesScreen
import com.example.gymeaze.ui.screens.exercises.guest.ExercisesOverviewScreen
import com.example.gymeaze.ui.screens.exercises.loggedIn.MyExercisesScreen
import com.example.gymeaze.ui.screens.exercises.logggedIn.FavoriteExercisesScreen
import com.example.gymeaze.viewmodel.ExerciseCreatorViewModel
import com.example.gymeaze.viewmodel.ExercisesViewModel

@Composable
fun ExerciseScreen(
    onSettingsClick: () -> Unit,
    isUserLoggedIn: Boolean,
    createExerciseViewModel: ExerciseCreatorViewModel,
    setShowBottomBar: (Boolean) -> Unit = {}
) {
    if (!isUserLoggedIn) {
        ExercisesOverviewScreen(onSettingsClick = onSettingsClick)
        return
    }

    var exerciseSection by remember { mutableStateOf("menu") }
    var selectedExerciseId by remember { mutableStateOf<String?>(null) }

    val exercisesViewModel: ExercisesViewModel = viewModel()

    LaunchedEffect(exerciseSection) {
        setShowBottomBar(exerciseSection == "menu")

        if (exerciseSection == "menu") {
            exercisesViewModel.resetScrollPosition()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when (exerciseSection) {
            "menu" -> ExercisesMenuScreen(
                onSettingsClick = onSettingsClick,
                onCreateExerciseClick = { exerciseSection = "create" },
                onMyExercisesClick = { exerciseSection = "myExercises" },
                onBrowseExercisesClick = { exerciseSection = "browse" },
                onFavoriteExercisesClick = { exerciseSection = "favorite" }
            )
            "create" -> ExerciseCreatorScreen(
                viewModel = createExerciseViewModel,
                onBackClick = { exerciseSection = "menu" },
                onCreated = { exerciseSection = "menu" }
            )
            "myExercises" -> MyExercisesScreen(
                onBackClick = { exerciseSection = "menu" }
            )
            "browse" -> ExercisesScreen(
                onSettingsClick = onSettingsClick,
                onBackClick = { exerciseSection = "menu" },
                onExerciseClick = { exerciseId ->
                    selectedExerciseId = exerciseId
                    exerciseSection = "details"
                },
                viewModel = exercisesViewModel
            )
            "details" -> {
                if (selectedExerciseId != null) {
                    ExerciseDetailScreen(
                        exerciseId = selectedExerciseId!!,
                        onBackClick = { exerciseSection = "browse" },
                        exercisesViewModel = exercisesViewModel
                    )
                }
            }
            "favorite" -> FavoriteExercisesScreen(
                onBackClick = { exerciseSection = "menu" }
            )
        }
    }
}