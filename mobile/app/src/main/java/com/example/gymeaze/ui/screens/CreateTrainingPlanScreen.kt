package com.example.gymeaze.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.constants.Goals
import com.example.gymeaze.ui.components.DifficultySelectionDialog
import com.example.gymeaze.ui.components.GoalSelectionDialog
import com.example.gymeaze.ui.components.HashtagInputDialog
import com.example.gymeaze.ui.components.TrainingDayCard
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getInputBorderColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor
import com.example.gymeaze.viewmodel.CreateTrainingPlanViewModel
import kotlin.toString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTrainingPlanScreen(
    onBackClick: () -> Unit,
    onNavigateToExerciseSelection: (dayIndex: Int) -> Unit,
    viewModel: CreateTrainingPlanViewModel = viewModel()
) {

    val context = androidx.compose.ui.platform.LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val formData by viewModel.formData.collectAsState()

    var showGoalDialog by remember { mutableStateOf(false) }
    var showDifficultyDialog by remember { mutableStateOf(false) }
    var showHashtagDialog by remember { mutableStateOf(false) }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateSelectedImage(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nowy plan treningowy",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wstecz",
                            tint = getOnSurfaceColor()
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.createTrainingPlan() },
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
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
        containerColor = getBackgroundColor()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ImageSelectionSection(
                    selectedImageUri = formData.selectedImageUri,
                    onImageClick = { imageLauncher.launch("image/*") }
                )
            }

            item {
                OutlinedTextField(
                    value = formData.title,
                    onValueChange = viewModel::updateTitle,
                    label = { Text("Tytuł planu") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = getPrimaryColor(),
                        unfocusedBorderColor = getInputBorderColor(),
                        focusedTextColor = getOnSurfaceColor(),
                        unfocusedTextColor = getOnSurfaceColor()
                    ),
                    singleLine = true,
                    supportingText = {
                        Text("${formData.title.length}/150")
                    }
                )
            }

            item {
                OutlinedTextField(
                    value = formData.description,
                    onValueChange = viewModel::updateDescription,
                    label = { Text("Opis (opcjonalny)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = getPrimaryColor(),
                        unfocusedBorderColor = getInputBorderColor(),
                        focusedTextColor = getOnSurfaceColor(),
                        unfocusedTextColor = getOnSurfaceColor()
                    ),
                    minLines = 3,
                    maxLines = 5,
                    supportingText = {
                        Text("${formData.description.length}/850")
                    }
                )
            }

            item {
                SelectionCard(
                    title = "Cel treningu",
                    selectedValue = getGoalDisplayName(formData.goal),
                    onClick = { showGoalDialog = true }
                )
            }

            item {
                SelectionCard(
                    title = "Poziom trudności",
                    selectedValue = getDifficultyDisplayName(formData.advancedLevel),
                    onClick = { showDifficultyDialog = true }
                )
            }

            item {
                HashtagsSection(
                    hashtags = formData.hashtags,
                    onAddHashtag = { showHashtagDialog = true },
                    onRemoveHashtag = viewModel::removeHashtag
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dni treningowe",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor()
                    )

                    IconButton(
                        onClick = { viewModel.addTrainingDay() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Dodaj dzień",
                            tint = getPrimaryColor()
                        )
                    }
                }
            }

            itemsIndexed(formData.days) { index, day ->
                TrainingDayCard(
                    day = day,
                    dayIndex = index + 1,
                    onEditName = { newName -> viewModel.updateDayName(index, newName) },
                    onToggleRestDay = { viewModel.toggleRestDay(index) },
                    onAddExercise = { onNavigateToExerciseSelection(index) },
                    onRemoveExercise = { exerciseIndex ->
                        viewModel.removeExercise(index, exerciseIndex)
                    },
                    onRemoveDay = { viewModel.removeTrainingDay(index) },
                    onEditExercise = { exerciseIndex ->
                        // TODO Implementacja nawigacji do edycji ćwiczenia
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            //TODO Obsluga różnych typów błędów
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            // TODO Pokazanie komunikatu o sukcesie
            onBackClick()
        }
    }

    if (showGoalDialog) {
        GoalSelectionDialog(
            selectedGoal = formData.goal,
            onGoalSelected = { goal ->
                viewModel.updateGoal(goal)
                showGoalDialog = false
            },
            onDismiss = { showGoalDialog = false }
        )
    }

    if (showDifficultyDialog) {
        DifficultySelectionDialog(
            selectedDifficulty = formData.advancedLevel,
            onDifficultySelected = { difficulty ->
                viewModel.updateDifficulty(difficulty)
                showDifficultyDialog = false
            },
            onDismiss = { showDifficultyDialog = false }
        )
    }

    if (showHashtagDialog) {
        HashtagInputDialog(
            onHashtagAdded = { hashtag ->
                viewModel.addHashtag(hashtag)
                showHashtagDialog = false
            },
            onDismiss = { showHashtagDialog = false }
        )
    }
}

@Composable
private fun ImageSelectionSection(
    selectedImageUri: String?,
    onImageClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onImageClick() },
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Zdjęcie planu treningowego",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Dodaj zdjęcie",
                        modifier = Modifier.size(48.dp),
                        tint = getOnSurfaceSecondaryColor()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Dotknij, aby dodać zdjęcie",
                        color = getOnSurfaceSecondaryColor(),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectionCard(
    title: String,
    selectedValue: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = getOnSurfaceSecondaryColor()
                )
                Text(
                    text = selectedValue,
                    fontSize = 16.sp,
                    color = getOnSurfaceColor(),
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = getOnSurfaceSecondaryColor()
            )
        }
    }
}

@Composable
private fun HashtagsSection(
    hashtags: List<String>,
    onAddHashtag: () -> Unit,
    onRemoveHashtag: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
        shape = RoundedCornerShape(12.dp)
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
                    text = "Hashtagi",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = getOnSurfaceColor()
                )
                IconButton(onClick = onAddHashtag) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Dodaj hashtag",
                        tint = getPrimaryColor()
                    )
                }
            }

            if (hashtags.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(hashtags) { index, hashtag ->
                        HashtagChip(
                            hashtag = hashtag,
                            onRemove = { onRemoveHashtag(index) }
                        )
                    }
                }
            } else {
                Text(
                    text = "Brak hashtagów",
                    color = getOnSurfaceSecondaryColor(),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun HashtagChip(
    hashtag: String,
    onRemove: () -> Unit
) {
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
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "#$hashtag",
                fontSize = 12.sp,
                color = getPrimaryColor()
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Usuń hashtag",
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onRemove() },
                tint = getPrimaryColor()
            )
        }
    }
}

private fun getGoalDisplayName(goal: String): String = when (goal) {
    Goals.NONE -> "Nie wybrano"
    Goals.RECOVERY -> "Regeneracja i odpoczynek"
    Goals.STRENGTH -> "Zwiększanie siły"
    Goals.POSTURE -> "Poprawa postawy ciała"
    Goals.MASS_GAIN -> "Zwiększenie masy ciała"
    Goals.COORDINATION -> "Poprawa koordynacji"
    Goals.CALORIE_BURN -> "Spalanie kalorii"
    Goals.MOBILITY -> "Poprawa mobilności"
    Goals.LEGS_GLUTES -> "Rozwój nóg i pośladków"
    Goals.ENDURANCE -> "Poprawa wytrzymałości"
    Goals.FAT_LOSS -> "Redukcja tkanki tłuszczowej"
    Goals.MUSCLE_GAIN -> "Budowanie masy mięśniowej"
    Goals.FLEXIBILITY -> "Poprawa gibkości"
    else -> "Nie wybrano"
}

private fun getDifficultyDisplayName(difficulty: String): String = when (difficulty) {
    AdvancedLevel.NONE -> "Nie wybrano"
    AdvancedLevel.EASY -> "Początkujący"
    AdvancedLevel.MEDIUM -> "Średniozaawansowany"
    AdvancedLevel.HARD -> "Zaawansowany"
    else -> "Nie wybrano"
}