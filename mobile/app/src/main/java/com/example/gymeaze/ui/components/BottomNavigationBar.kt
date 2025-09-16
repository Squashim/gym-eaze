package com.example.gymeaze.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymeaze.R
import com.example.gymeaze.ui.theme.GymEazeTheme
import com.example.gymeaze.ui.theme.getNavBarBackgroundColor
import com.example.gymeaze.ui.theme.getNavBarSelectedColor
import com.example.gymeaze.ui.theme.getNavBarUnselectedColor
import com.example.gymeaze.ui.theme.getOnPrimaryColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun GymEazeBottomNavigationBar(
    selectedRoute: String,
    onItemSelected: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem("exercises", Icons.Default.FitnessCenter, stringResource(R.string.nav_exercises)),
        BottomNavItem("workouts", Icons.Outlined.Assignment, stringResource(R.string.nav_workouts)),
        BottomNavItem("community", Icons.Outlined.People, stringResource(R.string.nav_community)),
        BottomNavItem("profile", Icons.Default.Person, stringResource(R.string.nav_profile))
    )

    NavigationBar(
        containerColor = getNavBarBackgroundColor()
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                        if (selectedRoute == item.route) {
                            Box(
                                Modifier
                                    .padding(top = 4.dp)
                                    .height(3.dp)
                                    .width(48.dp)
                                    .background(getOnSurfaceColor(), shape = RoundedCornerShape(1.5.dp))
                            )
                        }
                    }
                },
                selected = selectedRoute == item.route,
                onClick = { onItemSelected(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = getOnSurfaceColor(),
                    unselectedIconColor = getNavBarUnselectedColor(),
                    indicatorColor = getNavBarBackgroundColor()
                )
            )
        }
    }
}

@Preview
@Composable
fun GymEazeBottomNavigationBarPreview(){
    GymEazeTheme {
        GymEazeBottomNavigationBar(selectedRoute = "", onItemSelected = {})
    }
}