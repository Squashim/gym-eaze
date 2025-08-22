package com.example.gymeaze.data.api

import com.example.gymeaze.data.models.AuthResponse
import com.example.gymeaze.data.models.GoogleAuthRequest
import com.example.gymeaze.data.models.LoginRequest
import com.example.gymeaze.data.models.LogoutRequest
import com.example.gymeaze.data.models.MessageResponse
import com.example.gymeaze.data.models.RefreshResponse
import com.example.gymeaze.data.models.RefreshTokenRequest
import com.example.gymeaze.data.models.RegisterRequest
import com.example.gymeaze.data.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<MessageResponse>
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("/api/auth/google")
    suspend fun googleAuth(@Body request: GoogleAuthRequest): Response<AuthResponse>

    @POST("/api/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshResponse>
    @POST("/api/auth/logout")
    suspend fun logout(@Body request: LogoutRequest): Response<MessageResponse>

    @GET("/api/auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<User>

    //z cookies
    @GET("/api/auth/me")
    suspend fun getCurrentUserWithCookie(): Response<User>

    @POST("/api/auth/link/google")
    suspend fun linkGoogleAccount(@Body request: GoogleAuthRequest): Response<AuthResponse>
}