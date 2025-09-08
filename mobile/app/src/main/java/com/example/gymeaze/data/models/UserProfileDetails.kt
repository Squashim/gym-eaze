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
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val image: String = ""
)

data class UpdateProfileResponse(
    val message: String
)
