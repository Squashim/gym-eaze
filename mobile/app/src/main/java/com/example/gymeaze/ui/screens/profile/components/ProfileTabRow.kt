package com.example.gymeaze.ui.screens.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset


import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.ui.theme.*

enum class ProfileTab {
    POSTS,
    EXERCISES,
    TRAINING_PLANS,
    STARTED_TRAINING_PLANS
}

@Composable
fun ProfileTabRow(
    selectedTab: ProfileTab,
    onTabSelected: (ProfileTab) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = Modifier
            .fillMaxWidth()
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
            selected = selectedTab == ProfileTab.POSTS,
            onClick = { onTabSelected(ProfileTab.POSTS) },
            modifier = Modifier.height(48.dp)
        ) {
            Text(
                text = "Posty",
                fontSize = 14.sp,
                fontWeight = if (selectedTab == ProfileTab.POSTS) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedTab == ProfileTab.POSTS) getPrimaryColor() else getOnSurfaceSecondaryColor()
            )
        }

        Tab(
            selected = selectedTab == ProfileTab.EXERCISES,
            onClick = { onTabSelected(ProfileTab.EXERCISES) },
            modifier = Modifier.height(48.dp)
        ) {
            Text(
                text = "Ćwiczenia",
                fontSize = 14.sp,
                fontWeight = if (selectedTab == ProfileTab.EXERCISES) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedTab == ProfileTab.EXERCISES) getPrimaryColor() else getOnSurfaceSecondaryColor()
            )
        }

        Tab(
            selected = selectedTab == ProfileTab.TRAINING_PLANS,
            onClick = { onTabSelected(ProfileTab.TRAINING_PLANS) },
            modifier = Modifier.height(48.dp)
        ) {
            Text(
                text = "Plany",
                fontSize = 14.sp,
                fontWeight = if (selectedTab == ProfileTab.TRAINING_PLANS) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedTab == ProfileTab.TRAINING_PLANS) getPrimaryColor() else getOnSurfaceSecondaryColor()
            )
        }

        Tab(
            selected = selectedTab == ProfileTab.STARTED_TRAINING_PLANS,
            onClick = { onTabSelected(ProfileTab.STARTED_TRAINING_PLANS) },
            modifier = Modifier.height(48.dp)
        ) {
            Text(
                text = "Aktywne",
                fontSize = 14.sp,
                fontWeight = if (selectedTab == ProfileTab.STARTED_TRAINING_PLANS) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedTab == ProfileTab.STARTED_TRAINING_PLANS) getPrimaryColor() else getOnSurfaceSecondaryColor()
            )
        }
    }
}

@Composable
fun ProfileTabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) getPrimaryColor().copy(alpha = 0.15f) else Color.Transparent
    val contentColor = if (isSelected) getPrimaryColor() else getOnSurfaceSecondaryColor()

    Card(
        modifier = modifier.padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}