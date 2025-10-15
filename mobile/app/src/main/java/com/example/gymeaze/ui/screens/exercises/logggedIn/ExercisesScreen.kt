package com.example.gymeaze.ui.screens.exercises.logggedIn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.data.models.Equipment
import com.example.gymeaze.data.models.MuscleGroup
import com.example.gymeaze.ui.components.ExerciseCard
import com.example.gymeaze.ui.components.HashtagInputField
import com.example.gymeaze.ui.components.MultiSelectDropdown
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.ExercisesViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.DialogProperties
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.ui.components.BackgroundWithDotsAndCircles
import com.example.gymeaze.ui.components.LoadingScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(
    onSettingsClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    onExerciseClick: (String) -> Unit = {},
    onEditExercise: (Exercise) -> Unit = {},
    viewModel: ExercisesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedMuscleGroups by viewModel.selectedMuscleGroups.collectAsState()
    val selectedEquipment by viewModel.selectedEquipment.collectAsState()
    val selectedHashtags by viewModel.selectedHashtags.collectAsState()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()
    val followersOnly by viewModel.followersOnly.collectAsState()
    val availableMuscleGroups by viewModel.availableMuscleGroups.collectAsState()
    val availableEquipment by viewModel.availableEquipment.collectAsState()
    val favoriteExercises by viewModel.favoriteExercises.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val scrollIndex by viewModel.scrollIndex
    val scrollOffset by viewModel.scrollOffset

    var showFilters by remember { mutableStateOf(false) }
    var lastLoadedUserId by remember { mutableStateOf<String?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var exerciseToDelete by remember { mutableStateOf<Exercise?>(null) }

    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = scrollIndex,
        initialFirstVisibleItemScrollOffset = scrollOffset
    )

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.reloadCurrentUser()
    }

    LaunchedEffect(currentUser?._id) {
        val userId = currentUser?._id
        if (userId != null && userId != lastLoadedUserId) {
            viewModel.reloadUserDataForExercises()
            lastLoadedUserId = userId
        }
    }

    LaunchedEffect(Unit) {
        viewModel.switchToMode(ExercisesViewModel.ExerciseMode.ALL)
    }

    LaunchedEffect(Unit) {
        viewModel.refreshExercises()
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow {
            lazyListState.firstVisibleItemIndex to lazyListState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            viewModel.updateScrollPosition(index, offset)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundWithDotsAndCircles(modifier = Modifier.fillMaxSize())

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Przeglądaj ćwiczenia",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = getOnSurfaceColor()
                        )
                    },
                    navigationIcon = {
                        onBackClick?.let { backCallback ->
                            IconButton(onClick = backCallback) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Wstecz",
                                    tint = getOnSurfaceColor()
                                )
                            }
                        }
                    },
                    actions = {
                        val hasFilters = hasActiveFilters(
                            selectedMuscleGroups,
                            selectedEquipment,
                            selectedHashtags,
                            selectedDifficulty,
                            sortBy,
                            followersOnly
                        )

                        BadgedBox(
                            badge = {
                                if (hasFilters) {
                                    Badge(
                                        containerColor = getPrimaryColor(),
                                        modifier = Modifier.size(8.dp)
                                    )
                                }
                            }
                        ) {
                            IconButton(onClick = { showFilters = true }) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Filtry",
                                    tint = getOnSurfaceColor()
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = getBackgroundColor()
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            if (uiState.isLoading) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                ) {
                    LoadingScreen()
                }
            } else {
                PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { viewModel.refreshExercises() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        SearchField(
                            query = searchQuery,
                            onQueryChange = { query ->
                                viewModel.updateSearchQuery(query)
                                coroutineScope.launch {
                                    lazyListState.scrollToItem(0)
                                    viewModel.resetScrollPosition()
                                }
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        when {
                            uiState.error != null -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = uiState.error ?: "Wystąpił nieznany błąd",
                                        color = MaterialTheme.colorScheme.error,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(onClick = { viewModel.refreshExercises() }) {
                                        Text("Spróbuj ponownie")
                                    }
                                }
                            }

                            uiState.exercises.isEmpty() -> {
                                EmptyExercisesMessage()
                            }

                            else -> {
                                LazyColumn(
                                    state = lazyListState,
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(
                                        items = uiState.exercises,
                                        key = { exercise -> exercise.id }
                                    ) { exercise ->
                                        ExerciseCard(
                                            exercise = exercise,
                                            isLiked = viewModel.isExerciseLiked(exercise.id),
                                            isFavorite = favoriteExercises.contains(exercise.id),
                                            isCurrentUserExercise = viewModel.isCurrentUserExercise(exercise.author.username),
                                            onLikeClick = { viewModel.toggleLike(exercise.id) },
                                            onFavoriteClick = { viewModel.toggleFavorite(exercise.id) },
                                            onEditClick = { exerciseId ->
                                                val exerciseToEdit = uiState.exercises.find { it.id == exerciseId }
                                                exerciseToEdit?.let { onEditExercise(it) }
                                            },
                                            onDeleteClick = { exerciseId ->
                                                val exercise = uiState.exercises.find { it.id == exerciseId }
                                                exercise?.let {
                                                    exerciseToDelete = it
                                                    showDeleteDialog = true
                                                }
                                            },
                                            onExerciseClick = { onExerciseClick(exercise.id) }
                                        )
                                    }

                                    if (uiState.hasMorePages) {
                                        item {
                                            LaunchedEffect(Unit) {
                                                viewModel.loadMoreExercises()
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (uiState.isLoadingMore) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(24.dp),
                                                        color = getPrimaryColor(),
                                                        strokeWidth = 2.dp
                                                    )
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
        }

        if (showFilters) {
            FilterDialog(
                selectedMuscleGroups = selectedMuscleGroups,
                availableMuscleGroups = availableMuscleGroups,
                onMuscleGroupsChange = { groups ->
                    viewModel.updateSelectedMuscleGroups(groups)
                    coroutineScope.launch {
                        lazyListState.scrollToItem(0)
                        viewModel.resetScrollPosition()
                    }
                },
                selectedEquipment = selectedEquipment,
                availableEquipment = availableEquipment,
                onEquipmentChange = { equipment ->
                    viewModel.updateSelectedEquipment(equipment)
                    coroutineScope.launch {
                        lazyListState.scrollToItem(0)
                        viewModel.resetScrollPosition()
                    }
                },
                selectedHashtags = selectedHashtags,
                onHashtagsChange = { hashtags ->
                    viewModel.updateSelectedHashtags(hashtags)
                    coroutineScope.launch {
                        lazyListState.scrollToItem(0)
                        viewModel.resetScrollPosition()
                    }
                },
                selectedDifficulty = selectedDifficulty,
                onDifficultyChange = { difficulty ->
                    viewModel.updateSelectedDifficulty(difficulty)
                    coroutineScope.launch {
                        lazyListState.scrollToItem(0)
                        viewModel.resetScrollPosition()
                    }
                },
                sortBy = sortBy,
                onSortByChange = { sort ->
                    viewModel.updateSortBy(sort)
                    coroutineScope.launch {
                        lazyListState.scrollToItem(0)
                        viewModel.resetScrollPosition()
                    }
                },
                followersOnly = followersOnly,
                onFollowersOnlyChange = { enabled ->
                    viewModel.updateFollowersOnly(enabled)
                    coroutineScope.launch {
                        lazyListState.scrollToItem(0)
                        viewModel.resetScrollPosition()
                    }
                },
                onClearAll = {
                    viewModel.clearAllFilters()
                    coroutineScope.launch {
                        lazyListState.scrollToItem(0)
                        viewModel.resetScrollPosition()
                    }
                },
                onDismiss = { showFilters = false }
            )
        }

        if (showDeleteDialog && exerciseToDelete != null) {
            DeleteExerciseDialog(
                exerciseName = exerciseToDelete!!.name,
                onDismiss = {
                    showDeleteDialog = false
                    exerciseToDelete = null
                },
                onConfirm = {
                    val exerciseId = exerciseToDelete!!.id
                    showDeleteDialog = false
                    exerciseToDelete = null

                    viewModel.deleteExercise(exerciseId) {
                        viewModel.refreshExercises()
                    }
                }
            )
        }
    }
}

fun hasActiveFilters(
    muscleGroups: List<MuscleGroup>,
    equipment: List<Equipment>,
    hashtags: List<String>,
    difficulty: String?,
    sortBy: String,
    followersOnly: Boolean
): Boolean {
    return muscleGroups.isNotEmpty() ||
            equipment.isNotEmpty() ||
            hashtags.isNotEmpty() ||
            difficulty != null ||
            sortBy != "latest" ||
            followersOnly
}
@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Wyszukaj ćwiczenia...",
                color = getOnSurfaceSecondaryColor()
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Wyszukaj",
                tint = getOnSurfaceSecondaryColor()
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Wyczyść",
                        tint = getOnSurfaceSecondaryColor()
                    )
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = getPrimaryColor(),
            unfocusedBorderColor = getInputBorderColor(),
            focusedTextColor = getOnSurfaceColor(),
            unfocusedTextColor = getOnSurfaceColor(),
            cursorColor = getPrimaryColor()
        ),
        singleLine = true
    )
}

@Composable
fun FilterDialog(
    selectedMuscleGroups: List<MuscleGroup>,
    availableMuscleGroups: List<MuscleGroup>,
    onMuscleGroupsChange: (List<MuscleGroup>) -> Unit,
    selectedEquipment: List<Equipment>,
    availableEquipment: List<Equipment>,
    onEquipmentChange: (List<Equipment>) -> Unit,
    selectedHashtags: List<String>,
    onHashtagsChange: (List<String>) -> Unit,
    selectedDifficulty: String?,
    onDifficultyChange: (String?) -> Unit,
    sortBy: String,
    onSortByChange: (String) -> Unit,
    followersOnly: Boolean,
    onFollowersOnlyChange: (Boolean) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit,
    isUserLoggedIn: Boolean = true
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Filtry i sortowanie",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor()
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = onClearAll,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = getOnSurfaceSecondaryColor().copy(alpha = 0.1f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Wyczyść filtry",
                                tint = getOnSurfaceSecondaryColor()
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Zamknij",
                                tint = getOnSurfaceColor()
                            )
                        }
                    }
                }

                HorizontalDivider(color = getOnSurfaceSecondaryColor().copy(alpha = 0.2f))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item {
                        SortingSection(
                            selectedSort = sortBy,
                            onSortChange = onSortByChange
                        )
                    }

                    if (isUserLoggedIn) {
                        item {
                            FollowersOnlyToggle(
                                enabled = followersOnly,
                                onToggle = onFollowersOnlyChange
                            )
                        }
                    }

                    item {
                        MultiSelectDropdown(
                            label = "Partie mięśniowe",
                            selectedItems = selectedMuscleGroups,
                            availableItems = availableMuscleGroups,
                            onSelectionChange = onMuscleGroupsChange,
                            itemDisplayText = { it.name },
                            itemContent = { muscleGroup ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = muscleGroup.image,
                                        contentDescription = muscleGroup.name,
                                        modifier = Modifier.size(28.dp).clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = muscleGroup.name,
                                        color = getOnSurfaceColor()
                                    )
                                }
                            },
                            placeholder = "Wybierz partie mięśniowe",
                            isLoading = availableMuscleGroups.isEmpty(),
                            useFullScreen = true
                        )
                    }

                    item {
                        MultiSelectDropdown(
                            label = "Sprzęt",
                            selectedItems = selectedEquipment,
                            availableItems = availableEquipment,
                            onSelectionChange = onEquipmentChange,
                            itemDisplayText = { it.name },
                            itemContent = { equipment ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = equipment.image,
                                        contentDescription = equipment.name,
                                        modifier = Modifier.size(28.dp).clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = equipment.name,
                                        color = getOnSurfaceColor()
                                    )
                                }
                            },
                            placeholder = "Wybierz sprzęt",
                            isLoading = availableEquipment.isEmpty(),
                            useFullScreen = true
                        )
                    }

                    item {
                        DifficultySelector(
                            selectedDifficulty = selectedDifficulty,
                            onDifficultyChange = onDifficultyChange
                        )
                    }

                    item {
                        HashtagInputField(
                            selectedHashtags = selectedHashtags,
                            onHashtagsChange = onHashtagsChange,
                            maxHashtags = 10
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FollowersOnlyToggle(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = getOnSurfaceSecondaryColor().copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Tylko obserwowani",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = getOnSurfaceColor()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pokaż ćwiczenia tylko od obserwowanych użytkowników",
                    fontSize = 14.sp,
                    color = getOnSurfaceSecondaryColor(),
                    lineHeight = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = getPrimaryColor(),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = getOnSurfaceSecondaryColor().copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
fun SortingSection(
    selectedSort: String,
    onSortChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Sortowanie",
            color = getOnSurfaceColor(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SortChip(
                text = "Najnowsze",
                isSelected = selectedSort == "latest",
                onClick = { onSortChange("latest") }
            )
            SortChip(
                text = "Najwyżej oceniane",
                isSelected = selectedSort == "top-rated",
                onClick = { onSortChange("top-rated") }
            )
        }
    }
}

@Composable
fun SortChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                fontSize = 12.sp
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = getPrimaryColor(),
            selectedLabelColor = getOnPrimaryColor(),
            containerColor = getSurfaceColor(),
            labelColor = getOnSurfaceColor()
        )
    )
}

@Composable
fun DifficultySelector(
    selectedDifficulty: String?,
    onDifficultyChange: ((String?) -> Unit)? = null,
    modifier: Modifier = Modifier,
    isInteractive: Boolean = onDifficultyChange != null
) {
    Column(modifier = modifier) {
        if (isInteractive) {
            Text(
                text = "Poziom trudności",
                color = getOnSurfaceColor(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        val difficultyOptions = listOf(
            Triple(AdvancedLevel.NONE, if (isInteractive) "Nie wybrano" else "Nie wybrano", Color(0xFF9E9E9E)),
            Triple(AdvancedLevel.EASY, if (isInteractive) "Podstawowy" else "Początkujący", Color(0xFF4CAF50)),
            Triple(AdvancedLevel.MEDIUM, if (isInteractive) "Średniozaawansowany" else "Średni", Color(0xFFFF9800)),
            Triple(AdvancedLevel.HARD, "Zaawansowany", Color(0xFFF44336))
        )

        if (isInteractive) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(difficultyOptions.size) { index ->
                    val (backendValue, displayText, chipColor) = difficultyOptions[index]
                    FilterChip(
                        selected = selectedDifficulty == backendValue,
                        onClick = {
                            onDifficultyChange?.invoke(
                                if (selectedDifficulty == backendValue) null else backendValue
                            )
                        },
                        label = {
                            Text(
                                text = displayText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = chipColor,
                            selectedLabelColor = Color.White,
                            containerColor = chipColor.copy(alpha = 0.3f),
                            labelColor = chipColor.copy(alpha = 0.9f)
                        )
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Poziom zaawansowania: ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor(),
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    val selectedOption = difficultyOptions.find { it.first == selectedDifficulty }
                        ?: difficultyOptions[0]

                    val (_, text, badgeColor) = selectedOption
                    val textColor = if (selectedOption.first == AdvancedLevel.NONE) {
                        getOnSurfaceColor()
                    } else {
                        getOnPrimaryColor()
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                color = if (selectedOption.first == AdvancedLevel.NONE) {
                                    getOnSurfaceColor().copy(alpha = 0.2f)
                                } else {
                                    badgeColor
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = text,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyExercisesMessage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = getOnSurfaceSecondaryColor()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nie znaleziono ćwiczeń",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = getOnSurfaceColor(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Spróbuj zmienić filtry wyszukiwania lub wyczyść filtry",
            fontSize = 14.sp,
            color = getOnSurfaceSecondaryColor(),
            textAlign = TextAlign.Center
        )
    }
}