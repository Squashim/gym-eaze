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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.data.models.StartedDay
import com.example.gymeaze.ui.components.BackgroundWithDotsAndCircles
import com.example.gymeaze.ui.theme.getBackgroundColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor
import com.example.gymeaze.ui.theme.getSurfaceVariantColor
import com.example.gymeaze.viewmodel.StartedPlanDaysViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartedPlanDaysScreen(
    userTrainingPlanId: String,
    planName: String,
    onBackClick: () -> Unit,
    onDayClick: (userTrainingPlanId: String, dayNumber: Int) -> Unit = { _, _ -> /* TODO */ },
    viewModel: StartedPlanDaysViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userTrainingPlanId, planName) {
        viewModel.loadDays(userTrainingPlanId, planName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Przebieg Treningu",
                            fontSize = 14.sp,
                            color = getOnSurfaceSecondaryColor()
                        )
                        Text(
                            text = uiState.planName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = getOnSurfaceColor()
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Wróć",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            BackgroundWithDotsAndCircles(modifier = Modifier.fillMaxSize())

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
                            text = uiState.error ?: "Wystąpił nieznany błąd",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refreshDays() }) {
                            Text("Spróbuj ponownie")
                        }
                    }
                }
                uiState.days.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Brak dni w tym planie.",
                            color = getOnSurfaceSecondaryColor(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Wybierz dzień, aby rozpocząć trening:",
                                fontSize = 16.sp,
                                color = getOnSurfaceColor(),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(uiState.days, key = { it.dayNumber }) { day ->
                            DayCard(
                                day = day,
                                onClick = {
                                    if (!day.isRestDay) {
                                        onDayClick(userTrainingPlanId, day.dayNumber)
                                    }
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(60.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCard(
    day: StartedDay,
    onClick: () -> Unit
) {
    val cardColor = if (day.isRestDay) getSurfaceColor() else getSurfaceVariantColor()
    val borderColor = getOnSurfaceColor().copy(alpha = 0.1f)
    val isClickable = !day.isRestDay

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .then(if (isClickable) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (day.isRestDay) getOnSurfaceColor().copy(alpha = 0.1f)
                            else getPrimaryColor().copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (day.isRestDay) Icons.Default.Bedtime else Icons.Default.FitnessCenter,
                        contentDescription = if (day.isRestDay) "Dzień odpoczynku" else "Dzień treningowy",
                        tint = if (day.isRestDay) getOnSurfaceSecondaryColor() else getPrimaryColor(),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = day.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor()
                    )
                }
            }

            if (!day.isRestDay) {
                val statusColor = if (day.isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                val statusIcon = if (day.isCompleted) Icons.Default.Check else Icons.Default.Close
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = if (day.isCompleted) "Ukończono" else "Nieukończono",
                        tint = statusColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}