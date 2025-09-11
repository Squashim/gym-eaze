package com.example.gymeaze.ui.screens.profile.sections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.R
import com.example.gymeaze.ui.screens.profile.sections.dialogs.PhysicalInfoDialog
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.UserViewModel

@Composable
fun PhysicalInfoSection(
    viewModel: UserViewModel? = null
) {
    val selectedGender by viewModel?.selectedGender?.collectAsState() ?: remember { mutableStateOf(null) }
    val age by viewModel?.age?.collectAsState() ?: remember { mutableStateOf("") }
    val height by viewModel?.height?.collectAsState() ?: remember { mutableStateOf("") }
    val weight by viewModel?.weight?.collectAsState() ?: remember { mutableStateOf("") }
    val isLoading by viewModel?.fitnessDataLoading?.collectAsState() ?: remember { mutableStateOf(false) }
    val error by viewModel?.fitnessDataError?.collectAsState() ?: remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        viewModel?.fetchUserFitnessData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Wyświetl błąd jeśli istnieje
        error?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = getErrorColor(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            EditPhysicalInfoSection(
                viewModel = viewModel,
                selectedGender = selectedGender,
                age = age,
                height = height,
                weight = weight
            )
        }
    }
}


@Composable
private fun EditPhysicalInfoSection(
    viewModel: UserViewModel? = null,
    selectedGender: String?,
    age: String,
    height: String,
    weight: String
) {
    val isFitnessDataPublic by viewModel?.isFitnessDetailsPublic?.collectAsState(true) ?: remember { mutableStateOf(true) }
    var showPhysicalInfoDialog by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.edit_physical_info),
                color = getOnSurfaceColor(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Icon(
                imageVector = if (isFitnessDataPublic) Icons.Default.Public else Icons.Default.Lock,
                contentDescription = if (isFitnessDataPublic)
                    stringResource(R.string.public_badges_desc) else stringResource(R.string.private_badges_desc),
                tint = getOnSurfaceSecondaryColor(),
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(R.string.physical_info_desc),
                tint = getOnSurfaceSecondaryColor(),
                modifier = Modifier
                    .size(16.dp)
                    .clickable{
                        showPhysicalInfoDialog = true
                    }
            )
        }

        Text(
            text = stringResource(R.string.edit_physical_info_description),
            color = getOnSurfaceSecondaryColor(),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        GenderSection(
            viewModel = viewModel,
            selectedGender = selectedGender
        )

        PhysicalDataField(
            label = stringResource(R.string.age),
            value = age,
            onValueChange = { newValue ->
                viewModel?.updateAge(newValue)
            },
            keyboardType = KeyboardType.Number
        )

        PhysicalDataField(
            label = stringResource(R.string.height_cm),
            value = height,
            onValueChange = { newValue ->
                viewModel?.updateHeight(newValue)
            },
            keyboardType = KeyboardType.Number
        )

        PhysicalDataField(
            label = stringResource(R.string.weight_kg),
            value = weight,
            onValueChange = { newValue ->
                viewModel?.updateWeight(newValue)
            },
            keyboardType = KeyboardType.Number
        )

        Button(
            onClick = {
                viewModel?.updateFitnessData()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                contentColor = getOnPrimaryColor(),
                containerColor = getPrimaryColor()
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(R.string.save_changes),
                color = getBackgroundColor(),
                fontSize = 16.sp
            )
        }
    }

    if (showPhysicalInfoDialog) {
        PhysicalInfoDialog(
            onDismiss = { showPhysicalInfoDialog = false }
        )
    }
}

@Composable
private fun GenderSection(
    viewModel: UserViewModel? = null,
    selectedGender: String?
) {
    val notSelectedValue = stringResource(R.string.gender_not_selected_value)
    val maleValue = stringResource(R.string.gender_male_value)
    val femaleValue = stringResource(R.string.gender_female_value)

    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = stringResource(R.string.gender),
            color = getOnSurfaceColor(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        GenderOption(
            text = stringResource(R.string.prefer_not_to_say_default),
            isSelected = selectedGender == notSelectedValue || selectedGender == null,
            onClick = {
                viewModel?.updateGender(notSelectedValue)
            }
        )

        GenderOption(
            text = stringResource(R.string.male),
            isSelected = selectedGender == maleValue,
            onClick = {
                viewModel?.updateGender(maleValue)
            }
        )

        GenderOption(
            text = stringResource(R.string.female),
            isSelected = selectedGender == femaleValue,
            onClick = {
                viewModel?.updateGender(femaleValue)
            }
        )
    }
}

@Composable
private fun GenderOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = getPrimaryColor(),
                unselectedColor = getOnSurfaceSecondaryColor()
            )
        )

        Text(
            text = text,
            color = getOnSurfaceColor(),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun PhysicalDataField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var fieldValue by remember { mutableStateOf(value) }

    LaunchedEffect(value) {
        fieldValue = value
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            color = getOnSurfaceColor(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        OutlinedTextField(
            value = fieldValue,
            onValueChange = { newValue ->
                if (keyboardType == KeyboardType.Number) {
                    if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                        fieldValue = newValue
                        onValueChange(newValue)
                    }
                } else {
                    fieldValue = newValue
                    onValueChange(newValue)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = getPrimaryColor(),
                unfocusedBorderColor = getInputBorderColor(),
                focusedTextColor = getOnSurfaceColor(),
                unfocusedTextColor = getOnSurfaceColor(),
                cursorColor = getPrimaryColor()
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}