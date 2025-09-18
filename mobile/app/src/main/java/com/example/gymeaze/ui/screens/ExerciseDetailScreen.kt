package com.example.gymeaze.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.data.models.Equipment
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.data.models.MuscleGroup
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.ExerciseDetailViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import java.text.SimpleDateFormat
import com.example.gymeaze.R
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exerciseId: String,
    onBackClick: () -> Unit,
    viewModel: ExerciseDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(exerciseId) {
        viewModel.loadExercise(exerciseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.exercise?.name ?: "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor(),
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wstecz",
                            tint = getOnSurfaceColor()
                        )
                    }
                },
                actions = {
                    uiState.exercise?.let { exercise ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.toggleLike() }
                            ) {
                                Icon(
                                    imageVector = if (exercise.likes > 0) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Polub",
                                    tint = getPrimaryColor()
                                )
                            }

                            Text(
                                text = exercise.likes.toString(),
                                fontSize = 14.sp,
                                color = getOnSurfaceColor(),
                                modifier = Modifier.padding(end = 8.dp)
                            )

                            IconButton(onClick = { /* TODO: Implementacja reportu */ }) {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = "Zgłoś",
                                    tint = getOnSurfaceSecondaryColor()
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = getBackgroundColor()
                )
            )
        },
        containerColor = getBackgroundColor()
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
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
                        text = uiState.error ?: "Wystąpił błąd",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.loadExercise(exerciseId) }) {
                        Text("Spróbuj ponownie")
                    }
                }
            }
            uiState.exercise != null -> {
                ExerciseDetailContent(
                    exercise = uiState.exercise!!,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun ExerciseDetailContent(
    exercise: Exercise,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            VideoPlayerSection(
                videoUrl = exercise.video,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            DifficultySection(difficulty = exercise.advancedLevel)
        }

        item {
            DescriptionSection(description = exercise.description)
        }

        item {
            MuscleGroupsSection(muscleGroups = exercise.muscleGroup)
        }

        item {
            EquipmentSection(equipment = exercise.equipment)
        }

        if (exercise.hashtags.isNotEmpty()) {
            item {
                HashtagsSection(hashtags = exercise.hashtags)
            }
        }

        item {
            AuthorSection(
                author = exercise.author,
                createdAt = exercise.createdAt
            )
        }
    }
}

@Composable
private fun VideoPlayerSection(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
        }
    }

    DisposableEffect(
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp)),
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = true
                    setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                }
            }
        )
    ) {
        onDispose {
            exoPlayer.release()
        }
    }
}

@Composable
private fun DifficultySection(difficulty: String) {
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
            val (text, badgeColor, textColor) = when (difficulty) {
                AdvancedLevel.EASY -> Triple("Początkujący", Color(0xFF4CAF50), getOnPrimaryColor())
                AdvancedLevel.MEDIUM -> Triple("Średni", Color(0xFFFF9800), getOnPrimaryColor())
                AdvancedLevel.HARD -> Triple("Zaawansowany", Color(0xFFF44336), getOnPrimaryColor())
                else -> Triple("Nieznany", getOnSurfaceColor().copy(alpha = 0.2f), getOnSurfaceColor())
            }

            Box(
                modifier = Modifier
                    .background(badgeColor, RoundedCornerShape(8.dp))
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

@Composable
private fun DescriptionSection(description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Opis ćwiczenia:",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = getOnSurfaceColor()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = getOnSurfaceColor(),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun MuscleGroupsSection(muscleGroups: List<MuscleGroup>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Aktywne grupy mięśniowe:",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = getOnSurfaceColor()
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(muscleGroups) { muscleGroup ->
                    MuscleGroupItem(muscleGroup = muscleGroup)
                }
            }
        }
    }
}

@Composable
private fun MuscleGroupItem(muscleGroup: MuscleGroup) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    getPrimaryColor().copy(alpha = 0.1f),
                    CircleShape
                )
                .border(2.dp, getPrimaryColor(), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = muscleGroup.image,
                contentDescription = muscleGroup.name,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.gym_eaze_logo_light),
                error = painterResource(R.drawable.gym_eaze_logo_dark)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = muscleGroup.name,
            fontSize = 12.sp,
            color = getOnSurfaceColor(),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun EquipmentSection(equipment: List<Equipment>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Potrzebny sprzęt ćwiczeniowy:",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = getOnSurfaceColor()
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(equipment) { equipmentItem ->
                    EquipmentItem(equipment = equipmentItem)
                }
            }
        }
    }
}

@Composable
private fun EquipmentItem(equipment: Equipment) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    getOnSurfaceColor().copy(alpha = 0.1f),
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = equipment.image,
                contentDescription = equipment.name,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.gym_eaze_logo_light),
                error = painterResource(R.drawable.gym_eaze_logo_dark)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = equipment.name,
            fontSize = 12.sp,
            color = getOnSurfaceColor(),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun HashtagsSection(hashtags: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Hashtagi:",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = getOnSurfaceColor()
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(hashtags) { hashtag ->
                    HashtagChip(hashtag = hashtag)
                }
            }
        }
    }
}

@Composable
private fun HashtagChip(hashtag: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = getPrimaryColor().copy(alpha = 0.1f),
        modifier = Modifier
            .border(1.dp, getPrimaryColor().copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    ) {
        Text(
            text = "#$hashtag",
            fontSize = 12.sp,
            color = getPrimaryColor(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun AuthorSection(
    author: com.example.gymeaze.data.models.ExerciseAuthor,
    createdAt: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Dodane przez:",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = getOnSurfaceColor()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = author.profileDetails?.profilePicture ?: "",
                    contentDescription = "Zdjęcie profilowe",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(getOnSurfaceColor().copy(alpha = 0.1f)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = author.username,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor()
                    )

                    Text(
                        text = formatDate(createdAt),
                        fontSize = 12.sp,
                        color = getOnSurfaceSecondaryColor()
                    )
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}