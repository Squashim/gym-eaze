package com.example.gymeaze.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gymeaze.R
import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.constants.PrivacySettings
import com.example.gymeaze.data.models.Exercise
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
    onCreated: () -> Unit,
    exerciseToEdit: Exercise? = null
) {
    val ui by viewModel.uiState.collectAsState()
    var showExitDialog by remember {mutableStateOf(false)}

    LaunchedEffect(exerciseToEdit) {
        exerciseToEdit?.let {
            viewModel.loadExerciseForEdit(it)
        }
    }


    LaunchedEffect(ui.success) {
        if (ui.success){
            onCreated()
            viewModel.clearSuccess()
            viewModel.clearData()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (ui.isEditMode) {
                            "Edytuj ćwiczenie"
                        } else {
                            stringResource(R.string.exercise_creator)
                        },
                        color = getOnSurfaceColor(),
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = getOnSurfaceColor()
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.cancel_creation),
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
            ) {
                Stepper(ui.currentStep)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (ui.currentStep) {
                    ExerciseCreatorStep.BASIC -> StepBasic(ui, viewModel)
                    ExerciseCreatorStep.EXTRA -> StepExtra(ui, viewModel)
                    ExerciseCreatorStep.SUMMARY -> StepSummary(ui)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
            ) {

                BottomBar(
                    step = ui.currentStep,
                    isLoading = ui.isLoading,
                    canProceed = when (ui.currentStep) {
                        ExerciseCreatorStep.BASIC -> ui.basicValid
                        ExerciseCreatorStep.EXTRA -> ui.extraValid
                        ExerciseCreatorStep.SUMMARY -> true
                    },
                    onPrev = viewModel::prevStep,
                    onNext = viewModel::nextStep,
                    onCreate = viewModel::create,
                    isEditMode = ui.isEditMode
                )
            }
        }
    }

    if (ui.error != null) {
        AlertDialog(
            onDismissRequest = viewModel::clearError,
            title = { Text(stringResource(R.string.error)) },
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
fun ExitConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = getSurfaceColor(),
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = stringResource(R.string.exit_creation_confirmation_title),
                color = getOnSurfaceColor(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        },
        text = {
            Text(
                text = stringResource(R.string.exit_creation_confirmation_message),
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
                        text = stringResource(R.string.return_button),
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
                        text = stringResource(R.string.interrupt),
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
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
            val bg = if (active || done) getPrimaryColor() else getNavBarUnselectedColor()
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
            node(0, stringResource(R.string.basic_data_step))
        }
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(horizontal = 8.dp),
            color = getInputBorderColor()
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            node(1, stringResource(R.string.additional_info_step))
        }
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(horizontal = 8.dp),
            color = getInputBorderColor()
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            node(2, stringResource(R.string.summary_step))
        }
    }
}

@Composable
private fun StepBasic(ui: ExerciseCreatorState, vm: ExerciseCreatorViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(stringResource(R.string.exercise_name_required), color = getOnSurfaceColor(), fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = ui.name,
                onValueChange = vm::setName,
                placeholder = { Text(stringResource(R.string.enter_exercise_name), color = getOnSurfaceSecondaryColor()) },
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
                title = if (ui.isEditMode && ui.existingImageUrl != null) {
                    "Zmień obraz"
                } else {
                    stringResource(R.string.exercise_image_required)
                },
                subtitle = if (ui.isEditMode && ui.existingImageUrl != null) {
                    "Obecny: ${ui.existingImageUrl?.takeLast(20) ?: ""}"
                } else {
                    stringResource(R.string.click_to_upload)
                },
                hint = stringResource(R.string.image_upload_requirements),
                selected = ui.imageUri != null || ui.existingImageUrl != null,
                onPick = vm::setImage,
                mime = "image/*",
                modifier = Modifier.weight(1f),
                onRemove = if (ui.imageUri != null) {
                    { vm.setImage(null) }
                } else null
            )
            UploadCard(
                title = if (ui.isEditMode && ui.existingVideoUrl != null) {
                    "Zmień wideo"
                } else {
                    stringResource(R.string.instructional_video_required)
                },
                subtitle = if (ui.isEditMode && ui.existingVideoUrl != null) {
                    "Obecne: ${ui.existingVideoUrl?.takeLast(20) ?: ""}"
                } else {
                    stringResource(R.string.click_to_upload)
                },
                hint = stringResource(R.string.video_upload_requirements),
                selected = ui.videoUri != null || ui.existingVideoUrl != null,
                onPick = vm::setVideo,
                mime = "video/*",
                modifier = Modifier.weight(1f),
                onRemove = if (ui.videoUri != null) {
                    { vm.setVideo(null) }
                } else null
            )
        }

        Column {
            Text(stringResource(R.string.difficulty_level_required), color = getOnSurfaceColor(), fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            DifficultyRow(AdvancedLevel.NONE, ui.difficulty == AdvancedLevel.NONE) { vm.setDifficulty(AdvancedLevel.NONE) }
            DifficultyRow(AdvancedLevel.EASY, ui.difficulty == AdvancedLevel.EASY) { vm.setDifficulty(AdvancedLevel.EASY) }
            DifficultyRow(AdvancedLevel.MEDIUM, ui.difficulty == AdvancedLevel.MEDIUM) { vm.setDifficulty(AdvancedLevel.MEDIUM) }
            DifficultyRow(AdvancedLevel.HARD, ui.difficulty == AdvancedLevel.HARD) { vm.setDifficulty(AdvancedLevel.HARD) }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.public_exercise_required), color = getOnSurfaceColor(), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(
                    stringResource(R.string.public_exercise_description),
                    color = getOnSurfaceSecondaryColor(),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            Switch(
                checked = ui.isPublic == PrivacySettings.PUBLIC,
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
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.exercise_description_required),
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
                        text = stringResource(R.string.exercise_description_placeholder),
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
                        text = stringResource(R.string.max_description_length),
                        color = getOnSurfaceSecondaryColor(),
                        fontSize = 12.sp
                    )
                },
                isError = ui.description.length > 850
            )
        }

        MultiSelectDropdown(
            label = stringResource(R.string.muscle_groups_required),
            selectedItems = ui.selectedMuscleGroups,
            availableItems = ui.availableMuscleGroups,
            onSelectionChange = vm::setMuscleGroups,
            itemDisplayText = { it.name },
            isLoading = ui.isLoadingData,
            placeholder = stringResource(R.string.select_muscle_groups),
            modifier = Modifier.fillMaxWidth(),
            itemContent = { muscleGroup ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = muscleGroup.image,
                        contentDescription = muscleGroup.name,
                        modifier = Modifier.size(28.dp).clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = muscleGroup.name,
                        color = getOnSurfaceColor()
                    )
                }
            }
        )

        MultiSelectDropdown(
            label = stringResource(R.string.exercise_equipment_required),
            selectedItems = ui.selectedEquipment,
            availableItems = ui.availableEquipment,
            onSelectionChange = vm::setEquipment,
            itemDisplayText = { it.name },
            isLoading = ui.isLoadingData,
            placeholder = stringResource(R.string.select_equipment),
            modifier = Modifier.fillMaxWidth(),
            itemContent = { equipment ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = equipment.image,
                        contentDescription = equipment.name,
                        modifier = Modifier.size(28.dp).clip(RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = equipment.name,
                        color = getOnSurfaceColor()
                    )
                }
            }
        )

        HashtagInputField(
            selectedHashtags = ui.hashtags,
            onHashtagsChange = vm::setHashtags,
            maxHashtags = 50
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StepSummary(ui: ExerciseCreatorState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = stringResource(R.string.verify_data_correctness),
            color = getOnSurfaceColor(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        SummaryField(
            label = stringResource(R.string.exercise_name_colon),
            value = ui.name
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.difficulty_level_colon),
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
                    text = stringResource(R.string.exercise_visibility_colon),
                    color = getOnSurfaceColor(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                VisibilityBadge(
                    isPublic = ui.isPublic === PrivacySettings.PUBLIC
                )
            }
        }

        ExpandableSummaryField(
            label = stringResource(R.string.exercise_description_colon),
            value = ui.description
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.muscle_groups_colon),
                color = getOnSurfaceColor(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            TagGroupWithImage(
                items = ui.selectedMuscleGroups
            )
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.required_equipment_colon),
                color = getOnSurfaceColor(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            EquipmentTagGroup(
                items = ui.selectedEquipment
            )
        }

        if (ui.hashtags.isNotEmpty()) {
            ExpandableTagField(
                items = ui.hashtags.map { "#${it}" }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
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
                    contentDescription = if (isExpanded) stringResource(R.string.collapse) else stringResource(R.string.expand),
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
    val (text, color, textColor) = when (difficulty) {
        AdvancedLevel.NONE -> Triple(AdvancedLevel.NONE, getSurfaceVariantColor(), getOnSurfaceColor())
        AdvancedLevel.EASY -> Triple(AdvancedLevel.EASY, getPrimaryColor(), getOnPrimaryColor())
        AdvancedLevel.MEDIUM -> Triple(AdvancedLevel.MEDIUM, getYellowColor(), getOnYellowColor())
        AdvancedLevel.HARD -> Triple(AdvancedLevel.HARD, getErrorColor().copy(alpha = 0.1f), getErrorColor())
        else -> Triple(AdvancedLevel.NONE, getSurfaceVariantColor(), getOnSurfaceColor())
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun VisibilityBadge(isPublic: Boolean) {
    val (text, color) = if (isPublic) {
        PrivacySettings.PUBLIC to getOnSurfaceColor()
    } else {
        PrivacySettings.PRIVATE to Color(0xFF9E9E9E)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = getSurfaceColor(),
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
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 120.dp)
    ) {
        items(items) { item ->
            TagChip(text = item)
        }
    }
}

@Composable
private fun TagGroupWithImage(items: List<com.example.gymeaze.data.models.MuscleGroup>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 120.dp)
    ) {
        items(items) { item ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(getPrimaryColor().copy(alpha = 0.1f), CircleShape)
                        .border(2.dp, getPrimaryColor(), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = item.image,
                        contentDescription = item.name,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.name,
                    fontSize = 12.sp,
                    color = getOnSurfaceColor(),
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun EquipmentTagGroup(items: List<com.example.gymeaze.data.models.Equipment>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 120.dp)
    ) {
        items(items) { item ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(getOnSurfaceColor().copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = item.image,
                        contentDescription = item.name,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.name,
                    fontSize = 12.sp,
                    color = getOnSurfaceColor(),
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        }
    }
}


@Composable
private fun ExpandableTagField(
    items: List<String>
) {
    var isExpanded by remember { mutableStateOf(false) }
    val displayItems = if (isExpanded) items else items.take(6)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.hashtags),
                color = getOnSurfaceColor(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            if (items.size > 6) {
                Icon(
                    imageVector = if (isExpanded)
                        Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) stringResource(R.string.collapse) else stringResource(R.string.expand),
                    tint = getOnSurfaceSecondaryColor(),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { isExpanded = !isExpanded }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.heightIn(max = if (isExpanded) 200.dp else 80.dp)
        ) {
            items(displayItems) { item ->
                HashtagChip(text = item)
            }
        }

        if (!isExpanded && items.size > 6) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.more_hashtags, items.size - 6),
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
        color = getSecondaryColor(),
        modifier = Modifier.padding(vertical = 2.dp).wrapContentWidth()
    ) {
        Text(
            text = text,
            color = getOnSecondaryColor(),
            fontSize = 12.sp,
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
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
    onCreate: () -> Unit,
    isEditMode: Boolean = false
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
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(1.dp, getOnSurfaceColor().copy(alpha = 0.5f))
            ) {
                Text(stringResource(R.string.go_back), color = getOnSurfaceColor())
            }
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
                    text = if (step == ExerciseCreatorStep.SUMMARY) {
                        if (isEditMode) {
                            "Zaktualizuj ćwiczenie"
                        } else {
                            stringResource(R.string.create_exercise)
                        }
                    } else {
                        stringResource(R.string.next_step)
                    },
                    color = getOnPrimaryColor()
                )
            }
        }
    }
}

/*
@Composable
fun UploadCard(
    title: String,
    subtitle: String,
    hint: String,
    selected: Boolean,
    onPick: (Uri?) -> Unit,
    mime: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent(), onPick)

    val alpha = if (enabled) 1f else 0.5f
    val cardColor = if (enabled) getSurfaceColor() else getSurfaceColor().copy(alpha = 0.6f)
    val borderColor = if (enabled) {
        getOnSurfaceColor().copy(alpha = 0.3f)
    } else {
        getOnSurfaceColor().copy(alpha = 0.15f)
    }

    Card(
        modifier = modifier
            .height(220.dp)
            .clickable(enabled = enabled) {
                if (enabled) launcher.launch(mime)
            }
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .alpha(alpha),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.CloudUpload,
                contentDescription = null,
                tint = if (enabled) getPrimaryColor() else getPrimaryColor().copy(alpha = 0.5f),
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                title,
                color = if (enabled) getOnSurfaceColor() else getOnSurfaceColor().copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                subtitle,
                color = if (enabled) getOnSurfaceSecondaryColor() else getOnSurfaceSecondaryColor().copy(alpha = 0.5f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                hint,
                color = if (enabled) getOnSurfaceSecondaryColor() else getOnSurfaceSecondaryColor().copy(alpha = 0.5f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
            Spacer(Modifier.height(6.dp))
            if (selected) {
                Text(
                    stringResource(R.string.file_selected),
                    color = if (enabled) getPrimaryColor() else getPrimaryColor().copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

 */

@Composable
fun UploadCard(
    title: String,
    subtitle: String,
    hint: String,
    selected: Boolean,
    onPick: (Uri?) -> Unit,
    mime: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onRemove: (() -> Unit)? = null
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent(), onPick)

    val alpha = if (enabled) 1f else 0.5f
    val cardColor = if (enabled) getSurfaceColor() else getSurfaceColor().copy(alpha = 0.6f)
    val borderColor = if (enabled) {
        getOnSurfaceColor().copy(alpha = 0.3f)
    } else {
        getOnSurfaceColor().copy(alpha = 0.15f)
    }

    Card(
        modifier = modifier
            .height(220.dp)
            .clickable(enabled = enabled) {
                if (enabled) launcher.launch(mime)
            }
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .alpha(alpha),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.CloudUpload,
                contentDescription = null,
                tint = if (enabled) getPrimaryColor() else getPrimaryColor().copy(alpha = 0.5f),
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                title,
                color = if (enabled) getOnSurfaceColor() else getOnSurfaceColor().copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                subtitle,
                color = if (enabled) getOnSurfaceSecondaryColor() else getOnSurfaceSecondaryColor().copy(alpha = 0.5f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                hint,
                color = if (enabled) getOnSurfaceSecondaryColor() else getOnSurfaceSecondaryColor().copy(alpha = 0.5f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
            Spacer(Modifier.height(6.dp))
            if (selected) {
                Text(
                    stringResource(R.string.file_selected),
                    color = if (enabled) getPrimaryColor() else getPrimaryColor().copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
                if (onRemove != null) {
                    Spacer(Modifier.height(6.dp))
                    TextButton(onClick = onRemove) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = getErrorColor())
                        Spacer(Modifier.width(4.dp))
                        Text(
                            stringResource(R.string.delete),
                            color = getErrorColor(),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun DifficultyRow(text: String, selected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
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
        Spacer(Modifier.width(4.dp))
        Text(text, color = getOnSurfaceColor())
    }
}