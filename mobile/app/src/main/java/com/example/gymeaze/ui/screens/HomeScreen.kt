package com.example.gymeaze.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymeaze.data.models.StartedTrainingPlan
import com.example.gymeaze.ui.components.GymEazeBottomNavigationBar
import com.example.gymeaze.ui.components.GymEazeTopAppBar
import com.example.gymeaze.ui.screens.exercises.ExerciseScreen
import com.example.gymeaze.ui.screens.posts.PostScreen
import com.example.gymeaze.ui.screens.profile.FollowListDialog
import com.example.gymeaze.ui.screens.profile.FollowStatsSection
import com.example.gymeaze.ui.screens.trainingsPlan.TrainingPlansNavigationScreen
import com.example.gymeaze.ui.screens.trainingsPlan.guest.WorkoutsNotLoggedInScreen
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getErrorColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor
import com.example.gymeaze.ui.theme.getSurfaceVariantColor
import com.example.gymeaze.viewmodel.ExerciseCreatorViewModel
import com.example.gymeaze.viewmodel.ProfileViewModel
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
    onEditPlanClick: (planId: String) -> Unit = {}
) {
    val currentDate = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(Date())
    var selectedRoute by remember { mutableStateOf("profile") }

    val createPostViewModel: PostsViewModel = viewModel()
    val createExerciseViewModel: ExerciseCreatorViewModel = viewModel()
    var showBottomBar by remember { mutableStateOf(true) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            when (selectedRoute) {
                "community", "exercises", "workouts" -> {
                }
                else -> {
                    GymEazeTopAppBar(onSettingsClick = onSettingsClick)
                }
            }
        },
        bottomBar = {
            if (showBottomBar) {
                GymEazeBottomNavigationBar(
                    selectedRoute = selectedRoute,
                    onItemSelected = { route ->
                        selectedRoute = route
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = getBackgroundColor()
    ) { paddingValues ->
        when (selectedRoute) {
            "exercises" -> ExerciseScreen(
                onSettingsClick = onSettingsClick,
                isUserLoggedIn = isUserLoggedIn,
                createExerciseViewModel = createExerciseViewModel,
                setShowBottomBar = { showBottomBar = it }
            )

            "workouts" -> {
                if (isUserLoggedIn) {
                    TrainingPlansNavigationScreen(
                        onSettingsClick = onSettingsClick,
                        onCreateTrainingPlanClick = onCreateTrainingPlanClick,
                        onPlanClick = onPlanClick,
                        onEditPlanClick = onEditPlanClick,
                        onStartedPlanClick = onStartedPlanClick,
                        setShowBottomBar = { showBottomBar = it }
                    )
                } else {
                    WorkoutsNotLoggedInScreen(
                        onSettingsClick = onSettingsClick, // Przekaż callback
                        onPlanClick = { planId ->
                            // Tutaj obsłuż kliknięcie w plan
                            // Opcja 1: Jeśli masz nawigację wewnętrzną
                            //selectedPlanId = planId
                            //showPlanDetails = true

                            // Opcja 2: Jeśli przekazujesz callback z wyższego poziomu
                            // onPlanClick(planId)
                        }
                    )
                }
            }

            "community" -> PostScreen(
                onSettingsClick = onSettingsClick,
                isUserLoggedIn = isUserLoggedIn,
                createPostViewModel = createPostViewModel,
                setShowBottomBar = { showBottomBar = it }
            )

            "profile" -> {
                val profileViewModel: ProfileViewModel = viewModel()

                LaunchedEffect(selectedRoute) {
                    if (selectedRoute == "profile") {
                        profileViewModel.loadStartedPlans()
                    }
                }
                ProfileScreen(
                    username = username,
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
                    onStartedPlanClick = onStartedPlanClick
                )
            }
        }
    }
}
@Composable
fun ProfileScreen(
    isUserLoggedIn: Boolean,
    username: String,
    currentDate: String,
    onEditProfileClick: () -> Unit,
    onExerciseCreatorClick: () -> Unit,
    onBrowseExercisesClick: () -> Unit,
    onCreateTrainingPlanClick: () -> Unit,
    paddingValues: PaddingValues,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    profileViewModel: ProfileViewModel,
    onStartedPlanClick: (userTrainingPlanId: String, planName: String) -> Unit
) {

    val uiState by profileViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        profileViewModel.loadFollowersAndFollowing()
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(getBackgroundColor()),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
            item {
                WelcomeSection(
                    username = username,
                    currentDate = currentDate,
                    onEditProfileClick = onEditProfileClick
                )
            }
            item {
                FollowStatsSection(
                    followersCount = uiState.followersCount,
                    followingCount = uiState.followingCount,
                    onFollowersClick = { profileViewModel.showFollowersDialog() },
                    onFollowingClick = { profileViewModel.showFollowingDialog() }
                )
            }
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
    }
    if (uiState.showFollowersDialog) {
        FollowListDialog(
            title = "Obserwujący",
            users = uiState.followers,
            isLoading = uiState.isLoadingFollowers,
            isFollowers = true,
            onDismiss = { profileViewModel.hideFollowersDialog() }
        )
    }

    if (uiState.showFollowingDialog) {
        FollowListDialog(
            title = "Obserwowani",
            users = uiState.following,
            isLoading = uiState.isLoadingFollowers,
            isFollowers = false,
            onDismiss = { profileViewModel.hideFollowingDialog() }
        )
    }
}
@Composable
private fun WelcomeSection(
    username: String,
    currentDate: String,
    onEditProfileClick: () -> Unit,
    // Nowe parametry - na razie z wartościami domyślnymi
    firstName: String? = null,
    lastName: String? = null,
    country: String? = null,
    gender: String? = null,
    age: Int? = null,
    height: Int? = null,
    weight: Double? = null,
    bmi: Double? = null,
    trainingGoal: String? = null,
    badges: List<String> = emptyList()
) {
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
            // Górny wiersz z nazwą użytkownika i edycją
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = buildString {
                            append(username)
                            if (firstName != null && lastName != null) {
                                append(" ($firstName $lastName)")
                            }
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor()
                    )

                    Text(
                        text = currentDate,
                        fontSize = 12.sp,
                        color = getOnSurfaceSecondaryColor(),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

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

            // Podstawowe informacje
            if (gender != null || age != null || country != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    gender?.let {
                        ProfileInfoChip(
                            icon = if (it.lowercase() == "mężczyzna" || it.lowercase() == "male")
                                "♂" else "♀",
                            text = it
                        )
                    }

                    age?.let {
                        ProfileInfoChip(
                            icon = "🎂",
                            text = "$it lat"
                        )
                    }

                    country?.let {
                        ProfileInfoChip(
                            icon = "🌍",
                            text = it
                        )
                    }
                }
            }

            // Pomiary ciała
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

            // Cel treningowy
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
                    Text(
                        text = "🎯",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 8.dp)
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

            // Wyróżnione odznaki
            if (badges.isNotEmpty()) {
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
                        badges.take(3).forEach { badge ->
                            BadgeItem(badgeName = badge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoChip(
    icon: String,
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
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            fontSize = 14.sp
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

@Composable
private fun BadgeItem(badgeName: String) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(
                color = getSurfaceVariantColor(),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Tutaj możesz dodać ikonę odznaki
        // Na razie placeholder
        Text(
            text = "🏆",
            fontSize = 24.sp
        )
    }
}

// Funkcja pomocnicza do określenia koloru BMI
private fun getBmiColor(bmi: Double): Color {
    return when {
        bmi < 18.5 -> Color(0xFF3B82F6) // Niebieski - niedowaga
        bmi < 25.0 -> Color(0xFF10B981) // Zielony - prawidłowa
        bmi < 30.0 -> Color(0xFFF59E0B) // Pomarańczowy - nadwaga
        else -> Color(0xFFEF4444) // Czerwony - otyłość
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