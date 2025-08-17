package com.example.gymeaze.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.R
import com.example.gymeaze.ui.components.GymEazeBottomNavigationBar
import com.example.gymeaze.ui.components.GymEazeTopAppBar
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.AuthUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    uiState: AuthUiState,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onUsernameOrEmailChange: (String) -> Unit,
    onToggleAuthMode: () -> Unit,
    onRegister: () -> Unit,
    onLogin: () -> Unit,
    onGoogleLogin: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            GymEazeTopAppBar(onSettingsClick = onSettingsClick)
        },
        bottomBar = {
            GymEazeBottomNavigationBar(
                selectedRoute = "auth",
                onItemSelected = { /* Logika nawigacji */ }
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        // Tło z gradientem
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Start,
                            Middle,
                            End
                        ),
                        radius = 1200f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.join_us),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceDark,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.your_training_community_progress),
                            fontSize = 14.sp,
                            color = OnSurfaceSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { if (!uiState.isRegistering) onToggleAuthMode() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (uiState.isRegistering) PrimaryGreen else ButtonOutlined
                                ),
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.register),
                                    color = OnSurfaceDark
                                )
                            }

                            OutlinedButton(
                                onClick = { if (uiState.isRegistering) onToggleAuthMode() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (!uiState.isRegistering) PrimaryGreen else Color.Transparent
                                ),
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.login),
                                    color = OnSurfaceDark
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        if (uiState.isRegistering) {
                            RegisterForm(
                                username = uiState.username,
                                email = uiState.email,
                                password = uiState.password,
                                confirmPassword = uiState.confirmPassword,
                                onUsernameChange = onUsernameChange,
                                onEmailChange = onEmailChange,
                                onPasswordChange = onPasswordChange,
                                onConfirmPasswordChange = onConfirmPasswordChange,
                                onRegister = onRegister,
                                isLoading = uiState.isLoading
                            )
                        } else {
                            LoginForm(
                                usernameOrEmail = uiState.usernameOrEmail,
                                password = uiState.password,
                                onUsernameOrEmailChange = onUsernameOrEmailChange,
                                onPasswordChange = onPasswordChange,
                                onLogin = onLogin,
                                isLoading = uiState.isLoading
                            )
                        }

                        uiState.errorMessage?.let { error ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = stringResource(R.string.or),
                            color = OnSurfaceSecondary,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onGoogleLogin,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoogleButton
                            ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !uiState.isLoading
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_google_logo),
                                    contentDescription = stringResource(R.string.google_logo),
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Unspecified
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = stringResource(R.string.continue_with_google),
                                    color = GoogleButtonText,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
@Composable
private fun RegisterForm(
    username: String,
    email: String,
    password: String,
    confirmPassword: String,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegister: () -> Unit,
    isLoading: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.username),
            color = OnSurfaceDark,
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.username),
                    color = OnSurfaceSecondary
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = OnSurfaceSecondary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = InputBorder,
                focusedTextColor = OnSurfaceDark,
                unfocusedTextColor = OnSurfaceDark,
                cursorColor = PrimaryGreen
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Text(
            text = stringResource(R.string.email),
            color = OnSurfaceDark,
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = {
                Text(
                    text = "Email",
                    color = OnSurfaceSecondary
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = OnSurfaceSecondary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = InputBorder,
                focusedTextColor = OnSurfaceDark,
                unfocusedTextColor = OnSurfaceDark,
                cursorColor = PrimaryGreen
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Text(
            text = stringResource(R.string.password),
            color = OnSurfaceDark,
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.password_placeholder),
                    color = OnSurfaceSecondary
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = OnSurfaceSecondary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = InputBorder,
                focusedTextColor = OnSurfaceDark,
                unfocusedTextColor = OnSurfaceDark,
                cursorColor = PrimaryGreen
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Text(
            text = stringResource(R.string.confirm_password),
            color = OnSurfaceDark,
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.password_placeholder),
                    color = OnSurfaceSecondary
                )
            },
            leadingIcon ={
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = OnSurfaceSecondary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = InputBorder,
                focusedTextColor = OnSurfaceDark,
                unfocusedTextColor = OnSurfaceDark,
                cursorColor = PrimaryGreen
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onRegister,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = !isLoading
        ) {
            if (isLoading){
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = OnSurfaceDark
                )
            } else {
                Text(
                    text = stringResource(R.string.register),
                    color = OnSurfaceDark,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun LoginForm(
    usernameOrEmail: String,
    password: String,
    onUsernameOrEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    isLoading: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.username_or_email),
            color = OnSurfaceDark,
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = usernameOrEmail,
            onValueChange = onUsernameOrEmailChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.username),
                    color = OnSurfaceSecondary
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = OnSurfaceSecondary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = InputBorder,
                focusedTextColor = OnSurfaceDark,
                unfocusedTextColor = OnSurfaceDark,
                cursorColor = PrimaryGreen
            ),
            shape = RoundedCornerShape(8.dp)
        )


        Text(
            text = stringResource(R.string.password),
            color = OnSurfaceDark,
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.password_placeholder),
                    color = OnSurfaceSecondary
                )
            },
            leadingIcon ={
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = OnSurfaceSecondary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = InputBorder,
                focusedTextColor = OnSurfaceDark,
                unfocusedTextColor = OnSurfaceDark,
                cursorColor = PrimaryGreen
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))


        Button(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = OnSurfaceDark
                )
            } else {
                Text(
                    text = stringResource(R.string.login),
                    color = OnSurfaceDark,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    GymEazeTheme {
        AuthScreen(
            uiState = AuthUiState(
                username = "Test",
                email = "Test@example.com",
                password = "",
                confirmPassword = "",
                usernameOrEmail = "",
                isRegistering = true,
                isLoading = false,
                errorMessage = null
            ),
            onUsernameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onUsernameOrEmailChange = {},
            onToggleAuthMode = {},
            onRegister = {},
            onLogin = {},
            onGoogleLogin = {},
            onSettingsClick = {}
        )
    }
}

