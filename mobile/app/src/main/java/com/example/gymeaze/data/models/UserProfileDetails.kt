package com.example.gymeaze.data.models

import com.google.gson.annotations.SerializedName


// Profile Info
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

// Login Details
data class UpdateLoginRequest(
     val username: String?,
     val password: String?
)

data class UpdateLoginResponse(
    val message: String,
    val error: String? = null
)

// Personal Details
data class UserPersonalData(
    val firstName: String,
    val lastName: String,
    val country: String?,
    val isPersonalDetailsPublic: Boolean
)

data class UpdatePersonalDataRequest(
    val firstName: String,
    val lastName: String,
    val country: String?
)

data class UpdatePersonalDataResponse(
    val message: String,
    val data: UserPersonalData?
)

// Fitness Details
data class UserFitnessData(
    val gender: String?,
    val age: Int?,
    val height: Int?,
    val weight: Int?,
    val isFitnessDetailsPublic: Boolean = true
)

data class UpdateFitnessDataRequest(
    val gender: String?,
    val age: Int?,
    val height: Int?,
    val weight: Int?
)

data class UpdateFitnessDataResponse(
    val message: String,
    val data: UserFitnessData
)

// Privacy Settings

data class PrivacySettings(
    val isPersonalDetailsPublic: Boolean = true,
    val isFitnessDetailsPublic: Boolean = true,
    val isGoalPublic: Boolean = true,
    val isBadgesDetailsPublic: Boolean = true,
    val isTrainingStatisticsPublic: Boolean = true
)

data class GetPrivacySettingsResponse(
    val message: String,
    val data: PrivacySettings
)

data class UpdatePrivacySettingsRequest(
    val privacySettings: PrivacySettings
)

data class UpdatePrivacySettingsResponse(
    val message: String
)

data class FavoritesResponse(
    val exercises: List<String>
)

data class FollowerUser(
    val _id: String,
    val username: String,
    val profileDetails: ProfileDetails?
)
