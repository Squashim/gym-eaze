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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
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
    LaunchedEffect(viewModel) {
        viewModel?.fetchUserPersonalData()
    }

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
    val firstName by viewModel?.firstName?.collectAsState("") ?: remember { mutableStateOf("") }
    val lastName by viewModel?.lastName?.collectAsState("") ?: remember { mutableStateOf("") }
    val selectedCountry by viewModel?.selectedCountry?.collectAsState() ?: remember { mutableStateOf(null) }
    val isPersonalDataPublic by viewModel?.isPersonalDetailsPublic?.collectAsState(true) ?: remember { mutableStateOf(true) }
    val isLoading by viewModel?.personalDataLoading?.collectAsState(false) ?: remember { mutableStateOf(false) }
    val error by viewModel?.personalDataError?.collectAsState() ?: remember { mutableStateOf(null) }

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
                imageVector = if (isPersonalDataPublic) Icons.Default.Public else Icons.Default.Lock,
                contentDescription = if (isPersonalDataPublic) "Public badges" else "Private badges",
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

        error?.let { errorMessage ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp
                )
            }
        }

        PersonalDataField(
            label = stringResource(R.string.name),
            value = firstName,
            enabled = !isLoading,
            onValueChange = { newValue ->
                viewModel?.updateFirstName(newValue)
            }
        )

        PersonalDataField(
            label = stringResource(R.string.surname),
            value = lastName,
            enabled = !isLoading,
            onValueChange = { newValue ->
                viewModel?.updateLastName(newValue)
            }
        )

        CountryField(
            viewModel = viewModel,
            enabled = !isLoading,
            selectedCountry = selectedCountry
        )

        Button(
            onClick = {
                viewModel?.updatePersonalData()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = getOnSurfaceColor()
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isLoading) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = getBackgroundColor(),
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Zapisywanie...",
                        color = getBackgroundColor(),
                        fontSize = 16.sp
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.save_changes),
                    color = getBackgroundColor(),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun PersonalDataField(
    label: String,
    value: String,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit
) {
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
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = getPrimaryColor(),
                unfocusedBorderColor = getInputBorderColor(),
                focusedTextColor = getOnSurfaceColor(),
                unfocusedTextColor = getOnSurfaceColor(),
                disabledTextColor = getOnSurfaceSecondaryColor(),
                disabledBorderColor = getInputBorderColor().copy(alpha = 0.5f),
                cursorColor = getPrimaryColor()
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
    }
}

@Composable
private fun CountryField(
    viewModel: UserViewModel? = null,
    enabled: Boolean = true,
    selectedCountry: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    val displayText = selectedCountry ?: "Podaj kraj zamieszkania"

    //TUTAJ POBIERAJ Z ENUMA ---------------------------------------------------------------
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
                value = displayText,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .let { if (enabled) it.clickable { expanded = true } else it },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = if (enabled) getInputBorderColor() else getInputBorderColor().copy(alpha = 0.5f),
                    disabledTextColor = if (selectedCountry == null)
                        getOnSurfaceSecondaryColor() else getOnSurfaceColor()
                ),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    if (enabled) {
                        Icon(
                            imageVector = if (expanded)
                                Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = getOnSurfaceSecondaryColor(),
                            modifier = Modifier.clickable { expanded = !expanded }
                        )
                    }
                }
            )

            if (enabled) {
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
                                viewModel?.updateCountry(country)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}