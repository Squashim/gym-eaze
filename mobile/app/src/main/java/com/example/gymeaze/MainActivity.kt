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
import com.example.gymeaze.ui.screens.profile.edit.ProfileEditScreen
import com.example.gymeaze.ui.screens.profile.settings.SettingsScreen
import com.example.gymeaze.ui.screens.profile.settings.ThemeSelectionScreen
import com.example.gymeaze.ui.theme.GymEazeTheme
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.viewmodel.AuthViewModel
import com.example.gymeaze.viewmodel.ExerciseCreatorViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.gymeaze.ui.components.WorkoutLoginRequiredDialog
import com.example.gymeaze.ui.screens.CreateTrainingPlanScreen
import com.example.gymeaze.ui.screens.EditTrainingPlanScreen
import com.example.gymeaze.ui.screens.ExerciseSelectionScreen
import com.example.gymeaze.ui.screens.FavoriteTrainingPlansScreen
import com.example.gymeaze.ui.screens.MyTrainingPlansScreen
import com.example.gymeaze.ui.screens.TrainingPlanDetailScreen
import com.example.gymeaze.ui.screens.TrainingPlansScreen
import com.example.gymeaze.ui.screens.posts.PostScreen
import com.example.gymeaze.ui.screens.posts.loggedIn.SeeAllPostsScreen
import com.example.gymeaze.ui.screens.posts.loggedIn.CreatePostScreen
import com.example.gymeaze.ui.screens.posts.loggedIn.MyPosts
import com.example.gymeaze.ui.screens.posts.loggedIn.PostsFollowedUsers
import com.example.gymeaze.ui.screens.profile.settings.HelpScreen
import com.example.gymeaze.ui.screens.profile.settings.NotificationsScreen
import com.example.gymeaze.ui.screens.profile.settings.PrivacySettingsScreen
import com.example.gymeaze.viewmodel.CreateTrainingPlanViewModel
import com.example.gymeaze.viewmodel.EditTrainingPlanViewModel
import com.example.gymeaze.viewmodel.LogoutEvent
import com.example.gymeaze.viewmodel.posts.PostsViewModel
import com.example.gymeaze.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authViewModel: AuthViewModel = viewModel()
            val exerciseCreatorViewModel: ExerciseCreatorViewModel = viewModel()
            val createTrainingPlanViewModel: CreateTrainingPlanViewModel = viewModel()
            val createPostViewModel: PostsViewModel = viewModel()


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

            val showCreateTrainingPlan by authViewModel.showCreateTrainingPlan.collectAsState()
            val showTrainingPlansMenu by authViewModel.showTrainingPlansMenu.collectAsState()
            val showExerciseSelectionForDay by authViewModel.showExerciseSelectionForDay.collectAsState()
            val selectedDayIndex by authViewModel.selectedDayIndex.collectAsState()

            val showBrowseTrainingPlans by authViewModel.showBrowseTrainingPlans.collectAsState()
            val showMyTrainingPlans by authViewModel.showMyTrainingPlans.collectAsState()
            val showFavoriteTrainingPlans by authViewModel.showFavoriteTrainingPlans.collectAsState()
            val showTrainingPlanDetail by authViewModel.showTrainingPlanDetail.collectAsState()
            val selectedTrainingPlanId by authViewModel.selectedTrainingPlanId.collectAsState()
            val showEditTrainingPlan by authViewModel.showEditTrainingPlan.collectAsState()
            val selectedEditPlanId by authViewModel.selectedEditPlanId.collectAsState()

            val showPrivacySettings by userViewModel.showPrivacySettings.collectAsState()
            val showHelpScreen by userViewModel.showHelpScreen.collectAsState()
            val showNotificationsScreen by userViewModel.showNotificationsScreen.collectAsState()

            val showPostScreen by authViewModel.showPostScreen.collectAsState()
            val showCreatePost by authViewModel.showCreatePost.collectAsState()
            val showBrowsePost by authViewModel.showBrowsePost.collectAsState()
            val showFollowedUsersPosts by authViewModel.showFollowedUserPosts.collectAsState()
            val showMyPosts by authViewModel.showMyPosts.collectAsState()




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
                    createPostViewModel.clearPosts()
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
                            showNotificationsScreen -> {
                                NotificationsScreen(
                                    onReturnClick = userViewModel::hideNotificationsScreen,
                                )
                            }
                            showPrivacySettings -> {
                                PrivacySettingsScreen(
                                    onReturnClick = userViewModel::hidePrivacySettingsScreen,
                                    viewModel = userViewModel
                                )
                            }
                            showHelpScreen -> {
                                HelpScreen(
                                    onReturnClick = userViewModel::hideHelpScreen,
                                )
                            }
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
                                    onPrivacySettingsClick = if (isUserLoggedIn) { { userViewModel.showPrivacySettingsScreen() } } else null,
                                    onHelpClick = if (isUserLoggedIn) { { userViewModel.showHelpScreen() } } else null,
                                    onNotificationsClick = if (isUserLoggedIn) { { userViewModel.showNotificationsScreen() } } else null,
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

                            showExerciseSelectionForDay && selectedDayIndex != null -> {
                                val isEditMode = authViewModel.isEditPlanExerciseSelectionActive()

                                ExerciseSelectionScreen(
                                    dayIndex = selectedDayIndex!!,
                                    onBackClick = {
                                        if (isEditMode) {
                                            authViewModel.clearEditPlanExerciseSelectionCallback()
                                        } else {
                                            authViewModel.clearExerciseSelectionCallback()
                                        }
                                        authViewModel.hideExerciseSelectionForDayScreen()
                                    },
                                    onExerciseSelected = { exercise ->
                                        if (isEditMode) {
                                            authViewModel.onExerciseSelectedForEditPlan(exercise)
                                        } else {
                                            authViewModel.onExerciseSelectedForDay(exercise)
                                            authViewModel.hideExerciseSelectionForDayScreen()
                                        }
                                    }
                                )
                            }

                            showCreateTrainingPlan -> {
                                CreateTrainingPlanScreen(
                                    onBackClick = authViewModel::hideCreateTrainingPlanScreen,
                                    onNavigateToExerciseSelection = { dayIndex ->

                                        authViewModel.setExerciseSelectionCallback { exercise ->
                                            createTrainingPlanViewModel.addExerciseToDay(dayIndex, exercise)
                                        }
                                        authViewModel.showExerciseSelectionForDayScreen(dayIndex)
                                    },
                                    viewModel = createTrainingPlanViewModel
                                )
                            }

                            showTrainingPlanDetail && selectedTrainingPlanId != null -> {
                                TrainingPlanDetailScreen(
                                    planId = selectedTrainingPlanId!!,
                                    onBackClick = authViewModel::hideTrainingPlanDetailScreen,
                                    onExerciseClick = { exerciseId ->
                                        authViewModel.showExerciseDetailScreen(exerciseId)
                                    }
                                )
                            }

                            showFavoriteTrainingPlans -> {
                                FavoriteTrainingPlansScreen(
                                    onBackClick = authViewModel::hideFavoriteTrainingPlansScreen,
                                    onPlanClick = { planId ->
                                        authViewModel.showTrainingPlanDetailScreen(planId)
                                    },
                                    onSettingsClick = authViewModel::showSettingsScreen
                                )
                            }

                            showEditTrainingPlan && selectedEditPlanId != null -> {
                                val editViewModel: EditTrainingPlanViewModel = viewModel()

                                EditTrainingPlanScreen(
                                    planId = selectedEditPlanId!!,
                                    onBackClick = authViewModel::hideEditTrainingPlanScreen,
                                    onSaveSuccess = authViewModel::hideEditTrainingPlanScreen,
                                    onNavigateToExerciseSelection = { dayIndex ->
                                        authViewModel.setEditPlanExerciseSelectionCallback { exercise ->
                                            editViewModel.addExerciseToDay(dayIndex, exercise)
                                        }
                                        authViewModel.showExerciseSelectionForDayScreen(dayIndex)
                                    },
                                    viewModel = editViewModel
                                )
                            }

                            showMyTrainingPlans -> {
                                MyTrainingPlansScreen(
                                    onBackClick = authViewModel::hideMyTrainingPlansScreen,
                                    onPlanClick = { planId ->
                                        authViewModel.showTrainingPlanDetailScreen(planId)
                                    },
                                    onEditPlanClick = { planId ->
                                        authViewModel.showEditTrainingPlanScreen(planId)
                                    },
                                    onCreatePlanClick = authViewModel::showCreateTrainingPlanScreen,
                                    onSettingsClick = authViewModel::showSettingsScreen
                                )
                            }

                            showBrowseTrainingPlans -> {
                                TrainingPlansScreen(
                                    onBackClick = authViewModel::hideBrowseTrainingPlansScreen,
                                    onPlanClick = { planId ->
                                        authViewModel.showTrainingPlanDetailScreen(planId)
                                    },
                                    onSettingsClick = authViewModel::showSettingsScreen
                                )
                            }

                            showMyPosts-> {
                                MyPosts(
                                    onBackClick = { authViewModel.hideMyPostsScreen() }
                                )
                            }
                            showFollowedUsersPosts -> {
                                PostsFollowedUsers(
                                    onBackClick = { authViewModel.hideFollowedUserPostsScreen() }
                                )
                            }
                            showBrowsePost -> {
                                SeeAllPostsScreen(
                                    onBackClick = { authViewModel.hideBrowsePostScreen() }
                                )
                            }

                            showCreatePost -> {
                                CreatePostScreen(
                                    viewModel = createPostViewModel,
                                    onBackClick = {

                                        authViewModel.hideCreatePostScreen()
                                    },
                                    onCreated = {

                                        authViewModel.hideCreatePostScreen()

                                    }
                                )
                            }

                            showPostScreen -> {
                                PostScreen(
                                    onSettingsClick = authViewModel::showSettingsScreen,
                                    isUserLoggedIn = isUserLoggedIn,
                                    createPostViewModel = createPostViewModel,
                                    setShowBottomBar = {  }

                                )
                            }

                            !isUserLoggedIn -> {
                                AuthScreen(
                                    isUserLoggedIn = isUserLoggedIn,
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
                                    onCreateTrainingPlanClick = authViewModel::showCreateTrainingPlanScreen,
                                    onCreatePostClick = authViewModel::showCreatePostScreen,
                                    onBrowsePostClick = authViewModel::showBrowsePostScreen,
                                    onFollowedUsersPosts = authViewModel :: showFollowedUserPostsScreen,
                                    onMyPosts = authViewModel :: showMyPostsScreen

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
                                    isUserLoggedIn = isUserLoggedIn,
                                    username = username,
                                    onLogout = { authViewModel.logout() },
                                    onExerciseCreatorClick = authViewModel::showExerciseCreatorScreen,
                                    onSettingsClick = authViewModel::showSettingsScreen,
                                    onEditProfileClick = { authViewModel.showProfileEditScreen() },
                                    onBrowseExercisesClick = authViewModel::showBrowseExercisesScreen,
                                    onCreateTrainingPlanClick = authViewModel::showCreateTrainingPlanScreen,
                                    onCreatePostClick = authViewModel::showCreatePostScreen,
                                    onBrowsePostClick = authViewModel::showBrowsePostScreen,
                                    onFollowedUsersPosts = authViewModel :: showFollowedUserPostsScreen,
                                    onMyPosts = authViewModel :: showMyPostsScreen,
                                    onMyTrainingPlansClick = authViewModel::showMyTrainingPlansScreen,
                                    onBrowseTrainingPlansClick = authViewModel::showBrowseTrainingPlansScreen,
                                    onFavoriteTrainingPlansClick = authViewModel::showFavoriteTrainingPlansScreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
