package com.example.gymeaze.ui.screens.trainingsPlan

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.R
import com.example.gymeaze.ui.screens.FavoriteTrainingPlansScreen
import com.example.gymeaze.ui.screens.MyTrainingPlansScreen
import com.example.gymeaze.ui.screens.TrainingPlanDetailScreen
import com.example.gymeaze.ui.screens.TrainingPlansMenuScreen
import com.example.gymeaze.ui.screens.TrainingPlansScreen
import com.example.gymeaze.ui.screens.exercises.logggedIn.ExerciseDetailScreen
import com.example.gymeaze.ui.theme.LocalDarkTheme
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.viewmodel.ExercisesViewModel
import com.example.gymeaze.viewmodel.FavoriteTrainingPlansViewModel
import com.example.gymeaze.viewmodel.MyTrainingPlansViewModel
import com.example.gymeaze.viewmodel.TrainingPlansViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingPlansNavigationScreen(
    onSettingsClick: () -> Unit,
    onCreateTrainingPlanClick: () -> Unit,
    onPlanClick: (String) -> Unit,
    onEditPlanClick: (String) -> Unit,
    onStartedPlanClick: (userTrainingPlanId: String, planName: String) -> Unit,
    setShowBottomBar: (Boolean) -> Unit = {}
) {
    val trainingPlansViewModel: TrainingPlansViewModel = viewModel()
    val myPlansViewModel: MyTrainingPlansViewModel = viewModel()
    val favoritePlansViewModel: FavoriteTrainingPlansViewModel = viewModel()
    val exercisesViewModel: ExercisesViewModel = viewModel()

    var planSection by remember { mutableStateOf("menu") }
    var selectedPlanId by remember { mutableStateOf<String?>(null) }
    var selectedExerciseId by remember { mutableStateOf<String?>(null) }
    var returnToSection by remember { mutableStateOf("menu") }
    var returnFromExercise by remember { mutableStateOf(false) }

    val isDarkTheme = LocalDarkTheme.current

    LaunchedEffect(planSection) {
        setShowBottomBar(planSection == "menu")

        if (planSection == "menu") {
        }
    }

    Scaffold(
        topBar = {
            if (planSection == "menu") {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(
                                    id = if (isDarkTheme) {
                                        R.drawable.gym_eaze_logo_light
                                    } else {
                                        R.drawable.gym_eaze_logo_dark
                                    }
                                ),
                                contentDescription = stringResource(R.string.logo),
                                modifier = Modifier.height(64.dp)
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
        },
        containerColor = getBackgroundColor()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (planSection == "menu") paddingValues else PaddingValues(0.dp))
        ) {
            when (planSection) {
                "menu" -> TrainingPlansMenuScreen(
                    onSettingsClick = onSettingsClick,
                    onCreateTrainingPlanClick = onCreateTrainingPlanClick,
                    onMyTrainingPlansClick = { planSection = "my_plans" },
                    onBrowseTrainingPlansClick = { planSection = "browse" },
                    onFavoriteTrainingPlansClick = { planSection = "favorites" }
                )

                "my_plans" -> MyTrainingPlansScreen(
                    onBackClick = { planSection = "menu" },
                    onPlanClick = { planId ->
                        selectedPlanId = planId
                        returnToSection = "my_plans"
                        planSection = "details"
                    },
                    onEditPlanClick = { planId ->
                        selectedPlanId = planId
                        returnToSection = "my_plans"
                        onEditPlanClick(planId)
                    },
                    onCreatePlanClick = onCreateTrainingPlanClick,
                    onSettingsClick = onSettingsClick
                )

                "browse" -> TrainingPlansScreen(
                    onBackClick = { planSection = "menu" },
                    onPlanClick = { planId ->
                        selectedPlanId = planId
                        returnToSection = "browse"
                        planSection = "details"
                    },
                    onSettingsClick = onSettingsClick,
                    viewModel = trainingPlansViewModel
                )

                "favorites" -> FavoriteTrainingPlansScreen(
                    onBackClick = { planSection = "menu" },
                    onPlanClick = { planId ->
                        selectedPlanId = planId
                        returnToSection = "favorites"
                        planSection = "details"
                    },
                    onSettingsClick = onSettingsClick
                )

                "details" -> {
                    if (selectedPlanId != null) {
                        TrainingPlanDetailScreen(
                            planId = selectedPlanId!!,
                            onBackClick = {
                                selectedPlanId = null
                                planSection = returnToSection
                            },
                            onExerciseClick = { exerciseId ->
                                selectedExerciseId = exerciseId
                                returnFromExercise = true
                                planSection = "exercise_details"
                            },
                            onUserClick = { userId ->
                            },
                            navigateToWorkoutPlayer = { userTrainingPlanId, planName, dayNumber ->
                                onStartedPlanClick(userTrainingPlanId, planName)
                            }
                        )
                    }
                }

                "exercise_details" -> {
                    if (selectedExerciseId != null) {
                        ExerciseDetailScreen(
                            exerciseId = selectedExerciseId!!,
                            onBackClick = {
                                selectedExerciseId = null
                                if (returnFromExercise) {
                                    planSection = "details"
                                    returnFromExercise = false
                                } else {
                                    planSection = returnToSection
                                }
                            },
                            onEditClick = null,
                            onDeleteSuccess = {
                                selectedExerciseId = null
                                planSection = "details"
                            },
                            exercisesViewModel = exercisesViewModel,
                            isUserLoggedIn = true
                        )
                    }
                }
            }
        }
    }
}