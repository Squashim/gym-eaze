package com.example.gymeaze.ui.screens

import UserStartedPlansSection
import android.R.attr.description
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Man
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Woman
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymeaze.data.models.Badge
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.data.models.StartedTrainingPlan
import com.example.gymeaze.ui.components.BackgroundWithDotsAndCircles
import com.example.gymeaze.ui.components.DifficultyBadgeExercise
import com.example.gymeaze.ui.components.GymEazeBottomNavigationBar
import com.example.gymeaze.ui.components.GymEazeTopAppBar
import com.example.gymeaze.ui.components.user.UserAvatar
import com.example.gymeaze.ui.screens.exercises.ExerciseScreen
import com.example.gymeaze.ui.screens.exercises.logggedIn.ExerciseDetailScreen
import com.example.gymeaze.ui.screens.posts.PostScreen
import com.example.gymeaze.ui.screens.profile.FollowListDialog
import com.example.gymeaze.ui.screens.profile.FollowStatsSection
import com.example.gymeaze.ui.screens.profile.NotificationsDialog
import com.example.gymeaze.ui.screens.profile.UserExercisesSection
import com.example.gymeaze.ui.screens.profile.UserPostsSection
import com.example.gymeaze.ui.screens.profile.UserTrainingPlansSection
import com.example.gymeaze.ui.screens.profile.components.ProfileTab
import com.example.gymeaze.ui.screens.profile.components.ProfileTabRow
import com.example.gymeaze.ui.screens.trainingsPlan.TrainingPlansNavigationScreen
import com.example.gymeaze.ui.screens.trainingsPlan.guest.WorkoutsNotLoggedInScreen
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getCardBackgroundColor
import com.example.gymeaze.ui.theme.getErrorColor
import com.example.gymeaze.ui.theme.getInputBorderColor
import com.example.gymeaze.ui.theme.getOnPrimaryColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor
import com.example.gymeaze.ui.theme.getSurfaceVariantColor
import com.example.gymeaze.viewmodel.ExerciseCreatorViewModel
import com.example.gymeaze.viewmodel.ExercisesViewModel
import com.example.gymeaze.viewmodel.NotificationViewModel
import com.example.gymeaze.viewmodel.ProfileViewModel
import com.example.gymeaze.viewmodel.UserViewModel
import com.example.gymeaze.viewmodel.posts.PostsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isUserLoggedIn: Boolean,
    username: String,
    onLogout: () -> Unit,
    onExerciseCreatorClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onBrowseExercisesClick: () -> Unit,
    onCreateTrainingPlanClick: () -> Unit,
    onMyTrainingPlansClick: () -> Unit = {},
    onBrowseTrainingPlansClick: () -> Unit = {},
    onFavoriteTrainingPlansClick: () -> Unit = {},
    onCreatePostClick: () -> Unit = {},
    onBrowsePostClick: () -> Unit = {},
    onFollowedUsersPosts: () -> Unit = {},
    onMyPosts: () -> Unit = {},
    onStartedPlanClick: (userTrainingPlanId: String, planName: String) -> Unit,
    onPlanClick: (planId: String) -> Unit = {},
    onEditPlanClick: (planId: String) -> Unit = {},
    onUserProfileClick: (String) -> Unit = {}
) {
    val currentDate = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(Date())
    var selectedRoute by remember { mutableStateOf("profile") }

    var previousRoute by remember { mutableStateOf<String?>(null) }

    var savedExerciseSection by remember { mutableStateOf("menu") }
    var savedExerciseId by remember { mutableStateOf<String?>(null) }

    var previousPlanId by remember { mutableStateOf<String?>(null) }
    var previousPlanSection by remember { mutableStateOf<String?>(null) }

    var previousPlanContext by remember { mutableStateOf<String?>(null) }

    var previousPostContext by remember { mutableStateOf<String?>(null) }

    var displayedUsername by remember { mutableStateOf(username) }

    val createPostViewModel: PostsViewModel = viewModel()
    val createExerciseViewModel: ExerciseCreatorViewModel = viewModel()
    val exercisesViewModel: ExercisesViewModel = viewModel()

    val notificationViewModel: NotificationViewModel = viewModel()
    val notificationUiState by notificationViewModel.uiState.collectAsState()

    var showBottomBar by remember { mutableStateOf(true) }

    var selectedExerciseId by remember { mutableStateOf<String?>(null) }
    var showExerciseDetail by remember { mutableStateOf(false) }

    var selectedPlanId by remember { mutableStateOf<String?>(null) }
    var showPlanDetail by remember { mutableStateOf(false) }

    var showNotificationsSheet by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            when {
                showExerciseDetail || showPlanDetail -> {
                }
                selectedRoute in listOf("community", "exercises", "workouts") -> {
                }
                else -> {
                    GymEazeTopAppBar(
                        onSettingsClick = onSettingsClick,
                        showBackButton = displayedUsername != username,
                        onBackClick = {
                            displayedUsername = username
                            previousRoute?.let {
                                selectedRoute = it
                                previousRoute = null
                            }
                        },
                        showNotifications = isUserLoggedIn && displayedUsername == username,
                        onNotificationsClick = {
                            showNotificationsSheet = true
                            notificationViewModel.refresh()
                        },
                        notificationCount = notificationUiState.total
                    )
                }
            }
        },
        bottomBar = {
            if (showBottomBar && !showExerciseDetail && !showPlanDetail) {
                GymEazeBottomNavigationBar(
                    selectedRoute = selectedRoute,
                    onItemSelected = { route ->
                        selectedRoute = route
                        previousRoute = null
                        if (route == "profile") {
                            displayedUsername = username
                        }
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = getBackgroundColor()
    ) { paddingValues ->
        when {
            showExerciseDetail && selectedExerciseId != null -> {
                ExerciseDetailScreen(
                    exerciseId = selectedExerciseId!!,
                    onBackClick = {
                        showExerciseDetail = false
                        selectedExerciseId = null
                    },
                    onEditClick = null,
                    onDeleteSuccess = {
                        showExerciseDetail = false
                        selectedExerciseId = null
                    },
                    exercisesViewModel = exercisesViewModel,
                    isUserLoggedIn = isUserLoggedIn,
                    onUserClick = { clickedUsername ->
                        displayedUsername = clickedUsername
                        previousRoute = selectedRoute
                        selectedRoute = "profile"
                    }
                )
            }

            showPlanDetail && selectedPlanId != null -> {
                TrainingPlanDetailScreen(
                    planId = selectedPlanId!!,
                    onBackClick = {
                        showPlanDetail = false
                        selectedPlanId = null
                    },
                    onExerciseClick = { exerciseId ->
                        selectedExerciseId = exerciseId
                        showExerciseDetail = true
                    },
                    onUserClick = { userId ->
                    },
                    navigateToWorkoutPlayer = { userTrainingPlanId, planName, dayNumber ->
                        onStartedPlanClick(userTrainingPlanId, planName)
                    },
                    onEditPlanClick = { planId ->
                        onEditPlanClick(planId)
                    }
                )
            }

            selectedRoute == "exercises" -> {
                ExerciseScreen(
                    onSettingsClick = onSettingsClick,
                    isUserLoggedIn = isUserLoggedIn,
                    createExerciseViewModel = createExerciseViewModel,
                    setShowBottomBar = { showBottomBar = it },
                    initialExerciseSection = savedExerciseSection,
                    initialSelectedExerciseId = savedExerciseId,
                    onNavigationStateChanged = { section, exerciseId ->
                        savedExerciseSection = section
                        savedExerciseId = exerciseId
                    },
                    onUserClick = { clickedUsername ->
                        displayedUsername = clickedUsername
                        previousRoute = selectedRoute
                        selectedRoute = "profile"
                    }
                )
            }

            selectedRoute == "workouts" -> {
                if (isUserLoggedIn) {
                    TrainingPlansNavigationScreen(
                        onSettingsClick = onSettingsClick,
                        onCreateTrainingPlanClick = onCreateTrainingPlanClick,
                        onPlanClick = { planId ->
                            selectedPlanId = planId
                            showPlanDetail = true
                        },
                        onEditPlanClick = onEditPlanClick,
                        onStartedPlanClick = onStartedPlanClick,
                        onUserClick = { clickedUsername ->
                            previousPlanContext = "workouts"
                            previousPlanId = selectedPlanId

                            displayedUsername = clickedUsername
                            previousRoute = selectedRoute
                            selectedRoute = "profile"
                        },
                        setShowBottomBar = { showBottomBar = it }
                    )
                }
            }

            selectedRoute == "community" -> {
                PostScreen(
                    onSettingsClick = onSettingsClick,
                    isUserLoggedIn = isUserLoggedIn,
                    createPostViewModel = createPostViewModel,
                    setShowBottomBar = { showBottomBar = it },
                    onUserClick = { clickedUsername ->
                        previousPostContext = "community"

                        displayedUsername = clickedUsername
                        previousRoute = selectedRoute
                        selectedRoute = "profile"
                    }
                )
            }

            selectedRoute == "profile" -> {
                val profileViewModel: ProfileViewModel = viewModel()

                LaunchedEffect(displayedUsername) {
                    profileViewModel.loadUserProfile(displayedUsername)
                    if (displayedUsername == username) {
                        profileViewModel.loadStartedPlans()
                    }
                }

                LaunchedEffect(selectedRoute) {
                    if (selectedRoute == "profile" && displayedUsername == username) {
                        profileViewModel.loadStartedPlans()
                    }
                }

                ProfileScreen(
                    username = displayedUsername,
                    currentLoggedUsername = username,
                    currentDate = currentDate,
                    onEditProfileClick = onEditProfileClick,
                    onExerciseCreatorClick = onExerciseCreatorClick,
                    onBrowseExercisesClick = onBrowseExercisesClick,
                    onCreateTrainingPlanClick = onCreateTrainingPlanClick,
                    paddingValues = paddingValues,
                    isUserLoggedIn = isUserLoggedIn,
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    profileViewModel = profileViewModel,
                    onStartedPlanClick = onStartedPlanClick,
                    onUserProfileClick = { clickedUsername ->
                        displayedUsername = clickedUsername
                    },
                    onExerciseClick = { exerciseId ->
                        selectedExerciseId = exerciseId
                        showExerciseDetail = true
                    },
                    onPlanClick = { planId ->
                        if (previousPlanContext?.startsWith("plan_detail_") == true && previousPlanId != null) {
                            selectedPlanId = previousPlanId
                            showPlanDetail = true
                            selectedRoute = "workouts"
                            previousPlanContext = null
                            previousPlanId = null
                        } else if (previousPlanContext == "workouts" && previousPlanId != null) {
                            selectedPlanId = previousPlanId
                            showPlanDetail = true
                            selectedRoute = "workouts"
                            previousPlanContext = null
                            previousPlanId = null
                        } else {
                            selectedPlanId = planId
                            showPlanDetail = true
                        }
                    },
                    onBrowseTrainingPlansClick = { /* TODO */ },
                    onBrowsePostClick = {
                        if (previousPostContext == "community") {
                            selectedRoute = "community"
                            previousPostContext = null
                        }
                    }
                )
            }
        }
        if (showNotificationsSheet) {
            NotificationsDialog(
                notifications = notificationUiState.notifications,
                total = notificationUiState.total,
                isLoading = notificationUiState.isLoading,
                hasMore = notificationUiState.hasMore,
                onDismiss = {
                    showNotificationsSheet = false
                },
                onDeleteAll = {
                    notificationViewModel.deleteAllNotifications()
                },
                onLoadMore = {
                    notificationViewModel.loadNotifications(isRefresh = false)
                },
                onNotificationClick = { notification ->

                    when (notification.type) {
                        "follow" -> {
                            notification.relatedId?.let { userId ->
                                onUserProfileClick(userId)
                            }
                        }

                        "like", "comment" -> {
                            notification.relatedId?.let { postId ->
                                scope.launch {
                                    snackbarHostState.showSnackbar("Post: $postId")
                                }
                            }
                        }

                        "plan_like", "plan_start" -> {
                            notification.relatedId?.let { planId ->
                                onPlanClick(planId)
                            }
                        }
                    }
                }
            )
        }
    }
}
@Composable
fun ProfileScreen(
    isUserLoggedIn: Boolean,
    username: String,
    currentLoggedUsername: String,
    currentDate: String,
    onEditProfileClick: () -> Unit,
    onExerciseCreatorClick: () -> Unit,
    onBrowseExercisesClick: () -> Unit,
    onCreateTrainingPlanClick: () -> Unit,
    paddingValues: PaddingValues,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    profileViewModel: ProfileViewModel,
    onStartedPlanClick: (userTrainingPlanId: String, planName: String) -> Unit,
    onUserProfileClick: (String) -> Unit = {},
    onExerciseClick: (String) -> Unit = {},
    onPlanClick: (String) -> Unit = {},
    onBrowseTrainingPlansClick: () -> Unit,
    onBrowsePostClick: () -> Unit,
) {
    val uiState by profileViewModel.uiState.collectAsState()
    val userViewModel: UserViewModel = viewModel()
    val availableBadges by userViewModel.availableBadges.collectAsState()

    val userExercisesViewModel: ExercisesViewModel = viewModel()
    val isOwnProfile = username == currentLoggedUsername

    val postViewModel: PostsViewModel = viewModel()

    var selectedTab by remember { mutableStateOf(ProfileTab.POSTS) }

    LaunchedEffect(username) {
        profileViewModel.loadUserProfile(username)
        userExercisesViewModel.loadExercisesByUsername(username)
        postViewModel.fetchUserPostsByUsername(username)
    }

    LaunchedEffect(uiState.operationError) {
        uiState.operationError?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(message = error, duration = SnackbarDuration.Short)
            }
            profileViewModel.clearOperationMessages()
        }
    }

    LaunchedEffect(uiState.operationSuccessMessage) {
        uiState.operationSuccessMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
            }
            profileViewModel.clearOperationMessages()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        BackgroundWithDotsAndCircles()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                val profile = uiState.userProfile
                val bmi = calculateBmi(
                    profile?.fitnessDetails?.height,
                    profile?.fitnessDetails?.weight
                )

                WelcomeSection(
                    username = username,
                    currentDate = currentDate,
                    isOwnProfile = isOwnProfile,
                    onEditProfileClick = onEditProfileClick,
                    profilePicture = profile?.profilePicture,
                    description = profile?.description,
                    firstName = profile?.personalDetails?.firstName,
                    lastName = profile?.personalDetails?.lastName,
                    country = profile?.personalDetails?.country,
                    gender = profile?.fitnessDetails?.gender,
                    age = profile?.fitnessDetails?.age,
                    height = profile?.fitnessDetails?.height,
                    weight = profile?.fitnessDetails?.weight?.toDouble(),
                    bmi = bmi,
                    trainingGoal = profile?.goal,
                    pinnedBadges = profile?.pinnedBadges ?: emptyList()
                )
            }

            item {
                val profile = uiState.userProfile

                if (!isOwnProfile) {
                    if (profile != null) {
                        FollowButton(
                            isFollowing = profile.isFollowing,
                            isLoading = uiState.isFollowLoading,
                            onClick = { profileViewModel.toggleFollow(profile.id) }
                        )
                    } else {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .wrapContentWidth()
                            )
                        }
                    }
                }
            }

            item {
                val profile = uiState.userProfile

                FollowStatsSection(
                    followersCount = profile?.followersCount ?: 0,
                    followingCount = profile?.followingCount ?: 0,
                    onFollowersClick = {
                        if (isOwnProfile) {
                            profileViewModel.loadFollowersAndFollowing()
                            profileViewModel.showFollowersDialog()
                        }
                    },
                    onFollowingClick = {
                        if (isOwnProfile) {
                            profileViewModel.loadFollowersAndFollowing()
                            profileViewModel.showFollowingDialog()
                        }
                    }
                )
            }

            if (isOwnProfile) {
                item {
                    RecentActivitySection(
                        isLoading = uiState.isLoading,
                        startedPlans = uiState.startedPlans,
                        onStartedPlanClick = onStartedPlanClick,
                        onRefresh = { profileViewModel.loadStartedPlans() },
                        onDeletePlan = { planId -> profileViewModel.deleteStartedPlan(planId) },
                        onResetPlan = { planId -> profileViewModel.resetStartedPlan(planId) },
                        deletingPlanId = uiState.deletingPlanId,
                        resettingPlanId = uiState.resettingPlanId
                    )
                }
            } else {
                item {
                    ProfileTabRow(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }

                item {
                    when (selectedTab) {
                        ProfileTab.POSTS -> {
                            UserPostsSection(
                                username = username,
                                onBrowsePostClick = onBrowsePostClick,
                                postsViewModel = postViewModel,
                                currentLoggedUsername = currentLoggedUsername,
                                isUserLoggedIn = isUserLoggedIn,
                                onEditPost = { post ->
                                },
                                onDeletePost = { post ->
                                },
                                onHashtagClick = { hashtag ->
                                },
                                onCommentClick = { postId ->
                                },
                                onReportClick = { postId ->
                                },
                                onToggleLike = { postId ->
                                    postViewModel.toggleLike(postId)
                                }
                            )
                        }
                        ProfileTab.EXERCISES -> {
                            UserExercisesSection(
                                username = username,
                                userExercisesViewModel = userExercisesViewModel,
                                onExerciseClick = { exerciseId ->
                                    onExerciseClick(exerciseId)
                                },
                                onBrowseExercisesClick = onBrowseExercisesClick,
                                isOwnProfile = isOwnProfile,
                            )
                        }
                        ProfileTab.TRAINING_PLANS -> {
                            UserTrainingPlansSection(
                                username = username,
                                onBrowseTrainingPlansClick = onBrowseTrainingPlansClick,
                                onPlanClick = { planId ->
                                    onPlanClick(planId)
                                }
                            )
                        }
                        ProfileTab.STARTED_TRAINING_PLANS -> {
                            UserStartedPlansSection(
                                username = username,
                                profileViewModel = profileViewModel,
                                onStartedPlanClick = onStartedPlanClick,
                                isOwnProfile = isOwnProfile
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.showFollowersDialog) {
        FollowListDialog(
            title = "Obserwujący",
            users = uiState.followers,
            isLoading = uiState.isLoadingFollowers,
            isFollowers = true,
            onDismiss = { profileViewModel.hideFollowersDialog() },
            onUserClick = { clickedUsername ->
                profileViewModel.hideFollowersDialog()
                onUserProfileClick(clickedUsername)
            }
        )
    }

    if (uiState.showFollowingDialog) {
        FollowListDialog(
            title = "Obserwowani",
            users = uiState.following,
            isLoading = uiState.isLoadingFollowers,
            isFollowers = false,
            onDismiss = { profileViewModel.hideFollowingDialog() },
            onUserClick = { clickedUsername ->
                profileViewModel.hideFollowingDialog()
                onUserProfileClick(clickedUsername)
            }
        )
    }
}

private fun calculateBmi(height: Int?, weight: Int?): Double? {
    if (height == null || weight == null || height == 0) return null
    val heightInMeters = height / 100.0
    return weight / (heightInMeters * heightInMeters)
}

@Composable
fun FollowButton(
    isFollowing: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isFollowing) getPrimaryColor() else Color.Transparent,
            contentColor = if (isFollowing) getOnPrimaryColor() else getPrimaryColor()
        ),
        border = BorderStroke(1.dp,getPrimaryColor()),
        shape = RoundedCornerShape(8.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = getOnPrimaryColor()
            )
        } else {
            Text(
                text = if (isFollowing) "Przestań obserwować" else "Obserwuj",
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun WelcomeSection(
    username: String,
    currentDate: String,
    isOwnProfile: Boolean,
    onEditProfileClick: () -> Unit,
    profilePicture: String? = null,
    description: String? = null,
    firstName: String? = null,
    lastName: String? = null,
    country: String? = null,
    gender: String? = null,
    age: Int? = null,
    height: Int? = null,
    weight: Double? = null,
    bmi: Double? = null,
    trainingGoal: String? = null,
    pinnedBadges: List<Badge> = emptyList()
) {
    var selectedBadge by remember { mutableStateOf<Badge?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = getOnSurfaceColor().copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UserAvatar(
                        imageUrl = profilePicture,
                        username = username,
                        modifier = Modifier.size(48.dp)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = username,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = getOnSurfaceColor()
                        )

                        if (firstName != null && lastName != null) {
                            Text(
                                text = "$firstName $lastName",
                                fontSize = 14.sp,
                                color = getOnSurfaceSecondaryColor(),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                if (isOwnProfile) {
                    IconButton(
                        onClick = onEditProfileClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edytuj profil",
                            tint = getOnSurfaceSecondaryColor(),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            description?.takeIf { it.isNotEmpty() }?.let { bio ->
                Text(
                    text = bio,
                    fontSize = 14.sp,
                    color = getOnSurfaceSecondaryColor(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }


            if (gender != null || age != null || country != null) {
                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    gender?.let {
                        ProfileInfoChip(
                            icon = if (it.lowercase() == "mężczyzna" || it.lowercase() == "male")
                                Icons.Default.Man else Icons.Default.Woman,
                            text = it
                        )
                    }

                    age?.let {
                        ProfileInfoChip(
                            icon = Icons.Default.Cake,
                            text = "$it lat"
                        )
                    }

                    country?.let {
                        ProfileInfoChip(
                            icon = Icons.Default.Public,
                            text = it
                        )
                    }
                }
            }

            if (height != null || weight != null || bmi != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    height?.let {
                        MeasurementItem(
                            value = "$it cm",
                            label = "Wzrost"
                        )
                    }

                    weight?.let {
                        MeasurementItem(
                            value = String.format("%.1f kg", it),
                            label = "Waga"
                        )
                    }

                    bmi?.let {
                        MeasurementItem(
                            value = String.format("%.1f", it),
                            label = "BMI",
                            color = getBmiColor(it)
                        )
                    }
                }
            }

            trainingGoal?.let { goal ->
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = getSurfaceVariantColor(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TrackChanges,
                        contentDescription = "Cel treningowy",
                        modifier = Modifier
                            .size(42.dp)
                            .padding(end = 8.dp),
                        tint = getOnSurfaceColor()
                    )
                    Column {
                        Text(
                            text = "Cel treningowy",
                            fontSize = 12.sp,
                            color = getOnSurfaceSecondaryColor()
                        )
                        Text(
                            text = goal,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = getOnSurfaceColor()
                        )
                    }
                }
            }

            if (pinnedBadges.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Column {
                    Text(
                        text = "Wyróżnione odznaki",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = getOnSurfaceColor(),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pinnedBadges.forEach { badge ->
                            BadgeItem(
                                badge = badge,
                                onClick = { selectedBadge = badge }
                            )
                        }
                    }
                }
            }
        }
    }

    selectedBadge?.let { badge ->
        BadgeDetailsDialog(
            badge = badge,
            onDismiss = { selectedBadge = null }
        )
    }
}

@Composable
private fun BadgeItem(
    badge: Badge,
    onClick: () -> Unit
) {
    val imageUrl = badge.image
    val title = badge.title

    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = getSurfaceVariantColor(),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNotEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Odznaka $title",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onError = {
                    println("ERROR loading badge image: $imageUrl for badge: $title")
                }
            )
        } else {
            Text(
                text = "🏆",
                fontSize = 24.sp,
                color = getOnSurfaceColor()
            )
        }
    }
}

@Composable
private fun BadgeDetailsDialog(
    badge: Badge,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = getCardBackgroundColor(),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = badge.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor()
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Zamknij",
                            tint = getOnSurfaceSecondaryColor()
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (badge.image.isNotEmpty()) {
                        AsyncImage(
                            model = badge.image,
                            contentDescription = badge.title,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    color = getSurfaceVariantColor(),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(
                                    color = getSurfaceVariantColor(),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🏆",
                                fontSize = 48.sp
                            )
                        }
                    }

                    if (badge.description.isNotEmpty()) {
                        Text(
                            text = badge.description,
                            fontSize = 14.sp,
                            color = getOnSurfaceColor(),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }

                    badge.awardedAt?.let { awardedAt ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Zdobyto:",
                                fontSize = 12.sp,
                                color = getOnSurfaceSecondaryColor(),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = formatBadgeDate(awardedAt),
                                fontSize = 14.sp,
                                color = getPrimaryColor(),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = getOnSurfaceColor()
                        ),
                        border = BorderStroke(
                            1.dp,
                            getInputBorderColor()
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Zamknij",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

private fun formatBadgeDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("pl"))
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

@Composable
private fun ProfileInfoChip(
    icon: ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .background(
                color = getSurfaceVariantColor(),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = getOnSurfaceColor(),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            fontSize = 13.sp,
            color = getOnSurfaceColor(),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun MeasurementItem(
    value: String,
    label: String,
    color: Color = getOnSurfaceColor()
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = getOnSurfaceSecondaryColor()
        )
    }
}

private fun getBmiColor(bmi: Double): Color {
    return when {
        bmi < 18.5 -> Color(0xFF3B82F6)
        bmi < 25.0 -> Color(0xFF10B981)
        bmi < 30.0 -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }
}
@Composable
private fun RecentActivitySection(
    isLoading: Boolean,
    startedPlans: List<StartedTrainingPlan>,
    onStartedPlanClick: (userTrainingPlanId: String, planName: String) -> Unit,
    onRefresh: () -> Unit,
    onDeletePlan: (String) -> Unit,
    onResetPlan: (String) -> Unit,
    deletingPlanId: String?,
    resettingPlanId: String?
) {
    Column {
        Text(
            text = "Aktywne Treningi",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = getOnSurfaceColor(),
            modifier = Modifier
                .padding(bottom = 8.dp)
        )

        when {
            isLoading && startedPlans.isEmpty() -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = getPrimaryColor())
                    }
                }
            }
            !isLoading && startedPlans.isEmpty() -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = getOnSurfaceColor().copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = getOnSurfaceSecondaryColor(),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Brak aktywności",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = getOnSurfaceColor(),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Rozpocznij swój pierwszy trening!",
                            fontSize = 14.sp,
                            color = getOnSurfaceSecondaryColor(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            else -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    startedPlans.forEach { plan ->
                        val isComplete = plan.completedDays >= plan.totalDays
                        StartedPlanCard(
                            plan = plan,
                            onClick = if (!isComplete) {
                                { onStartedPlanClick(plan.id, plan.name) }
                            } else {
                                null
                            },
                            onDelete = { onDeletePlan(plan.id) },
                            onReset = { onResetPlan(plan.id) },
                            isDeleting = deletingPlanId == plan.id,
                            isResetting = resettingPlanId == plan.id,
                            isComplete = isComplete
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StartedPlanCard(
    plan: StartedTrainingPlan,
    onClick: (() -> Unit)?,
    onDelete: () -> Unit,
    onReset: () -> Unit,
    isDeleting: Boolean,
    isResetting: Boolean,
    isComplete: Boolean
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    var showMenu by remember { mutableStateOf(false) }

    val contentAlpha = if (isComplete) 0.6f else 1.0f
    val cardBackgroundColor = if (isComplete) {
        getOnSurfaceColor().copy(alpha = 0.05f)
            .compositeOver(getSurfaceColor())
    } else {
        getSurfaceColor()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .border(
                width = 1.dp,
                color = getOnSurfaceColor().copy(alpha = 0.1f * contentAlpha),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    top = 12.dp,
                    bottom = 12.dp,
                    end = 4.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = plan.image,
                contentDescription = plan.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                alpha = contentAlpha
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = plan.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = getOnSurfaceColor().copy(alpha = contentAlpha),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Autor: ${plan.author.username}",
                    fontSize = 12.sp,
                    color = getOnSurfaceSecondaryColor().copy(alpha = contentAlpha),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                val progress = if (plan.totalDays > 0) {
                    plan.completedDays.toFloat() / plan.totalDays.toFloat()
                } else 0f

                Text(
                    text = if(isComplete) "Ukończono" else "Ukończono ${plan.completedDays} z ${plan.totalDays} dni",
                    fontSize = 10.sp,
                    color = if (isComplete) getPrimaryColor() else getOnSurfaceSecondaryColor().copy(alpha = contentAlpha)
                )
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = getPrimaryColor().copy(alpha = contentAlpha),
                    trackColor = getOnSurfaceColor().copy(alpha = 0.1f),
                    strokeCap = StrokeCap.Round
                )
            }

            Box(contentAlignment = Alignment.Center) {
                if (isDeleting || isResetting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = getPrimaryColor()
                    )
                } else {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opcje",
                            tint = getOnSurfaceSecondaryColor().copy(alpha = contentAlpha)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(getSurfaceVariantColor())
                    ) {
                        DropdownMenuItem(
                            text = { Text("Resetuj postęp") },
                            onClick = {
                                showMenu = false
                                showResetDialog = true
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Resetuj"
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Usuń plan", color = getErrorColor()) },
                            onClick = {
                                showMenu = false
                                showDeleteDialog = true
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.DeleteOutline,
                                    contentDescription = "Usuń",
                                    tint = getErrorColor()
                                )
                            }
                        )
                    }
                }
            }
        }
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Potwierdź usunięcie") },
            text = { Text("Czy na pewno chcesz usunąć ten rozpoczęty plan treningowy? Tej operacji nie można cofnąć.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = getErrorColor())
                ) {
                    Text("Usuń")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = getOnSurfaceColor())
                ) {
                    Text("Anuluj")
                }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Potwierdź reset") },
            text = { Text("Czy na pewno chcesz zresetować postęp w tym planie? Ukończone dni zostaną oznaczone jako nieukończone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onReset()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = getPrimaryColor())
                ) {
                    Text("Resetuj")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = getOnSurfaceColor())
                    ) {
                    Text("Anuluj")
                }
            }
        )
    }
}