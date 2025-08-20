package com.example.gymeaze

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.ui.screens.AuthScreen
import com.example.gymeaze.ui.screens.IntroductionScreen
import com.example.gymeaze.ui.screens.SettingsScreen
import com.example.gymeaze.ui.screens.ThemeSelectionScreen
import com.example.gymeaze.ui.theme.GymEazeTheme
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authViewModel: AuthViewModel = viewModel()
            val uiState by authViewModel.uiState.collectAsState()
            val isUserLoggedIn by authViewModel.isLoggedIn.collectAsState(initial = false)
            val isFirstLogin by authViewModel.isFirstLogin.collectAsState(initial = true)
            val isDarkTheme by authViewModel.isDarkTheme.collectAsState(initial = true)
            val showSettings by authViewModel.showSettings.collectAsState()
            val showThemeSelection by authViewModel.showThemeSelection.collectAsState()
            val username by authViewModel.username.collectAsState(initial = "")

            GymEazeTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = getBackgroundColor()
                ) {
                    when {
                        // Ekran wyboru motywu
                        showThemeSelection -> {
                            ThemeSelectionScreen(
                                isDarkTheme = isDarkTheme,
                                onReturnClick = authViewModel::hideThemeSelectionScreen,
                                onThemeSelected = authViewModel::selectTheme
                            )
                        }

                        // Ekran ustawień
                        showSettings -> {
                            SettingsScreen(
                                onReturnClick = authViewModel::hideSettingsScreen,
                                onThemeClick = authViewModel::showThemeSelectionScreen
                            )
                        }

                        // Użytkownik nie jest zalogowany - pokaż ekran autoryzacji
                        !isUserLoggedIn -> {
                            AuthScreen(
                                uiState = uiState,
                                onUsernameChange = authViewModel::updateUsername,
                                onEmailChange = authViewModel::updateEmail,
                                onPasswordChange = authViewModel::updatePassword,
                                onConfirmPasswordChange = authViewModel::updateConfirmPassword,
                                onUsernameOrEmailChange = authViewModel::updateUsernameOrEmail,
                                onToggleAuthMode = authViewModel::toggleAuthMode,
                                onRegister = authViewModel::register,
                                onLogin = authViewModel::login,
                                onSettingsClick = authViewModel::showSettingsScreen,
                                onGoogleLogin = authViewModel::loginWithGoogle,
                            )
                        }

                        // Użytkownik jest zalogowany i to jego pierwszy login - pokaż wprowadzenie
                        isUserLoggedIn && isFirstLogin -> {
                            IntroductionScreen(
                                username = username,
                                onCompleteIntroduction = authViewModel::clearFirstLogin,
                                onSkipIntroduction = authViewModel::clearFirstLogin,
                                onSettingsClick = authViewModel::showSettingsScreen
                            )
                        }

                        // Użytkownik jest zalogowany i nie jest to pierwszy login - główna aplikacja
                        else -> {
                            // TODO: Implementacja głównych ekranów aplikacji
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = getBackgroundColor()
                            ) {
                                Column {
                                    Text(
                                        text = "Główna aplikacja - TODO: Implementacja",
                                        color = getBackgroundColor()
                                    )
                                    Button(
                                        onClick = authViewModel::logout
                                    ) {
                                        Text("Wyloguj")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}