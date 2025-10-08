package com.example.gymeaze.ui.screens.profile.edit.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.gymeaze.R
import com.example.gymeaze.data.models.Badge
import com.example.gymeaze.ui.theme.*

@Composable
fun BadgeSelectionDialog(
    availableBadges: List<Badge>,
    currentlySelectedBadges: List<Badge>,
    onDismiss: () -> Unit,
    onConfirm: (List<Badge>) -> Unit,
    isLoading: Boolean = false
) {
    var selectedBadgeIds by remember(currentlySelectedBadges) {
        mutableStateOf(currentlySelectedBadges.map { it.id }.toSet())
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = getCardBackgroundColor(),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.select_badges_title),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor()
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
                            tint = getOnSurfaceSecondaryColor()
                        )
                    }
                }

                if (isLoading || availableBadges.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.badges_selection_limit, selectedBadgeIds.size),
                        fontSize = 14.sp,
                        color = getOnSurfaceSecondaryColor(),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = getPrimaryColor()
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = stringResource(R.string.loading_badges),
                                    fontSize = 16.sp,
                                    color = getOnSurfaceSecondaryColor(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    availableBadges.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = stringResource(R.string.no_badges_available),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = getOnSurfaceColor(),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.earn_badges_message),
                                    fontSize = 14.sp,
                                    color = getOnSurfaceSecondaryColor(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(count = availableBadges.size) { index ->
                                val badge = availableBadges[index]
                                val isSelected = selectedBadgeIds.contains(badge.id)
                                val canSelect = selectedBadgeIds.size < 5 || isSelected

                                BadgeSelectionItem(
                                    badge = badge,
                                    isSelected = isSelected,
                                    onSelectionChange = { shouldBeSelected ->
                                        if (shouldBeSelected) {
                                            if (selectedBadgeIds.size < 5) {
                                                selectedBadgeIds = selectedBadgeIds + badge.id
                                            }
                                        } else {
                                            selectedBadgeIds = selectedBadgeIds - badge.id
                                        }
                                    },
                                    canSelect = canSelect
                                )
                            }
                        }
                    }
                }

                if (isLoading || availableBadges.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = getOnSurfaceColor()
                            ),
                            border = BorderStroke(1.dp, getInputBorderColor())
                        ) {
                            Text(stringResource(R.string.cancel))
                        }

                        Button(
                            onClick = {
                                val selectedBadges = availableBadges.filter { badge ->
                                    selectedBadgeIds.contains(badge.id)
                                }
                                onConfirm(selectedBadges)
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getPrimaryColor(),
                                contentColor = getOnPrimaryColor()
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = getOnPrimaryColor()
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.confirm_button),
                                    color = getOnPrimaryColor()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeSelectionItem(
    badge: Badge,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    canSelect: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = canSelect || isSelected) {
                onSelectionChange(!isSelected)
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) getPrimaryColor().copy(alpha = 0.1f)
            else getSurfaceColor()
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, getPrimaryColor())
        } else {
            BorderStroke(1.dp, getOnSurfaceSecondaryColor().copy(alpha = 0.2f))
        },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = badge.image,
                    contentDescription = badge.title,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = badge.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = badge.description,
                        fontSize = 14.sp,
                        color = getOnSurfaceSecondaryColor(),
                        lineHeight = 20.sp
                    )
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                getPrimaryColor(),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.selected),
                            tint = getOnPrimaryColor(),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .border(
                                2.dp,
                                if (canSelect) getOnSurfaceSecondaryColor().copy(alpha = 0.3f)
                                else getOnSurfaceSecondaryColor().copy(alpha = 0.1f),
                                RoundedCornerShape(8.dp)
                            )
                    )
                }
            }

            if (!canSelect && !isSelected) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            getOnSurfaceColor().copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        )
                )
            }
        }
    }
}