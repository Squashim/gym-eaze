package com.example.gymeaze.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.data.models.Equipment
import com.example.gymeaze.data.models.MuscleGroup
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getInputBackgroundColor
import com.example.gymeaze.ui.theme.getInputBorderColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor

@Composable
fun ExerciseFiltersDialog(
    isVisible: Boolean,
    selectedMuscleGroups: List<MuscleGroup>,
    selectedEquipment: List<Equipment>,
    selectedDifficulty: String?,
    hashtagsInput: String,
    followersOnly: Boolean,
    availableMuscleGroups: List<MuscleGroup>,
    availableEquipment: List<Equipment>,
    onMuscleGroupsChanged: (List<MuscleGroup>) -> Unit,
    onEquipmentChanged: (List<Equipment>) -> Unit,
    onDifficultyChanged: (String?) -> Unit,
    onHashtagsChanged: (String) -> Unit,
    onFollowersOnlyChanged: (Boolean) -> Unit,
    onApplyFilters: () -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
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
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filtry",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = getOnSurfaceColor()
                        )

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Zamknij",
                                tint = getOnSurfaceColor()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Wybierz filtry, aby zawęzić wyniki wyszukiwania.",
                        fontSize = 14.sp,
                        color = getOnSurfaceSecondaryColor()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Grupy mięśniowe:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MuscleGroupsDropdown(
                        selectedMuscleGroups = selectedMuscleGroups,
                        availableMuscleGroups = availableMuscleGroups,
                        onSelectionChanged = onMuscleGroupsChanged
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Sprzęt ćwiczeniowy:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    EquipmentDropdown(
                        selectedEquipment = selectedEquipment,
                        availableEquipment = availableEquipment,
                        onSelectionChanged = onEquipmentChanged
                    )

                    Spacer(modifier = Modifier.height(20.dp))


                    Text(
                        text = "Hashtagi:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    HashtagsInput(
                        value = hashtagsInput,
                        onValueChanged = onHashtagsChanged
                    )

                    Spacer(modifier = Modifier.height(20.dp))


                    Text(
                        text = "Poziom trudności:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DifficultyDropdown(
                        selectedDifficulty = selectedDifficulty,
                        onSelectionChanged = onDifficultyChanged
                    )

                    Spacer(modifier = Modifier.height(20.dp))


                    Text(
                        text = "Pokaż ćwiczenia obserwowanych użytkowników:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Włącz, aby wyświetlać tylko ćwiczenia osób,\nktóre obserwujesz",
                            fontSize = 14.sp,
                            color = getOnSurfaceSecondaryColor(),
                            modifier = Modifier.weight(1f)
                        )

                        Switch(
                            checked = followersOnly,
                            onCheckedChange = onFollowersOnlyChanged,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = getPrimaryColor(),
                                checkedTrackColor = getPrimaryColor().copy(alpha = 0.5f),
                                uncheckedThumbColor = getOnSurfaceSecondaryColor(),
                                uncheckedTrackColor = getInputBackgroundColor()
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Clear Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .border(
                                    width = 1.dp,
                                    color = getInputBorderColor(),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onClearFilters() }
                                .background(getBackgroundColor()),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Wyczyść",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = getOnSurfaceColor()
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onApplyFilters() }
                                .background(getPrimaryColor()),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Zastosuj",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = getBackgroundColor()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MuscleGroupsDropdown(
    selectedMuscleGroups: List<MuscleGroup>,
    availableMuscleGroups: List<MuscleGroup>,
    onSelectionChanged: (List<MuscleGroup>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    width = 1.dp,
                    color = getInputBorderColor(),
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .background(getInputBackgroundColor())
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedMuscleGroups.isEmpty()) {
                        "Wybierz grupy mięśniowe..."
                    } else {
                        "${selectedMuscleGroups.size} wybrano"
                    },
                    fontSize = 14.sp,
                    color = if (selectedMuscleGroups.isEmpty()) {
                        getOnSurfaceSecondaryColor()
                    } else {
                        getOnSurfaceColor()
                    }
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = getOnSurfaceSecondaryColor()
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(getSurfaceColor())
        ) {
            availableMuscleGroups.forEach { muscleGroup ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedMuscleGroups.any { it._id == muscleGroup._id },
                                onCheckedChange = { checked ->
                                    val newSelection = if (checked) {
                                        selectedMuscleGroups + muscleGroup
                                    } else {
                                        selectedMuscleGroups.filter { it._id != muscleGroup._id }
                                    }
                                    onSelectionChanged(newSelection)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = getPrimaryColor(),
                                    uncheckedColor = getOnSurfaceSecondaryColor()
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = muscleGroup.name,
                                fontSize = 14.sp,
                                color = getOnSurfaceColor()
                            )
                        }
                    },
                    onClick = {
                        val isSelected = selectedMuscleGroups.any { it._id == muscleGroup._id }
                        val newSelection = if (isSelected) {
                            selectedMuscleGroups.filter { it._id != muscleGroup._id }
                        } else {
                            selectedMuscleGroups + muscleGroup
                        }
                        onSelectionChanged(newSelection)
                    }
                )
            }
        }
    }
}

@Composable
private fun EquipmentDropdown(
    selectedEquipment: List<Equipment>,
    availableEquipment: List<Equipment>,
    onSelectionChanged: (List<Equipment>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    width = 1.dp,
                    color = getInputBorderColor(),
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .background(getInputBackgroundColor())
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedEquipment.isEmpty()) {
                        "Wybierz sprzęt..."
                    } else {
                        "${selectedEquipment.size} wybrano"
                    },
                    fontSize = 14.sp,
                    color = if (selectedEquipment.isEmpty()) {
                        getOnSurfaceSecondaryColor()
                    } else {
                        getOnSurfaceColor()
                    }
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = getOnSurfaceSecondaryColor()
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(getSurfaceColor())
        ) {
            availableEquipment.forEach { equipment ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedEquipment.any { it._id == equipment._id },
                                onCheckedChange = { checked ->
                                    val newSelection = if (checked) {
                                        selectedEquipment + equipment
                                    } else {
                                        selectedEquipment.filter { it._id != equipment._id }
                                    }
                                    onSelectionChanged(newSelection)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = getPrimaryColor(),
                                    uncheckedColor = getOnSurfaceSecondaryColor()
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = equipment.name,
                                fontSize = 14.sp,
                                color = getOnSurfaceColor()
                            )
                        }
                    },
                    onClick = {
                        val isSelected = selectedEquipment.any { it._id == equipment._id }
                        val newSelection = if (isSelected) {
                            selectedEquipment.filter { it._id != equipment._id }
                        } else {
                            selectedEquipment + equipment
                        }
                        onSelectionChanged(newSelection)
                    }
                )
            }
        }
    }
}

@Composable
private fun HashtagsInput(
    value: String,
    onValueChanged: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .border(
                width = 1.dp,
                color = getInputBorderColor(),
                shape = RoundedCornerShape(8.dp)
            )
            .background(getInputBackgroundColor())
            .padding(16.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = "Podaj hashtagi, np. biceps, triceps -\nużyj Entera lub przecinka",
                fontSize = 14.sp,
                color = getOnSurfaceSecondaryColor()
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChanged,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                color = getOnSurfaceColor()
            ),
            cursorBrush = SolidColor(getPrimaryColor()),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DifficultyDropdown(
    selectedDifficulty: String?,
    onSelectionChanged: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val difficulties = listOf(
        null to "Nie wybrano",
        AdvancedLevel.EASY to AdvancedLevel.EASY,
        AdvancedLevel.MEDIUM to AdvancedLevel.MEDIUM,
        AdvancedLevel.HARD to AdvancedLevel.HARD
    )

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    width = 1.dp,
                    color = getInputBorderColor(),
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .background(getInputBackgroundColor())
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedDifficulty ?: "Nie wybrano",
                    fontSize = 14.sp,
                    color = if (selectedDifficulty == null) {
                        getOnSurfaceSecondaryColor()
                    } else {
                        getOnSurfaceColor()
                    }
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = getOnSurfaceSecondaryColor()
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(getSurfaceColor())
        ) {
            difficulties.forEach { (value, label) ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (selectedDifficulty == value) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = getPrimaryColor(),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            } else {
                                Spacer(modifier = Modifier.width(28.dp))
                            }

                            Text(
                                text = label,
                                fontSize = 14.sp,
                                color = getOnSurfaceColor()
                            )
                        }
                    },
                    onClick = {
                        onSelectionChanged(value)
                        expanded = false
                    }
                )
            }
        }
    }
}