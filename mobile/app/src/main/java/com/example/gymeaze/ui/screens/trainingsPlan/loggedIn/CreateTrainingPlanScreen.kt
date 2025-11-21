package com.example.gymeaze.ui.screens.trainingsPlan.loggedIn

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.gymeaze.constants.PrivacySettings
import com.example.gymeaze.ui.components.GoalSelectionDialog
import com.example.gymeaze.ui.components.DifficultySelectionDialog
import com.example.gymeaze.ui.components.EmptyStateView
import com.example.gymeaze.ui.components.HashtagInputField
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.trainingsPlan.CreateTrainingPlanState
import com.example.gymeaze.viewmodel.trainingsPlan.CreateTrainingPlanViewModel
import com.example.gymeaze.viewmodel.trainingsPlan.DayExercise
import com.example.gymeaze.viewmodel.trainingsPlan.TrainingDay
import com.example.gymeaze.viewmodel.trainingsPlan.TrainingPlanCreatorStep
import com.example.gymeaze.viewmodel.trainingsPlan.TrainingSet

@Composable
private fun RestDayConversionWarningDialog(
    dayName: String,
    exercisesCount: Int,
    totalSetsCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFFF9800),
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Zmiana na dzień odpoczynku",
                fontWeight = FontWeight.Bold,
                color = getOnSurfaceColor(),
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "Czy na pewno chcesz zmienić dzień:",
                    fontSize = 14.sp,
                    color = getOnSurfaceColor()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"$dayName\"",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = getPrimaryColor()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "na dzień odpoczynku?",
                    fontSize = 14.sp,
                    color = getOnSurfaceColor()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFF9800).copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFF9800).copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "OSTRZEŻENIE - Utrata danych",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9800)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Ta akcja spowoduje TRWAŁE usunięcie:",
                            fontSize = 12.sp,
                            color = getOnSurfaceColor(),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "•",
                                    fontSize = 14.sp,
                                    color = Color(0xFFFF9800),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "$exercisesCount ćwiczeń",
                                    fontSize = 12.sp,
                                    color = getOnSurfaceColor()
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "•",
                                    fontSize = 14.sp,
                                    color = Color(0xFFFF9800),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "$totalSetsCount serii z wagami i powtórzeniami",
                                    fontSize = 12.sp,
                                    color = getOnSurfaceColor()
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "•",
                                    fontSize = 14.sp,
                                    color = Color(0xFFFF9800),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Wszystkie ustawienia czasów odpoczynku",
                                    fontSize = 12.sp,
                                    color = getOnSurfaceColor()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Tej akcji nie można cofnąć!",
                            fontSize = 13.sp,
                            color = getErrorColor(),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = getPrimaryColor(),
                    contentColor = getOnPrimaryColor()
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tak, zmień na odpoczynek",
                        fontSize = 13.sp
                    )
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = getOnSurfaceColor()
                ),
                border = BorderStroke(1.dp, getOnSurfaceColor().copy(alpha = 0.3f))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Anuluj")
                }
            }
        },
        containerColor = getSurfaceColor(),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun DeleteDayConfirmationDialog(
    dayName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = getErrorColor(),
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Usuń dzień treningowy",
                fontWeight = FontWeight.Bold,
                color = getOnSurfaceColor()
            )
        },
        text = {
            Column {
                Text(
                    text = "Czy na pewno chcesz usunąć dzień:",
                    fontSize = 14.sp,
                    color = getOnSurfaceColor()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"$dayName\"",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = getPrimaryColor()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Ta akcja spowoduje trwałe usunięcie dnia wraz ze wszystkimi ćwiczeniami i seriami.",
                    fontSize = 12.sp,
                    color = getErrorColor(),
                    lineHeight = 16.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = getErrorColor(),
                    contentColor = Color.White
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Usuń")
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = getOnSurfaceColor()
                ),
                border = BorderStroke(1.dp, getOnSurfaceColor().copy(alpha = 0.3f))
            ) {
                Text("Anuluj")
            }
        },
        containerColor = getSurfaceColor(),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun DeleteExerciseConfirmationDialog(
    exerciseName: String,
    dayName: String,
    setsCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = getErrorColor(),
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Usuń ćwiczenie",
                fontWeight = FontWeight.Bold,
                color = getOnSurfaceColor()
            )
        },
        text = {
            Column {
                Text(
                    text = "Czy na pewno chcesz usunąć ćwiczenie:",
                    fontSize = 14.sp,
                    color = getOnSurfaceColor()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"$exerciseName\"",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = getPrimaryColor()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "z dnia: \"$dayName\"",
                    fontSize = 12.sp,
                    color = getOnSurfaceSecondaryColor()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = getErrorColor().copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = getErrorColor(),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Zostaną usunięte:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = getErrorColor()
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Wszystkie $setsCount serii\n• Ustawienia wagi i powtórzeń\n• Czas odpoczynku",
                            fontSize = 11.sp,
                            color = getErrorColor(),
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = getErrorColor(),
                    contentColor = Color.White
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Usuń")
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = getOnSurfaceColor()
                ),
                border = BorderStroke(1.dp, getOnSurfaceColor().copy(alpha = 0.3f))
            ) {
                Text("Anuluj")
            }
        },
        containerColor = getSurfaceColor(),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun DeleteSetConfirmationDialog(
    setIndex: Int,
    exerciseName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = getErrorColor(),
                modifier = Modifier.size(28.dp)
            )
        },
        title = {
            Text(
                text = "Usuń serię",
                fontWeight = FontWeight.Bold,
                color = getOnSurfaceColor()
            )
        },
        text = {
            Column {
                Text(
                    text = "Czy na pewno chcesz usunąć serię ${setIndex + 1}?",
                    fontSize = 14.sp,
                    color = getOnSurfaceColor()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ćwiczenie: \"$exerciseName\"",
                    fontSize = 12.sp,
                    color = getOnSurfaceSecondaryColor()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ustawienia wagi i powtórzeń zostaną utracone.",
                    fontSize = 11.sp,
                    color = getErrorColor()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = getErrorColor(),
                    contentColor = Color.White
                )
            ) {
                Text("Usuń")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = getOnSurfaceColor()
                ),
                border = BorderStroke(1.dp, getOnSurfaceColor().copy(alpha = 0.3f))
            ) {
                Text("Anuluj")
            }
        },
        containerColor = getSurfaceColor(),
        shape = RoundedCornerShape(12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTrainingPlanScreen(
    viewModel: CreateTrainingPlanViewModel,
    onBackClick: () -> Unit,
    onNavigateToExerciseSelection: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    var showDeleteDayDialog by remember { mutableStateOf(false) }
    var showDeleteExerciseDialog by remember { mutableStateOf(false) }
    var showDeleteSetDialog by remember { mutableStateOf(false) }

    var dayToDelete by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var exerciseToDelete by remember { mutableStateOf<Triple<Int, Int, String>?>(null) }
    var setToDelete by remember { mutableStateOf<Triple<Int, Int, Int>?>(null) }

    var showRestDayWarningDialog by remember { mutableStateOf(false) }
    var dayToConvertToRest by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(uiState.validationToast) {
        uiState.validationToast?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long
            )
            viewModel.clearValidationToast()
        }
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            showSuccessDialog = true
            viewModel.clearSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kreator planu treningowego",
                        color = getOnSurfaceColor(),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wstecz",
                            tint = getOnSurfaceColor()
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = getBackgroundColor()
                )
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    CustomErrorSnackbar(
                        message = data.visuals.message,
                        onDismiss = { data.dismiss() }
                    )
                }
            )
        },
        containerColor = getBackgroundColor()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TrainingPlanStepper(
                currentStep = uiState.currentStep,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (uiState.currentStep) {
                    TrainingPlanCreatorStep.BASIC -> BasicInfoStep(uiState, viewModel)
                    TrainingPlanCreatorStep.DAYS_AND_EXERCISES -> DaysAndExercisesStep(
                        uiState = uiState,
                        viewModel = viewModel,
                        onNavigateToExerciseSelection = onNavigateToExerciseSelection,
                        onRequestDeleteDay = { dayIndex ->
                            val day = uiState.days[dayIndex]
                            dayToDelete = dayIndex to day.name
                            showDeleteDayDialog = true
                        },
                        onRequestDeleteExercise = { dayIndex, exerciseIndex ->
                            val day = uiState.days[dayIndex]
                            val exercise = day.exercises[exerciseIndex]
                            exerciseToDelete = Triple(dayIndex, exerciseIndex, exercise.exercise.name)
                            showDeleteExerciseDialog = true
                        },
                        onRequestDeleteSet = { dayIndex, exerciseIndex, setIndex ->
                            setToDelete = Triple(dayIndex, exerciseIndex, setIndex)
                            showDeleteSetDialog = true
                        },
                        onRequestRestDayConversion = { dayIndex ->
                            dayToConvertToRest = dayIndex
                            showRestDayWarningDialog = true
                        }
                    )
                    TrainingPlanCreatorStep.SUMMARY -> SummaryStep(uiState)
                }
            }

            TrainingPlanBottomBar(
                step = uiState.currentStep,
                isLoading = uiState.isLoading,
                onPrev = viewModel::prevStep,
                onNext = viewModel::nextStep,
                onCreate = viewModel::createTrainingPlan,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    if (showRestDayWarningDialog) {
        dayToConvertToRest?.let { dayIndex ->
            val day = uiState.days[dayIndex]
            val totalSets = day.exercises.sumOf { it.sets.size }

            RestDayConversionWarningDialog(
                dayName = day.name,
                exercisesCount = day.exercises.size,
                totalSetsCount = totalSets,
                onConfirm = {
                    viewModel.toggleRestDay(dayIndex)
                    showRestDayWarningDialog = false
                    dayToConvertToRest = null
                },
                onDismiss = {
                    showRestDayWarningDialog = false
                    dayToConvertToRest = null
                }
            )
        }
    }

    if (showDeleteDayDialog) {
        dayToDelete?.let { (dayIndex, dayName) ->
            DeleteDayConfirmationDialog(
                dayName = dayName,
                onConfirm = {
                    viewModel.removeDay(dayIndex)
                    showDeleteDayDialog = false
                    dayToDelete = null
                },
                onDismiss = {
                    showDeleteDayDialog = false
                    dayToDelete = null
                }
            )
        }
    }

    if (showDeleteExerciseDialog) {
        exerciseToDelete?.let { (dayIndex, exerciseIndex, exerciseName) ->
            val day = uiState.days[dayIndex]
            val exercise = day.exercises[exerciseIndex]

            DeleteExerciseConfirmationDialog(
                exerciseName = exerciseName,
                dayName = day.name,
                setsCount = exercise.sets.size,
                onConfirm = {
                    viewModel.removeExerciseFromDay(dayIndex, exerciseIndex)
                    showDeleteExerciseDialog = false
                    exerciseToDelete = null
                },
                onDismiss = {
                    showDeleteExerciseDialog = false
                    exerciseToDelete = null
                }
            )
        }
    }

    if (showDeleteSetDialog) {
        setToDelete?.let { (dayIndex, exerciseIndex, setIndex) ->
            val day = uiState.days[dayIndex]
            val exercise = day.exercises[exerciseIndex]

            DeleteSetConfirmationDialog(
                setIndex = setIndex,
                exerciseName = exercise.exercise.name,
                onConfirm = {
                    viewModel.removeSetFromExercise(dayIndex, exerciseIndex, setIndex)
                    showDeleteSetDialog = false
                    setToDelete = null
                },
                onDismiss = {
                    showDeleteSetDialog = false
                    setToDelete = null
                }
            )
        }
    }


    if (showSuccessDialog) {
        TrainingPlanSuccessDialog(
            onDismiss = {
                showSuccessDialog = false
                viewModel.clearData()
                onBackClick()
            }
        )
    }

    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = viewModel::clearError,
            title = { Text("Błąd") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = viewModel::clearError) {
                    Text("OK", color = getPrimaryColor())
                }
            }
        )
    }

    if (showExitDialog) {
        TrainingPlanExitConfirmationDialog(
            onDismiss = { showExitDialog = false },
            onConfirm = {
                showExitDialog = false
                viewModel.clearData()
                onBackClick()
            }
        )
    }
}

@Composable
private fun CustomErrorSnackbar(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = getErrorColor()
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = message,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Zamknij",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}



@Composable
private fun TrainingPlanSuccessDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            getPrimaryColor().copy(alpha = 0.1f),
                            RoundedCornerShape(40.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = getPrimaryColor()
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))


                Text(
                    text = "Plan utworzony!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = getOnSurfaceColor(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))


                Text(
                    text = "Twój plan treningowy został pomyślnie utworzony. Możesz go znaleźć w sekcji \"Moje plany\".",
                    fontSize = 14.sp,
                    color = getOnSurfaceSecondaryColor(),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getPrimaryColor(),
                        contentColor = getOnPrimaryColor()
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Świetnie!",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}


@Composable
private fun TrainingPlanExitConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = getSurfaceColor(),
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Przerwać tworzenie planu?",
                color = getOnSurfaceColor(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        },
        text = {
            Text(
                text = "Wszystkie wprowadzone dane zostaną utracone.",
                color = getOnSurfaceSecondaryColor(),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, getOnSurfaceColor().copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Kontynuuj",
                        color = getOnSurfaceColor(),
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getErrorColor()
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Przerwij",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    )
}

@Composable
private fun TrainingPlanStepper(
    currentStep: TrainingPlanCreatorStep,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val steps = listOf(
            "Podstawy",
            "Dni i ćwiczenia",
            "Podsumowanie"
        )

        steps.forEachIndexed { index, label ->
            val isActive = currentStep.ordinal == index
            val isDone = currentStep.ordinal > index

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (isActive || isDone) getPrimaryColor() else getNavBarUnselectedColor()
                ) {
                    Box(
                        modifier = Modifier.size(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = getOnPrimaryColor(),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = label,
                    color = if (isActive) getPrimaryColor() else getOnSurfaceSecondaryColor(),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            if (index < steps.size - 1) {
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 8.dp),
                    color = getInputBorderColor()
                )
            }
        }
    }
}

@Composable
private fun BasicInfoStep(
    uiState: CreateTrainingPlanState,
    viewModel: CreateTrainingPlanViewModel
) {
    var showGoalDialog by remember { mutableStateOf(false) }
    var showDifficultyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        OutlinedTextField(
            value = uiState.name,
            onValueChange = viewModel::setName,
            label = { Text("Nazwa planu treningowego *") },
            placeholder = { Text("Wpisz nazwę planu...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = getPrimaryColor(),
                unfocusedBorderColor = getInputBorderColor(),
                focusedTextColor = getOnSurfaceColor(),
                unfocusedTextColor = getOnSurfaceColor(),
                cursorColor = getPrimaryColor()
            ),
            supportingText = {
                Text("${uiState.name.length}/150")
            },
            isError = uiState.name.length > 150
        )

        ImageUploadSection(
            selectedImageUri = uiState.imageUri,
            onImageSelected = viewModel::setImage
        )

        OutlinedTextField(
            value = uiState.goal,
            onValueChange = { },
            label = { Text("Cel treningu *") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showGoalDialog = true },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = getInputBorderColor(),
                disabledTextColor = getOnSurfaceColor()
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = getOnSurfaceSecondaryColor()
                )
            }
        )

        OutlinedTextField(
            value = uiState.advancedLevel,
            onValueChange = { },
            label = { Text("Poziom zaawansowania *") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDifficultyDialog = true },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = getInputBorderColor(),
                disabledTextColor = getOnSurfaceColor()
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = getOnSurfaceSecondaryColor()
                )
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Plan publiczny",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = getOnSurfaceColor()
                )
                Text(
                    text = "Inni użytkownicy będą mogli zobaczyć Twój plan",
                    fontSize = 12.sp,
                    color = getOnSurfaceSecondaryColor()
                )
            }

            Switch(
                checked = uiState.visibility == PrivacySettings.PUBLIC,
                onCheckedChange = { isPublic ->
                    viewModel.setVisibility(
                        if (isPublic) PrivacySettings.PUBLIC else PrivacySettings.PRIVATE
                    )
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = getOnPrimaryColor(),
                    checkedTrackColor = getPrimaryColor()
                )
            )
        }

        OutlinedTextField(
            value = uiState.description,
            onValueChange = viewModel::setDescription,
            label = { Text("Opis (opcjonalny)") },
            placeholder = { Text("Opisz swój plan treningowy...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            maxLines = 8,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = getPrimaryColor(),
                unfocusedBorderColor = getInputBorderColor(),
                focusedTextColor = getOnSurfaceColor(),
                unfocusedTextColor = getOnSurfaceColor(),
                cursorColor = getPrimaryColor()
            ),
            supportingText = {
                Text("${uiState.description.length}/850")
            },
            isError = uiState.description.length > 850
        )

        HashtagInputField(
            selectedHashtags = uiState.hashtags,
            onHashtagsChange = viewModel::setHashtags,
            maxHashtags = 50
        )
    }

    if (showGoalDialog) {
        GoalSelectionDialog(
            selectedGoal = uiState.goal,
            onGoalSelected = { goal ->
                viewModel.setGoal(goal)
                showGoalDialog = false
            },
            onDismiss = { showGoalDialog = false }
        )
    }

    if (showDifficultyDialog) {
        DifficultySelectionDialog(
            selectedDifficulty = uiState.advancedLevel,
            onDifficultySelected = { difficulty ->
                viewModel.setAdvancedLevel(difficulty)
                showDifficultyDialog = false
            },
            onDismiss = { showDifficultyDialog = false }
        )
    }
}


@Composable
private fun DaysAndExercisesStep(
    uiState: CreateTrainingPlanState,
    viewModel: CreateTrainingPlanViewModel,
    onNavigateToExerciseSelection: (Int) -> Unit,
    onRequestDeleteDay: (Int) -> Unit,
    onRequestDeleteExercise: (Int, Int) -> Unit,
    onRequestDeleteSet: (Int, Int, Int) -> Unit,
    onRequestRestDayConversion: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dni treningowe i ćwiczenia",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = getOnSurfaceColor()
            )

            if (uiState.days.size < 30) {
                FloatingActionButton(
                    onClick = viewModel::addDay,
                    modifier = Modifier.size(48.dp),
                    containerColor = getPrimaryColor()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Dodaj dzień",
                        tint = getOnPrimaryColor()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.days.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.CalendarToday,
                title = "Brak dni treningowych",
                subtitle = "Dodaj pierwszy dzień, aby rozpocząć",
                modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(uiState.days) { index, day ->
                    DayWithExercisesCard(
                        day = day,
                        onNameChange = { name -> viewModel.updateDayName(index, name) },
                        onToggleRestDay = {
                            if (viewModel.shouldWarnBeforeRestDayConversion(index)) {
                                onRequestRestDayConversion(index)
                            } else {
                                viewModel.toggleRestDay(index)
                        } },
                        onRemoveDay = { onRequestDeleteDay(index) },
                        onAddExercise = { onNavigateToExerciseSelection(index) },
                        onRemoveExercise = { exerciseIndex ->
                            onRequestDeleteExercise(index, exerciseIndex)
                        },
                        onUpdateRestTime = { exerciseIndex, restTime ->
                            viewModel.updateExerciseRestTime(index, exerciseIndex, restTime)
                        },
                        onToggleExerciseType = { exerciseIndex ->
                            viewModel.toggleExerciseType(index, exerciseIndex)
                        },
                        onAddSet = { exerciseIndex ->
                            viewModel.addSetToExercise(index, exerciseIndex)
                        },
                        onRemoveSet = { exerciseIndex, setIndex ->
                            onRequestDeleteSet(index, exerciseIndex, setIndex)
                        },
                        onUpdateSet = { exerciseIndex, setIndex, set ->
                            viewModel.updateSet(index, exerciseIndex, setIndex, set)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DayWithExercisesCard(
    day: TrainingDay,
    onNameChange: (String) -> Unit,
    onToggleRestDay: () -> Unit,
    onRemoveDay: () -> Unit,
    onAddExercise: () -> Unit,
    onRemoveExercise: (Int) -> Unit,
    onUpdateRestTime: (Int, Int) -> Unit,
    onToggleExerciseType: (Int) -> Unit,
    onAddSet: (Int) -> Unit,
    onRemoveSet: (Int, Int) -> Unit,
    onUpdateSet: (Int, Int, TrainingSet) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dzień ${day.dayNumber}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = getOnSurfaceColor()
                )

                IconButton(onClick = onRemoveDay) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Usuń dzień",
                        tint = getErrorColor()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = day.name,
                onValueChange = onNameChange,
                label = { Text("Nazwa dnia") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = getPrimaryColor(),
                    unfocusedBorderColor = getInputBorderColor()
                )
            )

            Spacer(modifier = Modifier.height(12.dp))



            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dzień odpoczynku",
                    fontSize = 14.sp,
                    color = getOnSurfaceColor()
                )

                Switch(
                    checked = day.isRestDay,
                    onCheckedChange = { onToggleRestDay() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = getOnPrimaryColor(),
                        checkedTrackColor = getPrimaryColor()
                    )
                )
            }


            if (!day.isRestDay) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ćwiczenia (${day.exercises.size})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor()
                    )

                    if (day.exercises.size < 15) {
                        IconButton(onClick = onAddExercise) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Dodaj ćwiczenie",
                                tint = getPrimaryColor()
                            )
                        }
                    }
                }

                if (day.exercises.isEmpty()) {
                    Text(
                        text = "Brak ćwiczeń. Kliknij + aby dodać.\nKażde ćwiczenie można dodać tylko raz na dzień treningowy.",
                        fontSize = 12.sp,
                        color = getOnSurfaceSecondaryColor(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))

                    day.exercises.forEachIndexed { exerciseIndex, dayExercise ->
                        ExerciseItem(
                            dayExercise = dayExercise,
                            onRemove = { onRemoveExercise(exerciseIndex) },
                            onUpdateRestTime = { restTime -> onUpdateRestTime(exerciseIndex, restTime) },
                            onToggleType = { onToggleExerciseType(exerciseIndex) },
                            onAddSet = { onAddSet(exerciseIndex) },
                            onRemoveSet = { setIndex -> onRemoveSet(exerciseIndex, setIndex) },
                            onUpdateSet = { setIndex, set -> onUpdateSet(exerciseIndex, setIndex, set) }
                        )

                        if (exerciseIndex < day.exercises.size - 1) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun ImageUploadSection(
    selectedImageUri: Uri?,
    onImageSelected: (Uri?) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> onImageSelected(uri) }

    Column {
        Text(
            text = "Zdjęcie planu treningowego *",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = getOnSurfaceColor()
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedImageUri != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Zdjęcie planu",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    IconButton(
                        onClick = { onImageSelected(null) },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Usuń zdjęcie",
                            tint = Color.White
                        )
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { launcher.launch("image/*") }
                    .border(
                        1.dp,
                        getInputBorderColor(),
                        RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = getPrimaryColor()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Kliknij, aby dodać zdjęcie",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor()
                    )

                    Text(
                        text = "JPG, PNG, WebP (maks. 5MB)",
                        fontSize = 12.sp,
                        color = getOnSurfaceSecondaryColor()
                    )
                }
            }
        }
    }
}
@Composable
private fun ExerciseItem(
    dayExercise: DayExercise,
    onRemove: () -> Unit,
    onUpdateRestTime: (Int) -> Unit,
    onToggleType: () -> Unit,
    onAddSet: () -> Unit,
    onRemoveSet: (Int) -> Unit,
    onUpdateSet: (Int, TrainingSet) -> Unit
) {
    var showRestTimeDialog by remember { mutableStateOf(false) }

    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = dayExercise.exercise.image,
                    contentDescription = dayExercise.exercise.name,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = dayExercise.exercise.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = if (dayExercise.isRepsSet) "Powtórzenia" else "Czas",
                        fontSize = 12.sp,
                        color = getOnSurfaceSecondaryColor()
                    )
                }
            }

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Usuń ćwiczenie",
                    tint = getErrorColor()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Typ:",
                fontSize = 14.sp,
                color = getOnSurfaceColor()
            )

            Row {
                FilterChip(
                    selected = dayExercise.isRepsSet,
                    onClick = { if (!dayExercise.isRepsSet) onToggleType() },
                    label = { Text("Powtórzenia") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = getPrimaryColor(),
                        selectedLabelColor = getOnPrimaryColor()
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                FilterChip(
                    selected = !dayExercise.isRepsSet,
                    onClick = { if (dayExercise.isRepsSet) onToggleType() },
                    label = { Text("Czas") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = getPrimaryColor(),
                        selectedLabelColor = getOnPrimaryColor()
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Odpoczynek po serii:",
                fontSize = 14.sp,
                color = getOnSurfaceColor()
            )

            TextButton(onClick = { showRestTimeDialog = true }) {
                Text(
                    text = "${dayExercise.restAfterSet}s",
                    color = getPrimaryColor()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Serie:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = getOnSurfaceColor()
            )

            if (dayExercise.sets.size < 15) {
                IconButton(onClick = onAddSet) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Dodaj serię",
                        tint = getPrimaryColor()
                    )
                }
            }
        }

        dayExercise.sets.forEachIndexed { setIndex, set ->
            SetItem(
                set = set,
                setIndex = setIndex,
                isRepsSet = dayExercise.isRepsSet,
                canRemove = dayExercise.sets.size > 1,
                onRemove = { onRemoveSet(setIndex) },
                onUpdate = { updatedSet -> onUpdateSet(setIndex, updatedSet) }
            )

            if (setIndex < dayExercise.sets.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }


    if (showRestTimeDialog) {
        RestTimeDialog(
            currentRestTime = dayExercise.restAfterSet,
            onConfirm = { restTime ->
                onUpdateRestTime(restTime)
                showRestTimeDialog = false
            },
            onDismiss = { showRestTimeDialog = false }
        )
    }
}

@Composable
private fun SetItem(
    set: TrainingSet,
    setIndex: Int,
    isRepsSet: Boolean,
    canRemove: Boolean,
    onRemove: () -> Unit,
    onUpdate: (TrainingSet) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = getBackgroundColor())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Seria ${setIndex + 1}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = getOnSurfaceColor()
            )

            if (isRepsSet) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val repsError = set.repetitions == null || set.repetitions !in 1..100
                    OutlinedTextField(
                        value = set.repetitions?.toString() ?: "",
                        onValueChange = { value ->
                            if (value.isEmpty()) {
                                onUpdate(set.copy(repetitions = null))
                            } else {
                                val reps = value.toIntOrNull()
                                if (reps != null) {
                                    onUpdate(set.copy(repetitions = reps))
                                }
                            }
                        },
                        label = { Text("Powt.") },
                        modifier = Modifier.width(80.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = repsError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (repsError) getErrorColor() else getPrimaryColor(),
                            unfocusedBorderColor = if (repsError) getErrorColor() else getInputBorderColor()
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    val weight = set.weight ?: 0.0
                    val weightError = weight > 300.0
                    OutlinedTextField(
                        value = if(weight == 0.0) "" else weight.toString(),
                        onValueChange = { value ->
                            val weightValue = value.toDoubleOrNull()
                            when{
                                value.isEmpty() || weightValue == 0.0 -> {
                                    onUpdate(set.copy(weight = null))
                                }
                                weightValue != null && weightValue <= 300.0 -> {
                                    onUpdate(set.copy(weight = weightValue))
                                }
                            }
                        },
                        label = { Text("kg") },
                        modifier = Modifier.width(80.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        placeholder = { Text("Brak") },
                        isError = weightError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (weightError) getErrorColor() else getPrimaryColor(),
                            unfocusedBorderColor = if (weightError) getErrorColor() else getInputBorderColor()
                        )
                    )
                }
            } else {
                val durationError = set.duration == null || set.duration !in 30..21600
                OutlinedTextField(
                    value = set.duration?.toString() ?: "",
                    onValueChange = { value ->
                        if (value.isEmpty()) {
                            onUpdate(set.copy(duration = null))
                        } else {
                            val duration = value.toIntOrNull()
                            if (duration != null) {
                                onUpdate(set.copy(duration = duration))
                            }
                        }
                    },
                    label = { Text("Sekundy") },
                    modifier = Modifier.width(100.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("60") },
                    isError = durationError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (durationError) getErrorColor() else getPrimaryColor(),
                        unfocusedBorderColor = if (durationError) getErrorColor() else getInputBorderColor()
                    )
                )
            }

            if (canRemove) {
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Usuń serię",
                        tint = getErrorColor()
                    )
                }
            }
        }
    }
}

@Composable
private fun RestTimeDialog(
    currentRestTime: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var restTimeText by remember { mutableStateOf(currentRestTime.toString()) }
    var restTimeError by remember { mutableStateOf<String?>(null) }

    val restTimeInt = restTimeText.toIntOrNull()
    val isValid = restTimeInt != null && restTimeInt in 30..5400

    LaunchedEffect(restTimeText) {
        restTimeError = when {
            restTimeText.isEmpty() -> "Czas odpoczynku jest wymagany"
            restTimeInt == null -> "Podaj poprawną liczbę"
            restTimeInt < 30 -> "Minimum 30 sekund"
            restTimeInt > 5400 -> "Maksimum 1.5 godziny (5400 sekund)"
            else -> null
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Czas odpoczynku po serii",
                fontWeight = FontWeight.Bold,
                color = getOnSurfaceColor()
            )
        },
        text = {
            Column {
                Text(
                    text = "Ustaw czas odpoczynku między seriami (w sekundach)",
                    fontSize = 14.sp,
                    color = getOnSurfaceSecondaryColor(),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = restTimeText,
                    onValueChange = { value ->
                        if (value.all { it.isDigit() } || value.isEmpty()) {
                            restTimeText = value
                        }
                    },
                    label = { Text("Sekundy") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("60") },
                    isError = !isValid && restTimeText.isNotEmpty(),
                    supportingText = if (restTimeError != null) {
                        {
                            Text(
                                text = restTimeError!!,
                                color = getErrorColor(),
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        {
                            Text(
                                text = "Zalecane: 30-120 sekund",
                                color = getOnSurfaceSecondaryColor(),
                                fontSize = 12.sp
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (restTimeError != null) getErrorColor() else getPrimaryColor(),
                        unfocusedBorderColor = if (restTimeError != null) getErrorColor() else getInputBorderColor(),
                        focusedLabelColor = if (restTimeError != null) getErrorColor() else getPrimaryColor(),
                        unfocusedLabelColor = if (restTimeError != null) getErrorColor() else getOnSurfaceSecondaryColor()
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = getPrimaryColor().copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Sugerowane czasy:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = getOnSurfaceColor()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Siła: 120-180s\n• Masa: 60-90s\n• Wytrzymałość: 30-60s",
                            fontSize = 11.sp,
                            color = getOnSurfaceSecondaryColor(),
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    restTimeInt?.let { validTime ->
                        onConfirm(validTime)
                    }
                },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isValid) getPrimaryColor() else getPrimaryColor().copy(alpha = 0.5f),
                    contentColor = getOnPrimaryColor()
                )
            ) {
                Text("Potwierdź")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = getOnSurfaceColor()
                ),
                border = BorderStroke(1.dp, getOnSurfaceColor().copy(alpha = 0.3f))
            ) {
                Text("Anuluj")
            }
        },
        containerColor = getSurfaceColor(),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun SummaryStep(uiState: CreateTrainingPlanState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Podsumowanie planu treningowego",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = getOnSurfaceColor()
            )
        }

        item {
            SummaryCard(title = "Podstawowe informacje") {
                SummaryField("Nazwa", uiState.name)
                SummaryField("Cel", uiState.goal)
                SummaryField("Poziom", uiState.advancedLevel)
                SummaryField("Widoczność", uiState.visibility)
                if (uiState.description.isNotBlank()) {
                    SummaryField("Opis", uiState.description)
                }
                if (uiState.hashtags.isNotEmpty()) {
                    SummaryField("Hashtagi", uiState.hashtags.joinToString(", ") { "#$it" })
                }
            }
        }

        item {
            SummaryCard(title = "Struktura treningowa") {
                Text(
                    text = "Łączna liczba dni: ${uiState.days.size}",
                    fontSize = 14.sp,
                    color = getOnSurfaceColor()
                )

                val workoutDays = uiState.days.count { !it.isRestDay }
                val restDays = uiState.days.count { it.isRestDay }

                Text(
                    text = "Dni treningowe: $workoutDays",
                    fontSize = 14.sp,
                    color = getOnSurfaceColor()
                )

                Text(
                    text = "Dni odpoczynku: $restDays",
                    fontSize = 14.sp,
                    color = getOnSurfaceColor()
                )

                val totalExercises = uiState.days.sumOf { it.exercises.size }
                Text(
                    text = "Łączna liczba ćwiczeń: $totalExercises",
                    fontSize = 14.sp,
                    color = getOnSurfaceColor()
                )
            }
        }

        items(uiState.days) { day ->
            DaySummaryCard(day = day)
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = getOnSurfaceColor()
            )

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@Composable
private fun SummaryField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label:",
            fontSize = 12.sp,
            color = getOnSurfaceSecondaryColor(),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = getOnSurfaceColor()
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun DaySummaryCard(day: TrainingDay) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = day.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = getOnSurfaceColor()
                )

                if (day.isRestDay) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = getOnSurfaceColor().copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "Odpoczynek",
                            fontSize = 12.sp,
                            color = getOnSurfaceColor(),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (!day.isRestDay) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Ćwiczenia (${day.exercises.size}):",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = getOnSurfaceColor()
                )

                Spacer(modifier = Modifier.height(8.dp))

                day.exercises.forEach { exercise ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = exercise.exercise.image,
                            contentDescription = exercise.exercise.name,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = exercise.exercise.name,
                                fontSize = 14.sp,
                                color = getOnSurfaceColor(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = "${exercise.sets.size} ${if (exercise.sets.size == 1) "seria" else "serie"}",
                                fontSize = 12.sp,
                                color = getOnSurfaceSecondaryColor()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun TrainingPlanBottomBar(
    step: TrainingPlanCreatorStep,
    isLoading: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (step != TrainingPlanCreatorStep.BASIC) {
            OutlinedButton(
                onClick = onPrev,
                modifier = Modifier.weight(1f),
                enabled = !isLoading,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(1.dp, getOnSurfaceColor().copy(alpha = 0.5f))
            ) {
                Text("Wstecz", color = getOnSurfaceColor())
            }
        }

        Button(
            onClick = if (step == TrainingPlanCreatorStep.SUMMARY) onCreate else onNext,
            modifier = Modifier.weight(1f),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = getPrimaryColor()),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isLoading && step == TrainingPlanCreatorStep.SUMMARY) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = getOnPrimaryColor(),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = if (step == TrainingPlanCreatorStep.SUMMARY) "Utwórz plan" else "Dalej",
                    color = getOnPrimaryColor()
                )
            }
        }
    }
}