package com.example.gymeaze.ui.screens.profile.sections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.R
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.UserViewModel

@Composable
fun LoginDataSection(
    viewModel: UserViewModel? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EditLoginDataSection(viewModel = viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        DeleteAccountSection(viewModel = viewModel)
    }
}

@Composable
private fun EditLoginDataSection(
    viewModel: UserViewModel? = null
) {
    Text(
        text = stringResource(R.string.edit_login_data),
        color = getOnSurfaceColor(),
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium
    )
    Text(
        text = stringResource(R.string.edit_login_data_description),
        color = getOnSurfaceSecondaryColor(),
        fontSize = 14.sp,
        lineHeight = 20.sp
    )

    UsernameField(viewModel = viewModel)

    NewPasswordField(viewModel = viewModel)

    ConfirmNewPasswordField(viewModel = viewModel)

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = {
            // TODO: Implementacja zapisywania zmian przez viewModel
            //viewModel?.updateLoginData()
        },
        modifier = Modifier
            .fillMaxWidth(),
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

@Composable
private fun UsernameField(
    viewModel: UserViewModel? = null
) {
    var username by remember { mutableStateOf("")} // TODO: Pobrać z ViewModel

    // TODO: Pobierz username z viewModel gdy będzie dostępny
    LaunchedEffect(viewModel) {
        viewModel?.let {
            // username = it.getCurrentUsername()
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.username),
                color = getOnSurfaceColor(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = getOnSurfaceSecondaryColor(),
                modifier = Modifier.size(16.dp)
            )
        }

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                // TODO: Walidacja i aktualizacja w ViewModel
                //viewModel?.updateUsername(it)
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
private fun NewPasswordField(
    viewModel: UserViewModel? = null
) {
    var newPassword by remember { mutableStateOf("")}
    var isPasswordVisible by remember { mutableStateOf(false)}

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.new_password),
            color = getOnSurfaceColor(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = {
                newPassword = it
                // TODO: Walidacja hasla i aktualizacja w ViewModel
                //viewModel?.updateNewPassword(it)
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPasswordVisible)
                VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if(isPasswordVisible)
                            Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if(isPasswordVisible)
                            "Ukryj hasło" else "Pokaż hasło",
                        tint = getOnSurfaceSecondaryColor()
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = getPrimaryColor(),
                unfocusedBorderColor = getInputBorderColor(),
                focusedTextColor = getOnSurfaceColor(),
                unfocusedTextColor = getOnSurfaceColor(),
                cursorColor = getPrimaryColor()
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
    }
}

@Composable
private fun ConfirmNewPasswordField(
    viewModel: UserViewModel? = null
) {
    var confirmPassword by remember { mutableStateOf("")}
    var isPasswordVisible by remember { mutableStateOf(false)}

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.confirm_new_password),
            color = getOnSurfaceColor(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                // TODO: Walidacja potwierdzenia hasla i aktualizacja ViewModel
                //viewModel?.updateConfirmPassword(it)
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPasswordVisible)
                VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible)
                            Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isPasswordVisible)
                            "Ukryj hasło" else "Pokaż hasło",
                        tint = getOnSurfaceSecondaryColor()
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = getPrimaryColor(),
                unfocusedBorderColor = getInputBorderColor(),
                focusedTextColor = getOnSurfaceColor(),
                unfocusedTextColor = getOnSurfaceColor(),
                cursorColor = getPrimaryColor()
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
    }
}

@Composable
private fun DeleteAccountSection(
    viewModel: UserViewModel? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFB71C1C).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFB71C1C).copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.delete_account),
                color = Color(0xFFB71C1C),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = stringResource(R.string.delete_account_description),
                color = getOnSurfaceColor(),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Button(
                onClick = {
                    // TODO: Implementacja usuwania konta przez viewModel
                    //viewModel?.deleteAccount()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB71C1C)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.delete_account),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}