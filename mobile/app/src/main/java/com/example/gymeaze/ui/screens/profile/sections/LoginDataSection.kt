package com.example.gymeaze.ui.screens.profile.sections

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.gymeaze.R
import com.example.gymeaze.ui.components.LoadingScreen
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.UserViewModel

@Composable
fun LoginDataSection(
    viewModel: UserViewModel? = null,
    onNavigateToAuth: () -> Unit
) {
    val navigateToAuth by viewModel?.navigateToAuth?.collectAsState() ?: remember { mutableStateOf(false) }
    val success by viewModel?.loginDataSuccess?.collectAsState() ?: remember { mutableStateOf(null) }
    val isLoading by viewModel?.loginDataLoading?.collectAsState(false) ?: remember { mutableStateOf(false) }

    val context = LocalContext.current

    if (isLoading) {
        LoadingScreen()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            EditLoginDataSection(viewModel = viewModel,  isLoading = isLoading)
            DeleteAccountSection(viewModel = viewModel)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel?.resetUserState()
        }
    }

    LaunchedEffect(navigateToAuth) {
        if (navigateToAuth) {
            viewModel?.resetNavigation()
            onNavigateToAuth()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel?.fetchCurrentUser()
        viewModel?.resetLoginDataError()
    }

    LaunchedEffect(success) {
        success?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel?.resetLoginDataSuccess()
        }
    }
}

@Composable
private fun EditLoginDataSection(
    viewModel: UserViewModel? = null,
    isLoading: Boolean = false
) {
    val newUsername by viewModel?.newUsername?.collectAsState("") ?: remember { mutableStateOf("") }
    val newPassword by viewModel?.newPassword?.collectAsState("") ?: remember { mutableStateOf("") }
    val confirmPassword by viewModel?.confirmPassword?.collectAsState("") ?: remember { mutableStateOf("") }
    val error by viewModel?.loginDataError?.collectAsState() ?: remember { mutableStateOf(null) }
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
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

        error?.let { errorMessage ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = getErrorColor().copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, getErrorColor().copy(alpha = 0.3f))
            ) {
                Text(
                    text = errorMessage,
                    color = getErrorColor(),
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp
                )
            }
        }

        UsernameField(
            viewModel = viewModel,
            enabled = !isLoading
        )

        NewPasswordField(
            viewModel = viewModel,
            newPassword = newPassword,
            enabled = !isLoading
        )

        ConfirmNewPasswordField(
            viewModel = viewModel,
            confirmPassword = confirmPassword,
            enabled = !isLoading
        )

        Button(
            onClick = {
                viewModel?.updateLoginData()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = getPrimaryColor(),
                contentColor = getOnPrimaryColor()
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = getOnPrimaryColor(),
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = stringResource(R.string.saving),
                        color = getOnPrimaryColor(),
                        fontSize = 16.sp
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.save_changes),
                    color = getOnPrimaryColor(),
                    fontSize = 16.sp
                )
            }
        }
    }
}
@Composable
private fun UsernameField(
    viewModel: UserViewModel? = null,
    enabled: Boolean = true
) {
    val currentUsername by viewModel?.currentUsername?.collectAsState("") ?: remember { mutableStateOf("") }
    val newUsername by viewModel?.newUsername?.collectAsState("") ?: remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.username),
            color = getOnSurfaceColor(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        OutlinedTextField(
            value = newUsername,
            onValueChange = {
                viewModel?.updateNewUsername(it)
            },
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
            singleLine = true,
            placeholder = {
                Text(
                    text = if (newUsername.isEmpty()) currentUsername else stringResource(R.string.enter_new_username),
                    color = getOnSurfaceSecondaryColor()
                )
            }
        )
    }
}

@Composable
private fun NewPasswordField(
    viewModel: UserViewModel? = null,
    newPassword: String,
    enabled: Boolean = true
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

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
            onValueChange = { viewModel?.updateNewPassword(it) },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPasswordVisible)
                VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                if (enabled) {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible)
                                Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (isPasswordVisible)
                                stringResource(R.string.hide_password) else stringResource(R.string.show_password),
                            tint = getOnSurfaceSecondaryColor()
                        )
                    }
                }
            },
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
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            placeholder = {
                Text(
                    text = stringResource(R.string.enter_new_password),
                    color = getOnSurfaceSecondaryColor()
                )
            }
        )
    }
}

@Composable
private fun ConfirmNewPasswordField(
    viewModel: UserViewModel? = null,
    confirmPassword: String,
    enabled: Boolean = true
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

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
            onValueChange = { viewModel?.updateConfirmPassword(it) },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPasswordVisible)
                VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                if (enabled) {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible)
                                Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (isPasswordVisible)
                                stringResource(R.string.hide_password) else stringResource(R.string.show_password),
                            tint = getOnSurfaceSecondaryColor()
                        )
                    }
                }
            },
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
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            placeholder = {
                Text(
                    text = stringResource(R.string.confirm_password_placeholder),
                    color = getOnSurfaceSecondaryColor()
                )
            }
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
    val currentUsername by viewModel?.currentUsername?.collectAsState("") ?: remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = getErrorColor().copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, getErrorColor().copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = getErrorColor(),
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = stringResource(R.string.delete_account),
                    color = getErrorColor(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

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
                    containerColor = getErrorColor(),
                    contentColor = getOnPrimaryColor()
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !isDeleting
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = getOnPrimaryColor(),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (isDeleting) stringResource(R.string.deleting) else stringResource(R.string.delete_account),
                    color = getOnPrimaryColor(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) {
                    viewModel?.hideDeleteDialog()
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = getErrorColor(),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.delete_account_dialog_title),
                    color = getOnSurfaceColor(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = getErrorColor().copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, getErrorColor().copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.warning_title),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = getErrorColor()
                            )
                            Text(
                                text = stringResource(R.string.delete_account_warning_message),
                                fontSize = 14.sp,
                                lineHeight = 18.sp,
                                color = getOnSurfaceColor()
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.confirm_username_prompt),
                            fontSize = 14.sp,
                            color = getOnSurfaceColor()
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = getSurfaceColor(),
                            border = BorderStroke(1.dp, getPrimaryColor())
                        ) {
                            Text(
                                text = currentUsername,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = getPrimaryColor()
                            )
                        }

                        OutlinedTextField(
                            value = confirmUsername,
                            onValueChange = { viewModel?.updateConfirmUsername(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    stringResource(R.string.enter_username_placeholder),
                                    color = getOnSurfaceSecondaryColor()
                                )
                            },
                            enabled = !isDeleting,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = getErrorColor(),
                                unfocusedBorderColor = getInputBorderColor(),
                                focusedTextColor = getOnSurfaceColor(),
                                unfocusedTextColor = getOnSurfaceColor(),
                                cursorColor = getErrorColor()
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    deleteError?.let { error ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = getErrorColor().copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = error,
                                color = getErrorColor(),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel?.deleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getErrorColor(),
                        contentColor = getOnPrimaryColor()
                    ),
                    enabled = !isDeleting && confirmUsername.isNotEmpty() &&
                            confirmUsername == currentUsername,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = getOnPrimaryColor(),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (isDeleting) stringResource(R.string.deleting) else stringResource(R.string.delete_account_confirm),
                        color = getOnPrimaryColor(),
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        viewModel?.hideDeleteDialog()
                    },
                    enabled = !isDeleting,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = getOnSurfaceColor()
                    ),
                    border = BorderStroke(1.dp, getInputBorderColor()),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
            containerColor = getCardBackgroundColor(),
            shape = RoundedCornerShape(16.dp),
            properties = DialogProperties(
                dismissOnBackPress = !isDeleting,
                dismissOnClickOutside = !isDeleting
            )
        )
    }
}