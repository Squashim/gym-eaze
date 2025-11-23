package com.example.gymeaze.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.R
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.ExercisesViewModel

enum class ExerciseTab{
    COMMUNITY,
    MY,
    FAVORITES
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSelectionScreen(
    onBackClick: () -> Unit,
    onExerciseSelected: (Exercise) -> Unit,
    viewModel: ExercisesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var selectedTab by remember { mutableStateOf(ExerciseTab.COMMUNITY) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.exercise_selection_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                            tint = getOnSurfaceColor()
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = getBackgroundColor()
                )
            )
        },
        containerColor = getBackgroundColor()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp)),
                containerColor = getSurfaceColor(),
                contentColor = getPrimaryColor(),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        color = getPrimaryColor(),
                        height = 3.dp
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = selectedTab === ExerciseTab.COMMUNITY,
                    onClick = {selectedTab = ExerciseTab.COMMUNITY},
                    modifier = Modifier.height(48.dp)
                ){
                    Text(
                        text = stringResource(R.string.exercise_selection_tab_community),
                        fontSize = 14.sp,
                        fontWeight = if (selectedTab == ExerciseTab.COMMUNITY) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == ExerciseTab.COMMUNITY) getPrimaryColor() else getOnSurfaceSecondaryColor()
                    )
                }

                Tab(
                    selected = selectedTab == ExerciseTab.MY,
                    onClick = { selectedTab = ExerciseTab.MY },
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = stringResource(R.string.exercise_selection_tab_my),
                        fontSize = 14.sp,
                        fontWeight = if (selectedTab == ExerciseTab.MY) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == ExerciseTab.MY) getPrimaryColor() else getOnSurfaceSecondaryColor()
                    )
                }

                Tab(
                    selected = selectedTab == ExerciseTab.FAVORITES,
                    onClick = { selectedTab = ExerciseTab.FAVORITES },
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = stringResource(R.string.exercise_selection_tab_favorites),
                        fontSize = 14.sp,
                        fontWeight = if (selectedTab == ExerciseTab.FAVORITES) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == ExerciseTab.FAVORITES) getPrimaryColor() else getOnSurfaceSecondaryColor()
                    )
                }
            }

            if(selectedTab == ExerciseTab.COMMUNITY){
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::updateSearchQuery,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.exercises_search_hint),
                            color = getOnSurfaceSecondaryColor()
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.action_search),
                            tint = getOnSurfaceSecondaryColor()
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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

            LaunchedEffect(selectedTab) {
                when (selectedTab) {
                    ExerciseTab.COMMUNITY -> {
                        viewModel.switchToMode(ExercisesViewModel.ExerciseMode.ALL)
                    }
                    ExerciseTab.MY -> {
                        val userId = viewModel.currentUser.value?._id
                        if (userId != null) {
                            viewModel.switchToMode(ExercisesViewModel.ExerciseMode.MY_EXERCISES, userId)
                        }
                    }
                    ExerciseTab.FAVORITES -> {
                        viewModel.switchToMode(ExercisesViewModel.ExerciseMode.FAVORITES)
                    }
                }
            }

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = getPrimaryColor())
                    }
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = uiState.error ?: stringResource(R.string.error_unknown),
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.refreshExercises() }) {
                            Text(stringResource(R.string.action_try_again))
                        }
                    }
                }

                uiState.exercises.isEmpty() -> {
                    val (titleText, subtitleText) = when (selectedTab) {
                        ExerciseTab.COMMUNITY -> stringResource(R.string.exercise_selection_empty_community_title) to stringResource(R.string.exercise_selection_empty_community_subtitle)
                        ExerciseTab.MY -> stringResource(R.string.exercise_selection_empty_my_title) to stringResource(R.string.exercise_selection_empty_my_subtitle)
                        ExerciseTab.FAVORITES -> stringResource(R.string.exercise_selection_empty_favorites_title) to stringResource(R.string.exercise_selection_empty_favorites_subtitle)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = titleText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = getOnSurfaceColor()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = subtitleText,
                            fontSize = 14.sp,
                            color = getOnSurfaceSecondaryColor(),
                            lineHeight = 18.sp
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.exercises) { exercise ->
                            ExerciseSelectionCard(
                                exercise = exercise,
                                onSelect = { onExerciseSelected(exercise) }
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

@Composable
private fun ExerciseSelectionCard(
    exercise: Exercise,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
        onClick = onSelect
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            coil.compose.AsyncImage(
                model = exercise.image,
                contentDescription = exercise.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = exercise.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = getOnSurfaceColor(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.exercise_author_label, exercise.author.username),
                    fontSize = 12.sp,
                    color = getOnSurfaceSecondaryColor()
                )

                if (exercise.muscleGroup.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = exercise.muscleGroup.joinToString(", ") { it.name },
                        fontSize = 12.sp,
                        color = getOnSurfaceSecondaryColor(),
                        maxLines = 1
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.edit_plan_add_exercise_desc),
                tint = getPrimaryColor(),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}