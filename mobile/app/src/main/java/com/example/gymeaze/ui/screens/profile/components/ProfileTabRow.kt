package com.example.gymeaze.ui.screens.profile.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset


import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.gymeaze.R

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
                text = stringResource(R.string.posts),
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
                text = stringResource(R.string.exercises),
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
                text = stringResource(R.string.plans2),
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
                text = stringResource(R.string.active),
                fontSize = 14.sp,
                fontWeight = if (selectedTab == ProfileTab.STARTED_TRAINING_PLANS) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedTab == ProfileTab.STARTED_TRAINING_PLANS) getPrimaryColor() else getOnSurfaceSecondaryColor()
            )
        }
    }
}

