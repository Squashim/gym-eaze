package com.example.gymeaze.data.api

import com.example.gymeaze.data.models.Badge
import com.example.gymeaze.data.models.UpdateProfileResponse
import com.example.gymeaze.data.models.UserProfileDetails
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part

interface UserApiService {

    @GET("/api/user/profile/profileDetails")
    suspend fun getUserProfileDetails(): Response<UserProfileDetails>

    @Multipart
    @PATCH("/api/user/profile/profileDetails")
    suspend fun updateUserProfileDetails(
        @Part("description") description: RequestBody,
        @Part("goal") goal: RequestBody?,
        @Part pinnedBadges: List<MultipartBody.Part>,
        @Part profilePicture: MultipartBody.Part?
    ): Response<UpdateProfileResponse>

    @GET("/api/user/badges")
    suspend fun getUserBadges(): Response<List<Badge>>

}