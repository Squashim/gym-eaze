package com.example.gymeaze.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymeaze.R
import com.example.gymeaze.ui.theme.GymEazeTheme
import com.example.gymeaze.ui.theme.getNavBarBackgroundColor
import com.example.gymeaze.ui.theme.getNavBarSelectedColor
import com.example.gymeaze.ui.theme.getNavBarUnselectedColor

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
        BottomNavItem("home", Icons.Default.Home, stringResource(R.string.nav_home)),
        BottomNavItem("workouts", Icons.Outlined.FitnessCenter, stringResource(R.string.nav_workouts)),
        BottomNavItem("community", Icons.Outlined.People, stringResource(R.string.nav_community)),
        BottomNavItem("profile", Icons.Default.Person, stringResource(R.string.nav_profile))
    )

    NavigationBar(
        containerColor = getNavBarBackgroundColor()
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon ={
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                selected = selectedRoute == item.route,
                onClick = { onItemSelected(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = getNavBarSelectedColor(),
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