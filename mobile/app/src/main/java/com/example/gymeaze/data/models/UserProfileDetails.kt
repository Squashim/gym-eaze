package com.example.gymeaze.data.models


data class UserProfileDetails(
    val profilePicture: String = "",
    val description: String = "",
    val goal: String = "",
    val pinnedBadges: List<Badge> = emptyList(),
    val isBadgesDetailsPublic: Boolean = false,
    val isGoalPublic: Boolean = false
)

data class Badge(
    val title: String = "",
    val description: String = "",
    val image: String = ""
)

