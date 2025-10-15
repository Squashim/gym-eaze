package com.example.gymeaze.data.api

import com.example.gymeaze.data.models.Badge
import com.example.gymeaze.data.models.FavoritesResponse
import com.example.gymeaze.data.models.GetPrivacySettingsResponse
import com.example.gymeaze.data.models.UpdateFitnessDataRequest
import com.example.gymeaze.data.models.UpdateFitnessDataResponse
import com.example.gymeaze.data.models.UpdateLoginRequest
import com.example.gymeaze.data.models.UpdateLoginResponse
import com.example.gymeaze.data.models.UpdatePersonalDataRequest
import com.example.gymeaze.data.models.UpdatePersonalDataResponse
import com.example.gymeaze.data.models.UpdatePrivacySettingsRequest
import com.example.gymeaze.data.models.UpdatePrivacySettingsResponse
import com.example.gymeaze.data.models.UpdateProfileResponse
import com.example.gymeaze.data.models.UserFavorites
import com.example.gymeaze.data.models.UserFitnessData
import com.example.gymeaze.data.models.UserPersonalData
import com.example.gymeaze.data.models.UserProfileDetails
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part

interface UserApiService {

    @GET("/api/user/profile/profile-details")
    suspend fun getUserProfileDetails(): Response<UserProfileDetails>

    @Multipart
    @PATCH("/api/user/profile/profile-details")
    suspend fun updateUserProfileDetails(
        @Part("description") description: RequestBody,
        @Part("goal") goal: RequestBody?,
        @Part pinnedBadges: List<MultipartBody.Part>,
        @Part profilePicture: MultipartBody.Part?
    ): Response<UpdateProfileResponse>

    @GET("/api/user/badges")
    suspend fun getUserBadges(): Response<List<Badge>>


    @PATCH("/api/user/login-details")
    suspend fun updateLoginData(
        @Body updateLoginRequest: UpdateLoginRequest
    ): Response<UpdateLoginResponse>

    @DELETE("/api/user/")
    suspend fun deleteAccount(): Response<Unit>

    @GET("/api/user/profile/personal-details")
    suspend fun getUserPersonalData(): Response<UserPersonalData>

    @PATCH("/api/user/profile/personal-details")
    suspend fun updateUserPersonalData(
        @Body personalData: UpdatePersonalDataRequest
    ): Response<UpdatePersonalDataResponse>

    @GET("/api/user/profile/fitness-details")
    suspend fun getUserFitnessData(): Response<UserFitnessData>

    @PATCH("/api/user/profile/fitness-details")
    suspend fun updateUserFitnessData(
        @Body fitnessData: UpdateFitnessDataRequest
    ): Response<UpdateFitnessDataResponse>

    @GET("/api/user/privacy-settings")
    suspend fun getPrivacySettings(): Response<GetPrivacySettingsResponse>

    @PATCH("/api/user/privacy-settings")
    suspend fun updatePrivacySettings(
        @Body privacySettings: UpdatePrivacySettingsRequest
    ): Response<UpdatePrivacySettingsResponse>

    @GET("/api/user/favorites")
    suspend fun getUserFavorites(): Response<FavoritesResponse>
}