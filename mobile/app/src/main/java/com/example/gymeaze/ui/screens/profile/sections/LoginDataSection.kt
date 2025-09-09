package com.example.gymeaze.ui.screens.profile.sections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
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
    val currentUser by viewModel?.currentUser?.collectAsState() ?: remember { mutableStateOf(null) }
    val newUsername by viewModel?.newUsername?.collectAsState() ?: remember { mutableStateOf("") }
    val newPassword by viewModel?.newPassword?.collectAsState() ?: remember { mutableStateOf("") }
    val confirmPassword by viewModel?.confirmPassword?.collectAsState() ?: remember { mutableStateOf("") }
    val isUpdateSuccessful by viewModel?.isUpdateSuccessful?.collectAsState() ?: remember { mutableStateOf(null) }
    val error by viewModel?.error?.collectAsState() ?: remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        viewModel?.fetchCurrentUser()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isUpdateSuccessful == true) {
            SuccessMessage(message = "Login data updated successfully!")
        }

        error?.let { errorMessage ->
            ErrorMessage(message = errorMessage)
        }

        EditLoginDataSection(
            viewModel = viewModel,
            currentUsername = currentUser?.username ?: "",
            newUsername = newUsername,
            newPassword = newPassword,
            confirmPassword = confirmPassword
        )

        Spacer(modifier = Modifier.height(16.dp))

        DeleteAccountSection(viewModel = viewModel)
    }
}

@Composable
private fun SuccessMessage(message: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.Green.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, Color.Green.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = Color.Green,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                color = Color.Green,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.Red.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                tint = Color.Red,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                color = Color.Red,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun EditLoginDataSection(
    viewModel: UserViewModel? = null,
    currentUsername: String,
    newUsername: String,
    newPassword: String,
    confirmPassword: String
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

    UsernameField(
        viewModel = viewModel,
        currentUsername = currentUsername,
        newUsername = newUsername
    )

    NewPasswordField(
        viewModel = viewModel,
        newPassword = newPassword
    )

    ConfirmNewPasswordField(
        viewModel = viewModel,
        confirmPassword = confirmPassword
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = {
            viewModel?.updateLoginData()
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
    viewModel: UserViewModel? = null,
    currentUsername: String,
    newUsername: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Current username: $currentUsername",
                color = getOnSurfaceSecondaryColor(),
                fontSize = 14.sp

            )

        }

        Text(
            text = stringResource(R.string.edit_login_data),
            color = getOnSurfaceColor(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        OutlinedTextField(
            value = newUsername,
            onValueChange = {
                viewModel?.updateNewUsername(it)
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
    viewModel: UserViewModel? = null,
    newPassword: String
) {
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
                viewModel?.updateNewPassword(it)
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
                            "Hide password" else "Show password",
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
    viewModel: UserViewModel? = null,
    confirmPassword: String
) {
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
                viewModel?.updateConfirmPassword(it)
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
                            "Hide password" else "Show password",
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
    val showDeleteDialog by viewModel?.showDeleteDialog?.collectAsState() ?: remember { mutableStateOf(false) }
    val confirmUsername by viewModel?.confirmUsername?.collectAsState() ?: remember { mutableStateOf("") }
    val isDeleting by viewModel?.isDeleting?.collectAsState() ?: remember { mutableStateOf(false) }
    val deleteError by viewModel?.deleteError?.collectAsState() ?: remember { mutableStateOf(null) }
    val currentUser by viewModel?.currentUser?.collectAsState() ?: remember { mutableStateOf(null) }

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
                    viewModel?.showDeleteDialog()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB71C1C)
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !isDeleting
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (isDeleting) "Usuwanie..." else stringResource(R.string.delete_account),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }

    // Dialog potwierdzenia usunięcia konta
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) {
                    viewModel?.hideDeleteDialog()
                }
            },
            title = {
                Text(
                    text = "Usuń konto",
                    color = Color(0xFFB71C1C),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Ta akcja jest nieodwracalna. Wszystkie Twoje dane zostaną trwale usunięte.",
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Text(
                        text = "Aby potwierdzić, wpisz swoją nazwę użytkownika: ${currentUser?.username}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    OutlinedTextField(
                        value = confirmUsername,
                        onValueChange = { viewModel?.updateConfirmUsername(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nazwa użytkownika") },
                        enabled = !isDeleting,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFB71C1C),
                            unfocusedBorderColor = Color.Gray
                        ),
                        singleLine = true
                    )

                    // Pokazuj błąd jeśli istnieje
                    deleteError?.let { error ->
                        Text(
                            text = error,
                            color = Color(0xFFB71C1C),
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel?.deleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB71C1C)
                    ),
                    enabled = !isDeleting && confirmUsername.isNotEmpty()
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (isDeleting) "Usuwanie..." else "Usuń konto",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel?.hideDeleteDialog()
                    },
                    enabled = !isDeleting
                ) {
                    Text("Anuluj")
                }
            }
        )
    }
}