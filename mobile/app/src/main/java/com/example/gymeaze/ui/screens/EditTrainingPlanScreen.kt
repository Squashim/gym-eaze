package com.example.gymeaze.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.constants.Goals
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.EditTrainingPlanViewModel
import com.example.gymeaze.viewmodel.TrainingSet
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.ui.components.DifficultyOptionItem
import com.example.gymeaze.ui.components.GoalOptionItem
import com.example.gymeaze.ui.components.getDifficultyColor
import com.example.gymeaze.ui.components.getDifficultyDescription
import com.example.gymeaze.ui.components.getGoalDescription
import com.example.gymeaze.viewmodel.DayExercise
import com.example.gymeaze.viewmodel.EditTrainingPlanState
import com.example.gymeaze.viewmodel.TrainingDay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTrainingPlanScreen(
    planId: String,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    onNavigateToExerciseSelection: (Int) -> Unit,
    viewModel: EditTrainingPlanViewModel = viewModel()
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

    LaunchedEffect(planId) {
        if (!uiState.isInitialLoadComplete) {
            viewModel.loadTrainingPlan(planId)
        } else {
            //skip
        }
    }

    LaunchedEffect(uiState.validationToast) {
        uiState.validationToast?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long
            )
            viewModel.clearValidationToast()
        }
    }

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            showSuccessDialog = true
            viewModel.clearSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edytuj plan treningowy",
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
                actions = {
                    TextButton(
                        onClick = viewModel::saveTrainingPlan,
                        enabled = !uiState.isUpdating && uiState.basicValid && uiState.daysAndExercisesValid
                    ) {
                        if (uiState.isUpdating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = getPrimaryColor(),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Zapisz",
                                color = getPrimaryColor(),
                                fontWeight = FontWeight.Medium
                            )
                        }
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

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = getPrimaryColor())
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Ładowanie planu...",
                            color = getOnSurfaceSecondaryColor()
                        )
                    }
                }
            }

            uiState.originalPlan != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    EditTrainingPlanStepper(
                        modifier = Modifier.padding(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            EditBasicInfoSection(
                                uiState = uiState,
                                viewModel = viewModel
                            )
                        }

                        item {
                            Text(
                                text = "Dni treningowe i ćwiczenia",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = getOnSurfaceColor()
                            )
                        }

                        item {
                            if (uiState.days.size < 30) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
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
                        }

                        if (uiState.days.isEmpty()) {
                            item {
                                EmptyDaysMessage()
                            }
                        } else {
                            itemsIndexed(uiState.days) { index, day ->
                                EditDayWithExercisesCard(
                                    day = day,
                                    dayIndex = index,
                                    onNameChange = { name -> viewModel.updateDayName(index, name) },
                                    onToggleRestDay = {
                                        if (viewModel.shouldWarnBeforeRestDayConversion(index)) {
                                            dayToConvertToRest = index
                                            showRestDayWarningDialog = true
                                        } else {
                                            viewModel.toggleRestDay(index)
                                        }
                                    },
                                    onRemoveDay = {
                                        dayToDelete = index to day.name
                                        showDeleteDayDialog = true
                                    },
                                    onAddExercise = {
                                        android.util.Log.d("EditTrainingPlanScreen", "Opening exercise selection for day $index")
                                        onNavigateToExerciseSelection(index)
                                    },
                                    onRemoveExercise = { exerciseIndex ->
                                        val exercise = day.exercises[exerciseIndex]
                                        exerciseToDelete = Triple(index, exerciseIndex, exercise.exercise.name)
                                        showDeleteExerciseDialog = true
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
                                        setToDelete = Triple(index, exerciseIndex, setIndex)
                                        showDeleteSetDialog = true
                                    },
                                    onUpdateSet = { exerciseIndex, setIndex, set ->
                                        viewModel.updateSet(index, exerciseIndex, setIndex, set)
                                    }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = getErrorColor(),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nie można załadować planu",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = getOnSurfaceColor()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.loadTrainingPlan(planId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getPrimaryColor()
                            )
                        ) {
                            Text("Spróbuj ponownie")
                        }
                    }
                }
            }
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
        EditTrainingPlanSuccessDialog(
            onDismiss = {
                showSuccessDialog = false
                onSaveSuccess()
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
        EditTrainingPlanExitConfirmationDialog(
            onDismiss = { showExitDialog = false },
            onConfirm = {
                showExitDialog = false
                onBackClick()
            }
        )
    }
}

@Composable
private fun EditTrainingPlanStepper(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = getPrimaryColor().copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = getPrimaryColor(),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Edycja planu treningowego",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = getPrimaryColor()
            )
        }
    }
}

@Composable
private fun EditBasicInfoSection(
    uiState: EditTrainingPlanState,
    viewModel: EditTrainingPlanViewModel
) {
    var showGoalDialog by remember { mutableStateOf(false) }
    var showDifficultyDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Podstawowe informacje",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = getOnSurfaceColor()
            )

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

            EditImageUploadSection(
                currentImageUrl = uiState.originalPlan?.image,
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
                    checked = uiState.visibility == "Publiczny",
                    onCheckedChange = { isPublic ->
                        viewModel.setVisibility(
                            if (isPublic) "Publiczny" else "Prywatny"
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

            EditHashtagInputField(
                selectedHashtags = uiState.hashtags,
                onHashtagsChange = viewModel::setHashtags,
                maxHashtags = 50
            )
        }
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
private fun EditImageUploadSection(
    currentImageUrl: String?,
    selectedImageUri: Uri?,
    onImageSelected: (Uri?) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> onImageSelected(uri) }

    Column {
        Text(
            text = "Zdjęcie planu treningowego",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = getOnSurfaceColor()
        )

        Spacer(modifier = Modifier.height(8.dp))

        val imageToShow = selectedImageUri?.toString() ?: currentImageUrl

        if (imageToShow != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box {
                    AsyncImage(
                        model = imageToShow,
                        contentDescription = "Zdjęcie planu",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    IconButton(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(
                                Color.Black.copy(alpha = 0.7f),
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Zmień zdjęcie",
                            tint = Color.White
                        )
                    }

                    if (selectedImageUri != null) {
                        IconButton(
                            onClick = { onImageSelected(null) },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .background(
                                    Color.Red.copy(alpha = 0.7f),
                                    RoundedCornerShape(8.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Usuń nowe zdjęcie",
                                tint = Color.White
                            )
                        }
                    }

                    if (selectedImageUri != null) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth(),
                            color = Color.Black.copy(alpha = 0.7f)
                        ) {
                            Text(
                                text = "Nowe zdjęcie - zostanie zapisane po aktualizacji planu",
                                color = Color.White,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
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
private fun EditHashtagInputField(
    selectedHashtags: List<String>,
    onHashtagsChange: (List<String>) -> Unit,
    maxHashtags: Int
) {
    var inputText by remember { mutableStateOf("") }

    Column {
        Text(
            text = "Hashtagi (${selectedHashtags.size}/$maxHashtags)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = getOnSurfaceColor()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = inputText,
            onValueChange = { newValue ->
                if (newValue.contains(' ') || newValue.contains(',') || newValue.contains('\n')) {
                    val hashtagToAdd = inputText.trim().lowercase()
                    if (hashtagToAdd.isNotEmpty() &&
                        hashtagToAdd !in selectedHashtags &&
                        selectedHashtags.size < maxHashtags &&
                        hashtagToAdd.length >= 2 &&
                        hashtagToAdd.length <= 30 &&
                        hashtagToAdd.all { it.isLetterOrDigit() }) {
                        onHashtagsChange(selectedHashtags + hashtagToAdd)
                    }
                    inputText = ""
                } else {
                    inputText = newValue.take(30)
                }
            },
            label = { Text("Dodaj hashtag") },
            placeholder = { Text("np. masa, siła, kondycja...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = getPrimaryColor(),
                unfocusedBorderColor = getInputBorderColor(),
                cursorColor = getPrimaryColor()
            ),
            supportingText = {
                Text(
                    text = "Użyj spacji, przecinka lub Enter aby dodać hashtag",
                    fontSize = 12.sp,
                    color = getOnSurfaceSecondaryColor()
                )
            }
        )

        if (selectedHashtags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedHashtags.size) { index ->
                    val hashtag = selectedHashtags[index]

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = getPrimaryColor().copy(alpha = 0.1f),
                        modifier = Modifier.border(
                            1.dp,
                            getPrimaryColor().copy(alpha = 0.3f),
                            RoundedCornerShape(16.dp)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "#$hashtag",
                                color = getPrimaryColor(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            IconButton(
                                onClick = {
                                    onHashtagsChange(selectedHashtags - hashtag)
                                },
                                modifier = Modifier.size(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Usuń hashtag",
                                    tint = getPrimaryColor(),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditDayWithExercisesCard(
    day: TrainingDay,
    dayIndex: Int,
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
                        EditExerciseItem(
                            dayExercise = dayExercise,
                            exerciseIndex = exerciseIndex,
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
private fun EditExerciseItem(
    dayExercise: DayExercise,
    exerciseIndex: Int,
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
            EditSetItem(
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
private fun EditSetItem(
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
                    val repsError = set.repetitions == null || set.repetitions!! !in 1..100
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

                    val weightError = set.weight == null || set.weight!! < 0.5 || set.weight!! > 300.0
                    OutlinedTextField(
                        value = set.weight?.toString() ?: "0.5",
                        onValueChange = { value ->
                            if (value.isEmpty()) {
                                onUpdate(set.copy(weight = 0.5))
                            } else {
                                val weight = value.toDoubleOrNull()
                                if (weight != null && weight >= 0.5 && weight <= 300.0) {
                                    onUpdate(set.copy(weight = weight))
                                }
                            }
                        },
                        label = { Text("kg") },
                        modifier = Modifier.width(80.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        placeholder = { Text("0.5") },
                        isError = weightError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (weightError) getErrorColor() else getPrimaryColor(),
                            unfocusedBorderColor = if (weightError) getErrorColor() else getInputBorderColor()
                        )
                    )
                }
            } else {
                val durationError = set.duration == null || set.duration!! !in 30..21600
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
private fun EmptyDaysMessage() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = getOnSurfaceSecondaryColor()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Brak dni treningowych",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = getOnSurfaceColor()
        )

        Text(
            text = "Dodaj pierwszy dzień, aby rozpocząć",
            fontSize = 14.sp,
            color = getOnSurfaceSecondaryColor()
        )
    }
}

@Composable
private fun EditTrainingPlanSuccessDialog(
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
                    text = "Plan zaktualizowany!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = getOnSurfaceColor(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Twój plan treningowy został pomyślnie zaktualizowany.",
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
private fun EditTrainingPlanExitConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = getSurfaceColor(),
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Przerwać edycję planu?",
                color = getOnSurfaceColor(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        },
        text = {
            Text(
                text = "Niezapisane zmiany zostaną utracone.",
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
fun GoalSelectionDialog(
    selectedGoal: String,
    onGoalSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val goals = listOf(
        Goals.NONE to "Nie wybrano",
        Goals.MUSCLE_GAIN to "Budowanie masy mięśniowej",
        Goals.STRENGTH to "Zwiększanie siły",
        Goals.ENDURANCE to "Poprawa wytrzymałości",
        Goals.FAT_LOSS to "Redukcja tkanki tłuszczowej",
        Goals.CALORIE_BURN to "Spalanie kalorii",
        Goals.MASS_GAIN to "Zwiększenie masy ciała",
        Goals.COORDINATION to "Poprawa koordynacji",
        Goals.MOBILITY to "Poprawa mobilności",
        Goals.FLEXIBILITY to "Poprawa gibkości",
        Goals.LEGS_GLUTES to "Rozwój nóg i pośladków",
        Goals.POSTURE to "Poprawa postawy ciała",
        Goals.RECOVERY to "Regeneracja i odpoczynek"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
            shape = RoundedCornerShape(16.dp)
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
                        text = "Wybierz cel treningu",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor()
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Zamknij",
                            tint = getOnSurfaceColor()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(goals) { (value, label) ->
                        GoalOptionItem(
                            label = label,
                            description = getGoalDescription(value),
                            isSelected = selectedGoal == value,
                            onClick = { onGoalSelected(value) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DifficultySelectionDialog(
    selectedDifficulty: String,
    onDifficultySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val difficulties = listOf(
        AdvancedLevel.NONE to "Nie wybrano",
        AdvancedLevel.EASY to "Początkujący",
        AdvancedLevel.MEDIUM to "Średniozaawansowany",
        AdvancedLevel.HARD to "Zaawansowany"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
            shape = RoundedCornerShape(16.dp)
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
                        text = "Wybierz poziom trudności",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor()
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Zamknij",
                            tint = getOnSurfaceColor()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                difficulties.forEach { (value, label) ->
                    DifficultyOptionItem(
                        label = label,
                        description = getDifficultyDescription(value),
                        isSelected = selectedDifficulty == value,
                        color = getDifficultyColor(value),
                        onClick = { onDifficultySelected(value) }
                    )
                }
            }
        }
    }
}