package com.example.gymeaze

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.ui.components.AccountLinkingDialog
import com.example.gymeaze.ui.components.LoadingScreen
import com.example.gymeaze.ui.screens.AuthScreen
import com.example.gymeaze.ui.screens.ExerciseCreatorScreen
import com.example.gymeaze.ui.screens.ExerciseDetailScreen
import com.example.gymeaze.ui.screens.ExercisesScreen
import com.example.gymeaze.ui.screens.HomeScreen
import com.example.gymeaze.ui.screens.IntroductionScreen
import com.example.gymeaze.ui.screens.profile.ProfileEditScreen
import com.example.gymeaze.ui.screens.profile.SettingsScreen
import com.example.gymeaze.ui.screens.profile.ThemeSelectionScreen
import com.example.gymeaze.ui.theme.GymEazeTheme
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.viewmodel.AuthViewModel
import com.example.gymeaze.viewmodel.ExerciseCreatorViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.gymeaze.data.repository.UserRepository
import com.example.gymeaze.viewmodel.LogoutEvent
import com.example.gymeaze.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authViewModel: AuthViewModel = viewModel()
            val exerciseCreatorViewModel: ExerciseCreatorViewModel = viewModel()
            val userViewModel: UserViewModel = viewModel {
                UserViewModel(
                    application = application,
                    onTokenExpired = { authViewModel.logout(manual = false) }
                )
            }

            val googleSignInLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                authViewModel.handleGoogleSignInResult(task)
            }

            val isCheckingToken by authViewModel.isCheckingToken.collectAsState()
            val uiState by authViewModel.uiState.collectAsState()
            val isUserLoggedIn by authViewModel.isLoggedIn.collectAsState(initial = false)
            val isFirstLogin by authViewModel.isFirstLogin.collectAsState(initial = true)
            val isDarkTheme by authViewModel.isDarkTheme.collectAsState(initial = true)
            val showSettings by authViewModel.showSettings.collectAsState()
            val showThemeSelection by authViewModel.showThemeSelection.collectAsState()
            val showProfileEdit by authViewModel.showProfileEdit.collectAsState()
            val username by authViewModel.username.collectAsState(initial = "")
            val showAccountLinking by authViewModel.showAccountLinking.collectAsState()
            val linkingEmail by authViewModel.linkingEmail.collectAsState()
            val linkingError by authViewModel.linkingError.collectAsState()
            val showExerciseCreator by authViewModel.showExerciseCreator.collectAsState()
            val showBrowseExercises by authViewModel.showBrowseExercises.collectAsState()
            val showExerciseDetail by authViewModel.showExerciseDetail.collectAsState()
            val selectedExerciseId by authViewModel.selectedExerciseId.collectAsState()
            val logoutEvent by authViewModel.logoutEvent.collectAsState()

            logoutEvent?.let { event ->
                val context = LocalContext.current
                LaunchedEffect(event) {
                    when (event) {
                        is LogoutEvent.TokenExpired -> {
                            Toast.makeText(
                                context,
                                context.getString(R.string.session_expired_login_again),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        is LogoutEvent.Manual -> {
                            Toast.makeText(context, context.getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
                        }
                        is LogoutEvent.AccountDeleted -> {
                            Toast.makeText(
                                context,
                                "Konto zostało pomyślnie usunięte",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    authViewModel.clearLogoutEvent()
                }
            }

            GymEazeTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = getBackgroundColor()
                ) {
                    if (isCheckingToken) {
                        LoadingScreen()
                    } else {
                        if (showAccountLinking) {
                            AccountLinkingDialog(
                                email = linkingEmail,
                                onDismiss = authViewModel::dismissAccountLinking,
                                onConfirm = authViewModel::confirmAccountLinking,
                                isLoading = uiState.isLoading,
                                errorMessage = linkingError
                            )
                        }

                        when {
                            showThemeSelection -> {
                                ThemeSelectionScreen(
                                    isDarkTheme = isDarkTheme,
                                    onReturnClick = authViewModel::hideThemeSelectionScreen,
                                    onThemeSelected = authViewModel::selectTheme
                                )
                            }
                            showSettings -> {
                                SettingsScreen(
                                    onReturnClick = authViewModel::hideSettingsScreen,
                                    onThemeClick = authViewModel::showThemeSelectionScreen,
                                    isUserLoggedIn = isUserLoggedIn,
                                    onLogoutClick = if (isUserLoggedIn) { { authViewModel.logout() } } else null,
                                    onPrivacySettingsClick = if (isUserLoggedIn) { { /* implementuj */ } } else null,
                                    onHelpClick = if (isUserLoggedIn) { { /* implementuj */ } } else null,
                                    onNotificationsClick = if (isUserLoggedIn) { { /* implementuj */ } } else null,
                                )
                            }
                            showProfileEdit -> {
                                ProfileEditScreen(
                                    viewModel = userViewModel,
                                    onReturnClick = authViewModel::hideProfileEditScreen,
                                    onNavigateToAuth = {
                                        authViewModel.delete()
                                        authViewModel.hideProfileEditScreen()
                                    },
                                    selectedRoute = "profile"
                                )
                            }
                            showExerciseCreator -> {
                                ExerciseCreatorScreen(
                                    viewModel = exerciseCreatorViewModel,
                                    onBackClick = authViewModel::hideExerciseCreatorScreen,
                                    onCreated = authViewModel::hideExerciseCreatorScreen
                                )
                            }
                            showExerciseDetail && selectedExerciseId != null -> {
                                ExerciseDetailScreen(
                                    exerciseId = selectedExerciseId!!,
                                    onBackClick = authViewModel::hideExerciseDetailScreen
                                )
                            }
                            showBrowseExercises -> {
                                ExercisesScreen(
                                    onSettingsClick = authViewModel::showSettingsScreen,
                                    onBackClick = authViewModel::hideBrowseExercisesScreen,
                                    onExerciseClick = { exerciseId ->
                                        authViewModel.showExerciseDetailScreen(exerciseId)
                                    }
                                )
                            }
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
                                    onGoogleLogin = {
                                        val signInIntent = authViewModel.startGoogleSignIn()
                                        googleSignInLauncher.launch(signInIntent)
                                    },
                                )
                            }
                            isUserLoggedIn && isFirstLogin -> {
                                IntroductionScreen(
                                    username = username,
                                    onCompleteIntroduction = authViewModel::showProfileEditScreen,
                                    onSkipIntroduction = authViewModel::clearFirstLogin,
                                    onSettingsClick = authViewModel::showSettingsScreen
                                )
                            }
                            else -> {
                                HomeScreen(
                                    username = username,
                                    onLogout = { authViewModel.logout() },
                                    onExerciseCreatorClick = authViewModel::showExerciseCreatorScreen,
                                    onSettingsClick = authViewModel::showSettingsScreen,
                                    onEditProfileClick = { authViewModel.showProfileEditScreen() },
                                    onBrowseExercisesClick = authViewModel::showBrowseExercisesScreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}