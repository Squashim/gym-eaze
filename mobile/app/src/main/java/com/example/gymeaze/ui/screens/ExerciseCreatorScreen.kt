package com.example.gymeaze.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.ui.components.HashtagInputField
import com.example.gymeaze.ui.components.MultiSelectDropdown
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.ExerciseCreatorStep
import com.example.gymeaze.viewmodel.ExerciseCreatorState
import com.example.gymeaze.viewmodel.ExerciseCreatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseCreatorScreen(
    viewModel: ExerciseCreatorViewModel,
    onBackClick: () -> Unit,
    onCreated: () -> Unit
) {
    val ui by viewModel.uiState.collectAsState()
    var showExitDialog by remember {mutableStateOf(false)}

    LaunchedEffect(ui.success) { if (ui.success) onCreated() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kreator ćwiczenia",
                        color = getOnSurfaceColor(),
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = getOnSurfaceColor()
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {showExitDialog = true}) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Anuluj tworzenie",
                            tint = getOnSurfaceColor()
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = getBackgroundColor()
                )
            )
        },
        containerColor = getBackgroundColor()
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 16.dp)
        ) {
            Stepper(viewModel.step)

            Spacer(modifier = Modifier.height(8.dp))

            when (viewModel.step) {
                ExerciseCreatorStep.BASIC -> StepBasic(ui, viewModel)
                ExerciseCreatorStep.EXTRA -> StepExtra(ui, viewModel)
                ExerciseCreatorStep.SUMMARY -> StepSummary(ui)
            }

            Spacer(modifier = Modifier.height(8.dp))

            BottomBar(
                step = viewModel.step,
                isLoading = ui.isLoading,
                canProceed = when (viewModel.step) {
                    ExerciseCreatorStep.BASIC -> ui.basicValid
                    ExerciseCreatorStep.EXTRA -> ui.extraValid
                    ExerciseCreatorStep.SUMMARY -> true
                },
                onPrev = viewModel::prevStep,
                onNext = viewModel::nextStep,
                onCreate = viewModel::create
            )
        }
    }

    if (ui.error != null) {
        AlertDialog(
            onDismissRequest = viewModel::clearError,
            title = { Text("Błąd") },
            text = { Text(ui.error!!) },
            confirmButton = {
                TextButton(onClick = viewModel::clearError) {
                    Text("OK", color = getPrimaryColor())
                }
            }
        )
    }

    if (showExitDialog) {
        ExitConfirmationDialog(
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
private fun ExitConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = getSurfaceColor(),
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Czy na pewno chcesz przerwać tworzenie ćwiczenia?",
                color = getOnSurfaceColor(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        },
        text = {
            Text(
                text = "Wszystkie niezapisane zmiany zostaną utracone, a rozpoczęty proces tworzenia nowego ćwiczenia zostanie anulowany. Dotychczas wprowadzone infromacje nie zostaną zapisane i nie będzie możliwości ich odzyskania po wyjściu z tego ekranu.",
                color = getOnSurfaceSecondaryColor(),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDC3545)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(0.45f)
            ) {
                Text(
                    text = "Przerwij",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(1.dp, getOnSurfaceColor().copy(alpha = 0.3f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(0.45f)
            ) {
                Text(
                    text = "Powrót do kreatora",
                    color = getOnSurfaceColor(),
                    fontWeight = FontWeight.Medium
                )
            }
        }

    )
}

@Composable
private fun Stepper(step: ExerciseCreatorStep) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        @Composable
        fun node(idx: Int, label: String) {
            val active = step.ordinal == idx
            val done = step.ordinal > idx
            val bg = if (active || done) getPrimaryColor() else getSurfaceVariantColor()
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = bg
            ) {
                Box(
                    modifier = Modifier.size(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${idx + 1}",
                        color = getOnPrimaryColor(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = label,
                color = if (active) getPrimaryColor() else getOnSurfaceSecondaryColor(),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            node(0, "Podstawowe\ndane")
        }
        Divider(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(horizontal = 8.dp),
            color = getInputBorderColor()
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            node(1, "Dodatkowe\ninformacje")
        }
        Divider(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(horizontal = 8.dp),
            color = getInputBorderColor()
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            node(2, "Podsumowanie")
        }
    }
}

@Composable
private fun StepBasic(ui: ExerciseCreatorState, vm: ExerciseCreatorViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("Nazwa ćwiczenia*", color = getOnSurfaceColor(), fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = ui.name,
                onValueChange = vm::setName,
                placeholder = { Text("Podaj nazwę ćwiczenia", color = getOnSurfaceSecondaryColor()) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = getPrimaryColor(),
                    unfocusedBorderColor = getInputBorderColor(),
                    focusedTextColor = getOnSurfaceColor(),
                    unfocusedTextColor = getOnSurfaceColor(),
                    cursorColor = getPrimaryColor()
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            UploadCard(
                title = "Obraz poglądowy ćwiczenia*",
                subtitle = "Kliknij, aby przesłać",
                hint = "Maksymalny rozmiar obrazu to 5MB. Obsługiwane typy: .jpg, .png, .webp.",
                selected = ui.imageUri != null,
                onPick = vm::setImage,
                mime = "image/*",
                modifier = Modifier.weight(1f)
            )
            UploadCard(
                title = "Filmik instruktażowy*",
                subtitle = "Kliknij, aby przesłać",
                hint = "Maksymalny rozmiar filmiku to 20MB. Obsługiwane typy: .mp4, .webm.",
                selected = ui.videoUri != null,
                onPick = vm::setVideo,
                mime = "video/*",
                modifier = Modifier.weight(1f)
            )
        }

        Column {
            Text("Poziom trudności*", color = getOnSurfaceColor(), fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(12.dp))
            DifficultyRow("Początkujący", ui.difficulty == "easy") { vm.setDifficulty("easy") }
            DifficultyRow("Średniozaawansowany", ui.difficulty == "medium") { vm.setDifficulty("medium") }
            DifficultyRow("Zaawansowany", ui.difficulty == "hard") { vm.setDifficulty("hard") }
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Publiczne ćwiczenie*", color = getOnSurfaceColor(), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(
                    "Inni użytkownicy będą mogli znaleźć i używać tego ćwiczenia.",
                    color = getOnSurfaceSecondaryColor(),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            Switch(
                checked = ui.isPublic,
                onCheckedChange = vm::setPublic,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = getOnPrimaryColor(),
                    checkedTrackColor = getPrimaryColor(),
                    uncheckedThumbColor = getOnSurfaceSecondaryColor(),
                    uncheckedTrackColor = getSurfaceVariantColor()
                )
            )
        }
    }
}

@Composable
private fun StepExtra(ui: ExerciseCreatorState, vm: ExerciseCreatorViewModel) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "Opis ćwiczenia*",
                color = getOnSurfaceColor(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = ui.description,
                onValueChange = vm::setDescription,
                placeholder = {
                    Text(
                        text = "Opisz ćwiczenie i sposób wykonania np.\nKrok 1. Ustaw się w pozycji pompki...\nKrok 2. Zegnij ramiona...\n...",
                        color = getOnSurfaceSecondaryColor()
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 6,
                maxLines = 10,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = getPrimaryColor(),
                    unfocusedBorderColor = getInputBorderColor(),
                    focusedTextColor = getOnSurfaceColor(),
                    unfocusedTextColor = getOnSurfaceColor(),
                    cursorColor = getPrimaryColor()
                ),
                shape = RoundedCornerShape(8.dp),
                supportingText = {
                    Text(
                        text = "Maksymalna długość opisu to 850 znaków.",
                        color = getOnSurfaceSecondaryColor(),
                        fontSize = 12.sp
                    )
                },
                isError = ui.description.length > 850
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MultiSelectDropdown(
                label = "Grupy mięśniowe*",
                selectedItems = ui.selectedMuscleGroups,
                availableItems = ui.availableMuscleGroups,
                onSelectionChange = vm::setMuscleGroups,
                itemDisplayText = { it.name },
                isLoading = ui.isLoadingData,
                modifier = Modifier.weight(1f)
            )

            MultiSelectDropdown(
                label = "Sprzęt ćwiczeniowy*",
                selectedItems = ui.selectedEquipment,
                availableItems = ui.availableEquipment,
                onSelectionChange = vm::setEquipment,
                itemDisplayText = { it.name },
                isLoading = ui.isLoadingData,
                modifier = Modifier.weight(1f)
            )
        }

        HashtagInputField(
            selectedHashtags = ui.selectedHashtags,
            onHashtagsChange = vm::setHashtags,
            availableHashtags = ui.availableHashtags,
            maxHashtags = 50
        )
    }
}

@Composable
private fun StepSummary(ui: ExerciseCreatorState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Sprawdź poprawność danych",
            color = getOnSurfaceColor(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        SummaryField(
            label = "Nazwa ćwiczenia",
            value = ui.name
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Poziom trudności:",
                    color = getOnSurfaceColor(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                DifficultyBadge(
                    difficulty = ui.difficulty
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Widoczność ćwiczenia:",
                    color = getOnSurfaceColor(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                VisibilityBadge(
                    isPublic = ui.isPublic
                )
            }
        }

        ExpandableSummaryField(
            label = "Opis ćwiczenia",
            value = ui.description
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Grupy mięśniowe",
                    color = getOnSurfaceColor(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                TagGroup(
                    items = ui.selectedMuscleGroups.map { it.name }
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Wymagany sprzęt",
                    color = getOnSurfaceColor(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                TagGroup(
                    items = ui.selectedEquipment.map { it.name }
                )
            }
        }

        if (ui.selectedHashtags.isNotEmpty()) {
            ExpandableTagField(
                label = "Hashtagi",
                items = ui.selectedHashtags.map { "#${it.name}" }
            )
        }

        FileStatusSection(
            hasImage = ui.imageUri != null,
            hasVideo = ui.videoUri != null
        )
    }
}


@Composable
private fun SummaryField(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            color = getOnSurfaceColor(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = getOnSurfaceColor(),
            fontSize = 16.sp
        )
    }
}

@Composable
private fun ExpandableSummaryField(
    label: String,
    value: String
) {
    var isExpanded by remember { mutableStateOf(false) }
    val maxLines = if (isExpanded) Int.MAX_VALUE else 2

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = getOnSurfaceColor(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            if (value.length > 100) {
                Icon(
                    imageVector = if (isExpanded)
                        Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Zwiń" else "Rozwiń",
                    tint = getOnSurfaceSecondaryColor(),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { isExpanded = !isExpanded }
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = getOnSurfaceColor(),
            fontSize = 14.sp,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun DifficultyBadge(difficulty: String) {
    val (text, color) = when (difficulty) {
        "easy" -> "easy" to Color(0xFF4CAF50)
        "medium" -> "medium" to Color(0xFFFF9800)
        "hard" -> "hard" to Color(0xFFF44336)
        else -> "easy" to Color(0xFF4CAF50)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun VisibilityBadge(isPublic: Boolean) {
    val (text, color) = if (isPublic) {
        "public" to getPrimaryColor()
    } else {
        "private" to Color(0xFF9E9E9E)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        modifier = Modifier
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isPublic) Icons.Default.Public else Icons.Default.Lock,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TagGroup(items: List<String>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 80.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.heightIn(max = 120.dp)
    ) {
        items(items) { item ->
            TagChip(text = item)
        }
    }
}

@Composable
private fun ExpandableTagField(
    label: String,
    items: List<String>
) {
    var isExpanded by remember { mutableStateOf(false) }
    val displayItems = if (isExpanded) items else items.take(4)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = getOnSurfaceColor(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            if (items.size > 4) {
                Icon(
                    imageVector = if (isExpanded)
                        Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Zwiń" else "Rozwiń",
                    tint = getOnSurfaceSecondaryColor(),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { isExpanded = !isExpanded }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.heightIn(max = if (isExpanded) 200.dp else 80.dp)
        ) {
            items(displayItems) { item ->
                HashtagChip(text = item)
            }
        }

        if (!isExpanded && items.size > 4) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "i ${items.size - 4} więcej...",
                color = getOnSurfaceSecondaryColor(),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun TagChip(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = getSurfaceVariantColor(),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = getOnSurfaceColor(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun HashtagChip(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = getPrimaryColor().copy(alpha = 0.1f),
        modifier = Modifier
            .border(1.dp, getPrimaryColor().copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = getPrimaryColor(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FileStatusSection(
    hasImage: Boolean,
    hasVideo: Boolean
) {
    Column {
        Text(
            text = "Status plików",
            color = getOnSurfaceColor(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FileStatusItem(
                label = "Obraz",
                isSelected = hasImage,
                modifier = Modifier.weight(1f)
            )
            FileStatusItem(
                label = "Wideo",
                isSelected = hasVideo,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FileStatusItem(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (isSelected) Color(0xFF4CAF50) else Color(0xFFF44336),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (isSelected) "$label wybrany" else "Brak ${label.lowercase()}u",
            color = getOnSurfaceSecondaryColor(),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun BottomBar(
    step: ExerciseCreatorStep,
    isLoading: Boolean,
    canProceed: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onCreate: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (step != ExerciseCreatorStep.BASIC) {
            OutlinedButton(
                onClick = onPrev,
                modifier = Modifier.weight(1f),
                enabled = !isLoading,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, getOnSurfaceColor().copy(alpha = 0.5f))
            ) { Text("Cofnij", color = getOnSurfaceColor()) }
        }

        Button(
            onClick = if (step == ExerciseCreatorStep.SUMMARY) onCreate else onNext,
            modifier = Modifier.weight(1f),
            enabled = canProceed && !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = getPrimaryColor()),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isLoading && step == ExerciseCreatorStep.SUMMARY) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = getOnPrimaryColor(),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = if (step == ExerciseCreatorStep.SUMMARY) "Stwórz ćwiczenie" else "Kolejny etap",
                    color = getOnPrimaryColor()
                )
            }
        }
    }
}

@Composable
private fun UploadCard(
    title: String,
    subtitle: String,
    hint: String,
    selected: Boolean,
    onPick: (Uri?) -> Unit,
    mime: String,
    modifier: Modifier = Modifier
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent(), onPick)

    Card(
        modifier = modifier
            .height(200.dp)
            .clickable { launcher.launch(mime) }
            .border(1.dp, getOnSurfaceColor().copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = getPrimaryColor())
            Spacer(Modifier.height(8.dp))
            Text(title, color = getOnSurfaceColor(), fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
            Text(subtitle, color = getOnSurfaceSecondaryColor(), fontSize = 12.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(hint, color = getOnSurfaceSecondaryColor(), fontSize = 10.sp, textAlign = TextAlign.Center, lineHeight = 12.sp)
            Spacer(Modifier.height(8.dp))
            if (selected) Text("✓ Plik wybrany", color = getPrimaryColor(), fontSize = 12.sp)
        }
    }
}

@Composable
private fun DifficultyRow(text: String, selected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = getPrimaryColor(),
                unselectedColor = getOnSurfaceSecondaryColor()
            )
        )
        Spacer(Modifier.width(8.dp))
        Text(text, color = getOnSurfaceColor())
    }
}

