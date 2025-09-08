package com.example.gymeaze.ui.screens.profile.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.R
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.UserViewModel

@Composable
fun PersonalDataSection(
    viewModel: UserViewModel? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        EditPersonalDataSection(viewModel = viewModel)
    }
}

@Composable
private fun EditPersonalDataSection(
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
                text = stringResource(R.string.edit_personal_data),
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
            text = stringResource(R.string.edit_personal_data_description),
            color = getOnSurfaceSecondaryColor(),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        PersonalDataField(
            label = stringResource(R.string.username),
            value = "", // TODO: Pobrac z ViewModel
            onValueChange = { newValue ->
                // TODO: aktualizacja w ViewModel
                //viewModel?.updateFirstName(newValue)
            }
        )

        PersonalDataField(
            label = stringResource(R.string.surname),
            value = "", // TODO: Pobrac z ViewModel
            onValueChange = { newValue ->
                // TODO: aktualizacja w ViewModel
                //viewModel?.updateLastName(newValue)
            }
        )

        CountryField(viewModel = viewModel)

        Button(
            onClick = {
                // TODO: Implementacja zapisywania zmian przez viewModel
                //viewModel?.updatePersonalData()
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
private fun PersonalDataField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
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
                fieldValue = newValue
                onValueChange(newValue)
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
            singleLine = true
        )
    }
}

@Composable
private fun CountryField(
    viewModel: UserViewModel? = null
) {
    var selectedCountry by remember { mutableStateOf("Podaj kraj zamieszkania") } // TODO: pobrac z viewmodel
    var expanded by remember { mutableStateOf(false) }

    // TODO: Pobierz z viewModel gdy będzie dostępny
    LaunchedEffect(viewModel) {
        viewModel?.let {
            // selectedCountry = it.getCurrentCountry() ?: "Podaj kraj zamieszkania"
        }
    }

    // Tymczasowe TODO: pobrac z API
    val countries = listOf(
        "Polska",
        "Wielka Brytania",
        "Niemcy",
        "Hiszpania",
        "Francja",
        "Czechy",
        "Włochy",
        "Słowacja",
        "Litwa"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.country),
            color = getOnSurfaceColor(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Box {
            OutlinedTextField(
                value = selectedCountry,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = getInputBorderColor(),
                    disabledTextColor = if (selectedCountry == "Podaj kraj zamieszkania")
                        getOnSurfaceSecondaryColor() else getOnSurfaceColor()
                ),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded)
                            Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = getOnSurfaceSecondaryColor(),
                        modifier = Modifier.clickable { expanded = !expanded }
                    )
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(getSurfaceColor())
            ) {
                countries.forEach { country ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = country,
                                color = getOnSurfaceColor()
                            )
                        },
                        onClick = {
                            selectedCountry = country
                            expanded = false
                            // TODO: aktualizacja w viewmodel
                            //viewModel?.updateCountry(country)
                        }
                    )
                }
            }
        }
    }
}