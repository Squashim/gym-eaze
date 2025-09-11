package com.example.gymeaze.ui.screens.profile.sections
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gymeaze.R
import com.example.gymeaze.constants.Goals
import com.example.gymeaze.data.models.Badge
import com.example.gymeaze.ui.screens.profile.sections.dialogs.BadgesInfoDialog
import com.example.gymeaze.ui.screens.profile.sections.dialogs.TrainingGoalInfoDialog
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.UserViewModel

@Composable
fun ProfileInfoSection(
    viewModel: UserViewModel
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val availableBadges by viewModel.availableBadges.collectAsState()
    val selectedPinnedBadges by viewModel.selectedPinnedBadges.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()

    var biography by remember { mutableStateOf(userProfile?.description.orEmpty()) }
    var selectedGoal by remember { mutableStateOf(userProfile?.goal.orEmpty()) }


    DisposableEffect(Unit) {
        onDispose {
            viewModel.onScreenExit()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initializeProfileData()
    }

    LaunchedEffect(userProfile) {
        userProfile?.let { profile ->
            biography = profile.description.orEmpty()
            selectedGoal = profile.goal.orEmpty()
            if (selectedPinnedBadges.isEmpty() ||
                selectedPinnedBadges.map { it.id }.sorted() != profile.pinnedBadges.map { it.id }.sorted()) {
                viewModel.setSelectedPinnedBadges(profile.pinnedBadges)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ProfilePictureSection(
            profilePictureUrl = userProfile?.profilePicture.orEmpty(),
            selectedImageUri = selectedImageUri,
            onImageSelected = { uri -> viewModel.setSelectedImage(uri) }
        )

        BiographySection(
            biography = biography,
            onBiographyChange = { newValue -> biography = newValue }
        )

        TrainingGoalSection(
            selectedGoal = selectedGoal,
            onGoalChange = { newGoal -> selectedGoal = newGoal },
            isGoalPublic = userProfile?.isGoalPublic ?: true,
        )

        BadgesSection(
            pinnedBadges = selectedPinnedBadges,
            isBadgesDetailsPublic = userProfile?.isBadgesDetailsPublic ?: true,
            availableBadges = availableBadges,
            currentlyPinnedBadges = userProfile?.pinnedBadges ?: emptyList(),
            onBadgesSelected = { badges ->
                viewModel.setSelectedPinnedBadges(badges)
            },
            isLoading = isLoading
        )

        Button(
            onClick = {
                viewModel.updateProfile(
                    description = biography,
                    goal = if (selectedGoal == Goals.NONE) null else selectedGoal,
                    pinnedBadges = selectedPinnedBadges
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = getPrimaryColor(),
                contentColor = getOnPrimaryColor()
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = getOnPrimaryColor()
                )
            } else {

                Text(
                    text = stringResource(R.string.save_changes),
                    color = getOnPrimaryColor(),
                    fontSize = 16.sp
                )
            }
        }

        error?.let {
            Text(
                text = it,
                color = getErrorColor(),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ProfilePictureSection(
    profilePictureUrl: String,
    selectedImageUri: Uri? = null,
    onImageSelected: ((Uri?) -> Unit)? = null
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected?.invoke(uri)
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.profile_picture),
            color = getOnSurfaceColor(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(getSurfaceColor())
                    .border(1.dp, getOnSurfaceSecondaryColor().copy(alpha = 0.3f), CircleShape)
                    .clickable {
                        if (onImageSelected != null) launcher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                when {
                    selectedImageUri != null -> {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    profilePictureUrl.isNotEmpty() -> {
                        AsyncImage(
                            model = profilePictureUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = getOnSurfaceSecondaryColor(),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            if (onImageSelected != null) {
                Button(
                    onClick = { launcher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getSurfaceColor(),
                        contentColor = getOnSurfaceColor()
                    ),
                    border = BorderStroke(1.dp, getOnSurfaceSecondaryColor().copy(alpha = 0.3f)),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = when {
                            selectedImageUri != null -> stringResource(R.string.new_photo_selected)
                            profilePictureUrl.isNotEmpty() -> stringResource(R.string.change_photo)
                            else -> stringResource(R.string.add_photo)
                        },
                        fontSize = 14.sp
                    )
                }
            } else {
                Text(
                    text = when {
                        selectedImageUri != null -> stringResource(R.string.new_photo_selected)
                        profilePictureUrl.isNotEmpty() -> stringResource(R.string.change_photo)
                        else -> stringResource(R.string.no_picture_selected)
                    },
                    color = getOnSurfaceSecondaryColor(),
                    fontSize = 14.sp
                )
            }
        }

        Text(
            text = stringResource(R.string.picture_requirements),
            color = getOnSurfaceSecondaryColor(),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun BiographySection(
    biography: String,
    onBiographyChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.biography),
            color = getOnSurfaceColor(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        OutlinedTextField(
            value = biography,
            onValueChange = { newValue ->
                if(newValue.length <= 150) {
                    onBiographyChange(newValue)
                }
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.biography_placeholder),
                    color = getOnSurfaceSecondaryColor()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = getPrimaryColor(),
                unfocusedBorderColor = getInputBorderColor(),
                focusedTextColor = getOnSurfaceColor(),
                unfocusedTextColor = getOnSurfaceColor(),
                cursorColor = getPrimaryColor()
            ),
            shape = RoundedCornerShape(8.dp),
            maxLines = 5
        )

        Text(
            text = stringResource(R.string.biography_limit_current, biography.length),
            color = getOnSurfaceSecondaryColor(),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun TrainingGoalSection(
    selectedGoal: String,
    isGoalPublic: Boolean,
    onGoalChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showInfoDialog by remember {mutableStateOf(false)}

    val goalOptions = listOf(
        Goals.NONE,
        Goals.MUSCLE_GAIN,
        Goals.STRENGTH,
        Goals.FAT_LOSS,
        Goals.ENDURANCE,
        Goals.FLEXIBILITY,
        Goals.CALORIE_BURN,
        Goals.MASS_GAIN,
        Goals.COORDINATION,
        Goals.MOBILITY,
        Goals.LEGS_GLUTES,
        Goals.POSTURE,
        Goals.RECOVERY
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.training_goal),
                color = getOnSurfaceColor(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Icon(
                imageVector = if (isGoalPublic) Icons.Default.Public else Icons.Default.Lock,
                contentDescription = if (isGoalPublic)
                    stringResource(R.string.public_goal_desc) else stringResource(R.string.private_goal_desc),
                tint = getOnSurfaceSecondaryColor(),
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(R.string.training_goal_info),
                tint = getOnSurfaceSecondaryColor(),
                modifier = Modifier
                    .size(16.dp)
                    .clickable{
                        showInfoDialog = true
                    }
            )
        }


        Box {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = getSurfaceColor()
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (expanded) getPrimaryColor() else getInputBorderColor()
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (expanded) 4.dp else 0.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (selectedGoal.isEmpty() || selectedGoal == Goals.NONE) {
                            stringResource(R.string.select_training_goal)
                        } else {
                            selectedGoal
                        },
                        color = if (selectedGoal.isEmpty() || selectedGoal == Goals.NONE) {
                            getOnSurfaceSecondaryColor()
                        } else {
                            getOnSurfaceColor()
                        },
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = if (expanded) getPrimaryColor() else getOnSurfaceSecondaryColor(),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(max = 300.dp)
                    .background(
                        color = getCardBackgroundColor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = getInputBorderColor(),
                        shape = RoundedCornerShape(12.dp)
                    ),
                offset = DpOffset(0.dp, 4.dp)
            ) {
                goalOptions.forEachIndexed { index, goal ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = goal,
                                color = if (selectedGoal == goal) getPrimaryColor() else getOnSurfaceColor(),
                                fontWeight = if (selectedGoal == goal) FontWeight.Medium else FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        },
                        onClick = {
                            onGoalChange(goal)
                            expanded = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (selectedGoal == goal) {
                                    getPrimaryColor().copy(alpha = 0.1f)
                                } else {
                                    getCardBackgroundColor()
                                }
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )

                    if (index < goalOptions.size - 1) {
                        HorizontalDivider(
                            color = getInputBorderColor().copy(alpha = 0.3f),
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }

    if(showInfoDialog) {
        TrainingGoalInfoDialog(
            onDismiss = { showInfoDialog = false }
        )
    }
}

@Composable
private fun BadgesSection(
    pinnedBadges: List<Badge>,
    isBadgesDetailsPublic: Boolean,
    availableBadges: List<Badge>,
    currentlyPinnedBadges: List<Badge>,
    onBadgesSelected: (List<Badge>) -> Unit,
    isLoading: Boolean = false
) {
    var showBadgeSelectionDialog by remember { mutableStateOf(false) }
    var showBadgesInfoDialog by remember { mutableStateOf(false) }


    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.pinned_badges),
                color = getOnSurfaceColor(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Icon(
                imageVector = if (isBadgesDetailsPublic) Icons.Default.Public else Icons.Default.Lock,
                contentDescription = if (isBadgesDetailsPublic)
                    stringResource(R.string.public_badges_desc) else stringResource(R.string.private_badges_desc),
                tint = getOnSurfaceSecondaryColor(),
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(R.string.pinned_badges_info),
                tint = getOnSurfaceSecondaryColor(),
                modifier = Modifier
                    .size(16.dp)
                    .clickable{
                        showBadgesInfoDialog = true
                    }
            )
        }

        if (pinnedBadges.isEmpty()) {
            OutlinedButton(
                onClick = { showBadgeSelectionDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = getOnSurfaceColor()
                ),
                border = BorderStroke(1.dp, getOnSurfaceColor().copy(alpha = 0.5f)),
                enabled = !isLoading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = getOnSurfaceColor(),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(R.string.no_pinned_badges),
                        color = getOnSurfaceColor()
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    pinnedBadges.forEach { badge ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    getSurfaceColor(),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    getOnSurfaceSecondaryColor().copy(alpha = 0.2f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = badge.image,
                                contentDescription = badge.title,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = badge.title,
                                    color = getOnSurfaceColor(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = badge.description,
                                    color = getOnSurfaceSecondaryColor(),
                                    fontSize = 12.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Jeden przycisk do zarządzania odznakami
                OutlinedButton(
                    onClick = { showBadgeSelectionDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, getPrimaryColor()),
                    enabled = !isLoading
                ) {
                    Text(
                        text = stringResource(R.string.manage_badges),
                        color = getPrimaryColor()
                    )
                }
            }
        }
    }

    if (showBadgeSelectionDialog) {
        BadgeSelectionDialog(
            availableBadges = availableBadges,
            currentlySelectedBadges = pinnedBadges,
            onDismiss = {
                showBadgeSelectionDialog = false
                onBadgesSelected(currentlyPinnedBadges)
            },
            onConfirm = { selectedBadges ->
                onBadgesSelected(selectedBadges)
                showBadgeSelectionDialog = false
            },
            isLoading = isLoading
        )
    }

    if (showBadgesInfoDialog) {
        BadgesInfoDialog(
            onDismiss = { showBadgesInfoDialog = false }
        )
    }
}