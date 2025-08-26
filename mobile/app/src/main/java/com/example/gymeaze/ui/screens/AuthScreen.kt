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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
        containerColor = getBackgroundColor()
    ) { paddingValues ->
        // Tło z gradientem
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            getGradientStartColor(),
                            getGradientMiddleColor(),
                            getGradientEndColor()
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
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.join_us),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = getOnSurfaceColor(),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.your_training_community_progress),
                            fontSize = 14.sp,
                            color = getOnSurfaceSecondaryColor(),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { if (!uiState.isRegistering) onToggleAuthMode() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (uiState.isRegistering) getPrimaryColor() else getButtonOutlinedColor()
                                ),
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.register),
                                    color = if (uiState.isRegistering) getOnPrimaryColor() else getOnSurfaceColor()
                                )
                            }

                            OutlinedButton(
                                onClick = { if (uiState.isRegistering) onToggleAuthMode() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (!uiState.isRegistering) getPrimaryColor() else Color.Transparent
                                ),
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.login),
                                    color = if (!uiState.isRegistering) getOnPrimaryColor() else getOnSurfaceColor()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

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
                                isLoading = uiState.isLoading,
                                usernameError = uiState.usernameError,
                                emailError = uiState.emailError,
                                passwordError = uiState.passwordError,
                                confirmPasswordError = uiState.confirmPasswordError,
                                isRegisterValid = uiState.isRegisterValid
                            )
                        } else {
                            LoginForm(
                                usernameOrEmail = uiState.usernameOrEmail,
                                password = uiState.password,
                                onUsernameOrEmailChange = onUsernameOrEmailChange,
                                onPasswordChange = onPasswordChange,
                                onLogin = onLogin,
                                isLoading = uiState.isLoading,
                                usernameOrEmailError = uiState.usernameOrEmailError,
                                isLoginValid = uiState.isLoginValid
                            )
                        }

                        uiState.errorMessage?.let { error ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = error,
                                color = getErrorColor(),
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.or),
                            color = getOnSurfaceSecondaryColor(),
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onGoogleLogin,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getGoogleButtonColor()
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
                                    color = getGoogleButtonTextColor(),
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
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
    isLoading: Boolean,
    usernameError: String?,
    emailError: String?,
    passwordError: String?,
    confirmPasswordError: String?,
    isRegisterValid: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy((-1.5).dp)
    ) {
        Text(
            text = stringResource(R.string.username),
            color = getOnSurfaceColor(),
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.username),
                    color = getOnSurfaceSecondaryColor()
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = getOnSurfaceSecondaryColor()
                )
            },
            isError = usernameError != null,
            supportingText = {
                if(usernameError != null){
                    Text(
                        text = usernameError,
                        color = getErrorColor(),
                        fontSize = 12.sp
                        )
                }
            },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = getPrimaryColor(),
                unfocusedBorderColor = getInputBorderColor(),
                focusedTextColor = getOnSurfaceColor(),
                unfocusedTextColor = getOnSurfaceColor(),
                cursorColor = getPrimaryColor(),
                errorBorderColor = getErrorColor(),
                errorLeadingIconColor = getErrorColor()
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Text(
            text = stringResource(R.string.email),
            color = getOnSurfaceColor(),
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = {
                Text(
                    text = "Email",
                    color = getOnSurfaceSecondaryColor()
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = getOnSurfaceSecondaryColor()
                )
            },
            isError = emailError != null,
            supportingText = {
                if (emailError != null){
                    Text(text = emailError,
                        color = getErrorColor(),
                        fontSize = 12.sp
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clearAndSetSemantics { contentDescription = "Email input field" },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = getPrimaryColor(),
                unfocusedBorderColor = getInputBorderColor(),
                focusedTextColor = getOnSurfaceColor(),
                unfocusedTextColor = getOnSurfaceColor(),
                cursorColor = getPrimaryColor(),
                errorBorderColor = getErrorColor(),
                errorLeadingIconColor = getErrorColor()
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Text(
            text = stringResource(R.string.password),
            color = getOnSurfaceColor(),
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.password_placeholder),
                    color = getOnSurfaceSecondaryColor()
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = getOnSurfaceSecondaryColor()
                )
            },
            isError = passwordError != null,
            supportingText = {
                if(passwordError != null){
                    Text(
                        text = passwordError,
                        color = getErrorColor(),
                        fontSize = 12.sp
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = getPrimaryColor(),
                unfocusedBorderColor = getInputBorderColor(),
                focusedTextColor = getOnSurfaceColor(),
                unfocusedTextColor = getOnSurfaceColor(),
                cursorColor = getPrimaryColor(),
                errorBorderColor = getErrorColor(),
                errorLeadingIconColor = getErrorColor()
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Text(
            text = stringResource(R.string.confirm_password),
            color = getOnSurfaceColor(),
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.password_placeholder),
                    color = getOnSurfaceSecondaryColor()
                )
            },
            leadingIcon ={
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = getOnSurfaceSecondaryColor()
                )
            },
            isError = confirmPasswordError != null,
            supportingText = {
                if(confirmPasswordError != null){
                    Text(
                        text = confirmPasswordError,
                        color = getErrorColor(),
                        fontSize = 12.sp
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = getPrimaryColor(),
                unfocusedBorderColor = getInputBorderColor(),
                focusedTextColor = getOnSurfaceColor(),
                unfocusedTextColor = getOnSurfaceColor(),
                cursorColor = getPrimaryColor(),
                errorBorderColor = getErrorColor(),
                errorLeadingIconColor = getErrorColor()
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onRegister,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = getPrimaryColor(),
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = !isLoading && isRegisterValid,
        ) {
            if (isLoading){
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = getOnPrimaryColor()
                )
            } else {
                Text(
                    text = stringResource(R.string.register),
                    color = getOnPrimaryColor(),
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
    isLoading: Boolean,
    usernameOrEmailError: String?,
    isLoginValid: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy((-1.5).dp)
    ) {
        Text(
            text = stringResource(R.string.username_or_email),
            color = getOnSurfaceColor(),
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = usernameOrEmail,
            onValueChange = onUsernameOrEmailChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.username),
                    color = getOnSurfaceSecondaryColor()
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = getOnSurfaceSecondaryColor()
                )
            },
            isError = usernameOrEmailError != null,
            supportingText = {
                if(usernameOrEmailError != null){
                    Text(
                        text = usernameOrEmailError,
                        color = getErrorColor(),
                        fontSize = 12.sp)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clearAndSetSemantics { contentDescription = "Username input field" },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = getPrimaryColor(),
                unfocusedBorderColor = getInputBorderColor(),
                focusedTextColor = getOnSurfaceColor(),
                unfocusedTextColor = getOnSurfaceColor(),
                cursorColor = getPrimaryColor(),
                errorBorderColor = getErrorColor(),
                errorLeadingIconColor = getErrorColor()
            ),
            shape = RoundedCornerShape(8.dp)
        )


        Text(
            text = stringResource(R.string.password),
            color = getOnSurfaceColor(),
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.password_placeholder),
                    color = getOnSurfaceSecondaryColor()
                )
            },
            leadingIcon ={
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = getOnSurfaceSecondaryColor()
                )
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = getPrimaryColor(),
                unfocusedBorderColor = getInputBorderColor(),
                focusedTextColor = getOnSurfaceColor(),
                unfocusedTextColor = getOnSurfaceColor(),
                cursorColor = getPrimaryColor()
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = getPrimaryColor()
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = !isLoading && isLoginValid
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = getOnPrimaryColor()
                )
            } else {
                Text(
                    text = stringResource(R.string.login),
                    color = getOnPrimaryColor(),
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

