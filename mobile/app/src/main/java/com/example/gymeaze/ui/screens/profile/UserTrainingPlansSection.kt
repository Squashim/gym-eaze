package com.example.gymeaze.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.ui.screens.trainingsPlan.components.TrainingPlanCard
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor
import com.example.gymeaze.viewmodel.trainingsPlan.TrainingPlansViewModel

@Composable
fun UserTrainingPlansSection(
    username: String,
    onBrowseTrainingPlansClick: () -> Unit,
    onPlanClick: (String) -> Unit = {},
    onEditPlan: (String) -> Unit = {},
    onDeletePlan: (String) -> Unit = {},
    viewModel: TrainingPlansViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userPlans = uiState.userTrainingPlans
    val isLoading = uiState.isLoadingContent

    LaunchedEffect(username) {
        viewModel.loadUserTrainingPlans(username)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = getPrimaryColor(),
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else if (userPlans.isEmpty()) {
                EmptySectionMessage(
                    message = "$username nie ma jeszcze żadnych planów treningowych",
                    icon = Icons.Default.Assignment
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    userPlans.forEach { plan ->
                        TrainingPlanCard(
                            trainingPlan = plan,
                            onCardClick = {
                                onPlanClick(plan.id)
                            },
                            onLikeClick = { planId ->
                                viewModel.toggleLike(planId)
                            },
                            onFavoriteClick = { planId ->
                                viewModel.toggleFavorite(planId)
                            },
                            onEditClick = onEditPlan,
                            onDeleteClick = { trainingPlan ->
                                onDeletePlan(trainingPlan.id)
                            },
                            showOwnerActions = true
                        )
                    }
                }
            }
        }
    }
}
