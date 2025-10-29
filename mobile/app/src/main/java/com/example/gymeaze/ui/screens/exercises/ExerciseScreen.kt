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
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.ui.screens.exercises.logggedIn.ExerciseCreatorScreen
import com.example.gymeaze.ui.screens.exercises.logggedIn.ExerciseDetailScreen
import com.example.gymeaze.ui.screens.ExercisesMenuScreen
import com.example.gymeaze.ui.screens.exercises.logggedIn.ExercisesScreen
import com.example.gymeaze.ui.screens.FavoriteExercisesScreen
import com.example.gymeaze.ui.screens.exercises.guest.ExercisesOverviewScreen
import com.example.gymeaze.ui.screens.exercises.loggedIn.MyExercisesScreen
import com.example.gymeaze.viewmodel.ExerciseCreatorViewModel
import com.example.gymeaze.viewmodel.ExercisesViewModel

@Composable
fun ExerciseScreen(
    onSettingsClick: () -> Unit,
    isUserLoggedIn: Boolean,
    createExerciseViewModel: ExerciseCreatorViewModel,
    setShowBottomBar: (Boolean) -> Unit = {},
    onUserClick: (String) -> Unit = {},
    initialExerciseSection: String = "menu",
    initialSelectedExerciseId: String? = null,
    onNavigationStateChanged: (section: String, exerciseId: String?) -> Unit = { _, _ -> }
) {
    val exercisesViewModel: ExercisesViewModel = viewModel()

    if (!isUserLoggedIn) {
        var selectedExerciseId by remember { mutableStateOf<String?>(null) }

        when {
            selectedExerciseId != null -> {
                ExerciseDetailScreen(
                    exerciseId = selectedExerciseId!!,
                    onBackClick = {
                        selectedExerciseId = null
                    },
                    onEditClick = null,
                    onDeleteSuccess = {
                        selectedExerciseId = null
                    },
                    exercisesViewModel = exercisesViewModel,
                    isUserLoggedIn = false,
                    onUserClick = onUserClick
                )
            }
            else -> {
                ExercisesOverviewScreen(
                    onSettingsClick = onSettingsClick,
                    onExerciseClick = { exerciseId ->
                        selectedExerciseId = exerciseId
                    },
                    viewModel = exercisesViewModel
                )
            }
        }
        return
    }

    var exerciseSection by remember { mutableStateOf(initialExerciseSection) }
    var selectedExerciseId by remember { mutableStateOf(initialSelectedExerciseId) }
    var returnToSection by remember { mutableStateOf("menu") }
    var selectedExerciseForEdit by remember { mutableStateOf<Exercise?>(null) }

    LaunchedEffect(exerciseSection, selectedExerciseId) {
        onNavigationStateChanged(exerciseSection, selectedExerciseId)
    }

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
                onCreated = { exerciseSection = "menu" },
                exerciseToEdit = null
            )

            "edit" -> ExerciseCreatorScreen(
                viewModel = createExerciseViewModel,
                onBackClick = {
                    selectedExerciseForEdit = null
                    exerciseSection = returnToSection
                },
                onCreated = {
                    selectedExerciseForEdit = null
                    exerciseSection = returnToSection
                    exercisesViewModel.reloadUserDataForExercises()
                },
                exerciseToEdit = selectedExerciseForEdit
            )

            "myExercises" -> MyExercisesScreen(
                onBackClick = { exerciseSection = "menu" },
                onExerciseClick = { exerciseId ->
                    selectedExerciseId = exerciseId
                    returnToSection = "myExercises"
                    exerciseSection = "details"
                },
                onEditExercise = { exercise ->
                    selectedExerciseForEdit = exercise
                    returnToSection = "myExercises"
                    exerciseSection = "edit"
                },
                viewModel = exercisesViewModel
            )

            "browse" -> ExercisesScreen(
                onSettingsClick = onSettingsClick,
                onBackClick = { exerciseSection = "menu" },
                onExerciseClick = { exerciseId ->
                    selectedExerciseId = exerciseId
                    returnToSection = "browse"
                    exerciseSection = "details"
                },
                onEditExercise = { exercise ->
                    selectedExerciseForEdit = exercise
                    returnToSection = "browse"
                    exerciseSection = "edit"
                },
                viewModel = exercisesViewModel
            )

            "details" -> {
                if (selectedExerciseId != null) {
                    ExerciseDetailScreen(
                        exerciseId = selectedExerciseId!!,
                        onBackClick = {
                            selectedExerciseId = null
                            exerciseSection = returnToSection
                        },
                        onEditClick = { exercise ->
                            selectedExerciseForEdit = exercise
                            exerciseSection = "edit"
                        },
                        onDeleteSuccess = {
                            selectedExerciseId = null
                            exerciseSection = returnToSection
                            when (returnToSection) {
                                "myExercises" -> {
                                    val userId = exercisesViewModel.currentUser.value?._id
                                    userId?.let { exercisesViewModel.refreshUserExercises(it) }
                                }
                                "browse" -> exercisesViewModel.refreshExercises()
                                "favorite" -> exercisesViewModel.refreshFavoriteExercises()
                            }
                        },
                        exercisesViewModel = exercisesViewModel,
                        onUserClick = onUserClick
                    )
                }
            }

            "favorite" -> FavoriteExercisesScreen(
                onBackClick = { exerciseSection = "menu" },
                onExerciseClick = { exerciseId ->
                    selectedExerciseId = exerciseId
                    returnToSection = "favorite"
                    exerciseSection = "details"
                },
                viewModel = exercisesViewModel
            )
        }
    }
}