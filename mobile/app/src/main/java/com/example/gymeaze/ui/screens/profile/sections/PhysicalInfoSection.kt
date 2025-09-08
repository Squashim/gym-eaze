package com.example.gymeaze.ui.screens.profile.sections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.UserViewModel

@Composable
fun PhysicalInfoSection(
    viewModel: UserViewModel? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        EditPhysicalInfoSection(viewModel = viewModel)
    }
}

@Composable
private fun EditPhysicalInfoSection(
    viewModel: UserViewModel? = null
) {
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
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = getOnSurfaceSecondaryColor(),
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = stringResource(R.string.edit_physical_info_description),
            color = getOnSurfaceSecondaryColor(),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        GenderSection(viewModel = viewModel)

        PhysicalDataField(
            label = stringResource(R.string.age),
            value = "", // TODO: pobrac z viewmodel
            onValueChange = { newValue ->
                // TODO: aktualizacja w viewmodel
                //viewModel?.updateAge(newValue)
            },
            keyboardType = KeyboardType.Number
        )

        PhysicalDataField(
            label = stringResource(R.string.height_cm),
            value = "", // TODO: pobrac z viewmodel
            onValueChange = { newValue ->
                // TODO: aktualizacja w viewmodel
                //viewModel?.updateHeight(newValue)
            },
            keyboardType = KeyboardType.Number
        )

        PhysicalDataField(
            label = stringResource(R.string.weight_kg),
            value = "", // TODO: pobrac z viewmodel
            onValueChange = { newValue ->
                // TODO: aktualizacja w viewmodel
                //viewModel?.updateWeight(newValue)
            },
            keyboardType = KeyboardType.Number
        )

        Button(
            onClick = {
                // TODO: Implementacja zapisywania zmian przez viewModel
                //viewModel?.updatePhysicalInfo()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = getOnSurfaceColor()
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
}

@Composable
private fun GenderSection(
    viewModel: UserViewModel? = null
) {
    // TODO: pobrac z viewmodel
    var selectedGender by remember { mutableStateOf("prefer_not_to_say") } // "male", "female", "prefer_not_to_say"

    // TODO: Pobierz z viewModel gdy będzie dostępny
    LaunchedEffect(viewModel) {
        viewModel?.let {
            // selectedGender = it.getCurrentGender() ?: "prefer_not_to_say"
        }
    }

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
            isSelected = selectedGender == "prefer_not_to_say",
            onClick = {
                selectedGender = "prefer_not_to_say"
                // TODO: aktualizacja w viewmodel
                //viewModel?.updateGender("prefer_not_to_say")
            }
        )

        GenderOption(
            text = stringResource(R.string.male),
            isSelected = selectedGender == "male",
            onClick = {
                selectedGender = "male"
                // TODO: aktualizacja w viewmodel
                //viewModel?.updateGender("male")
            }
        )

        GenderOption(
            text = stringResource(R.string.female),
            isSelected = selectedGender == "female",
            onClick = {
                selectedGender = "female"
                // TODO: aktualizacja w viewmodel
                //viewModel?.updateGender("female")
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

    // Aktualizuj lokalny stan gdy wartość się zmieni
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