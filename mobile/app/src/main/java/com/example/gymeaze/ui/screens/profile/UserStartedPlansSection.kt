import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gymeaze.data.models.StartedTrainingPlan
import com.example.gymeaze.ui.screens.profile.EmptySectionMessage
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.ProfileViewModel

@Composable
fun UserStartedPlansSection(
    username: String,
    profileViewModel: ProfileViewModel,
    onStartedPlanClick: (userTrainingPlanId: String, planName: String) -> Unit,
    isOwnProfile: Boolean
) {
    val uiState by profileViewModel.uiState.collectAsState()
    val startedPlans = uiState.startedPlans
    val isLoading = uiState.isLoading

    LaunchedEffect(username) {
        if (!isOwnProfile) {
            profileViewModel.loadUserStartedPlans(username)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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
            } else if (startedPlans.isEmpty()) {
                EmptySectionMessage(
                    message = if (isOwnProfile)
                        "Nie masz jeszcze żadnych rozpoczętych treningów"
                    else
                        "$username nie ma jeszcze żadnych rozpoczętych treningów",
                    icon = Icons.Default.FitnessCenter
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    startedPlans.forEach { plan ->
                        StartedPlanCompactCard(
                            plan = plan,
                            onClick = if (isOwnProfile && plan.completedDays < plan.totalDays) {
                                { onStartedPlanClick(plan.id, plan.name) }
                            } else null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StartedPlanCompactCard(
    plan: StartedTrainingPlan,
    onClick: (() -> Unit)?
) {
    val isComplete = plan.completedDays >= plan.totalDays
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
            .height(140.dp)
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
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(116.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = plan.image,
                    contentDescription = plan.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = contentAlpha
                )

                if (!isComplete && onClick != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                getPrimaryColor().copy(alpha = 0.3f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Kontynuuj",
                            tint = getOnPrimaryColor(),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = plan.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = getOnSurfaceColor().copy(alpha = contentAlpha),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Autor: ${plan.author.username}",
                        fontSize = 11.sp,
                        color = getOnSurfaceSecondaryColor().copy(alpha = contentAlpha),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val progress = if (plan.totalDays > 0) {
                        plan.completedDays.toFloat() / plan.totalDays.toFloat()
                    } else 0f

                    Text(
                        text = if (isComplete) "Ukończono" else "${plan.completedDays}/${plan.totalDays} dni",
                        fontSize = 10.sp,
                        color = if (isComplete) getPrimaryColor() else getOnSurfaceSecondaryColor().copy(alpha = contentAlpha),
                        fontWeight = if (isComplete) FontWeight.SemiBold else FontWeight.Normal
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
            }
        }
    }
}