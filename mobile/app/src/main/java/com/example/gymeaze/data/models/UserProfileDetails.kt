package com.example.gymeaze.data.models

import com.google.gson.annotations.SerializedName

data class UserProfileDetails(
    @SerializedName("_id")
    val id: String = "",
    val username: String = "",
    val profilePicture: String = "",
    val description: String = "",
    val followersCount: Int? = null,
    val followingCount: Int? = null,
    val isBlocked: Boolean? = null,
    val isFollowing: Boolean = false,
    val goal: String? = null,
    val personalDetails: PersonalDetails? = null,
    val fitnessDetails: FitnessDetails? = null,
    val pinnedBadges: List<Badge> = emptyList(),
    val isBadgesDetailsPublic: Boolean = false,
    val isGoalPublic: Boolean = false
)
data class PersonalDetails(
    val firstName: String = "",
    val lastName: String = "",
    val country: String = ""
)

data class FitnessDetails(
    val height: Int? = null,
    val weight: Int? = null,
    val age: Int? = null,
    val gender: String? = null
)


data class Badge(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val image: String = "",
    val awardedAt: String? = null
)

data class UpdateProfileResponse(
    val message: String
)

data class UpdateLoginRequest(
     val username: String?,
     val password: String?
)

data class UpdateLoginResponse(
    val message: String,
    val error: String? = null
)

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

data class FollowResponse(
    val message: String,
    val isFollowing: Boolean,
    val followersCount: Int,
    val followingCount: Int
)
