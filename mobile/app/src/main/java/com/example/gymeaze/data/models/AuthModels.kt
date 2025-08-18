package com.example.gymeaze.data.models

data class User(
    val _id: String,
    val username: String,
    val email: String,
    val role: String,
    val profilePicture: String? = null,
    val createdAt: String,
    val updatedAt: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val login: String,
    val password: String
)

data class GoogleAuthRequest(
    val token: String,
)

data class RefreshTokenRequest(
    val token: String
)

data class LogoutRequest(
    val token: String
)

data class AuthResponse(
    val message: String,
    val accessToken: String,
    val refreshToken: String
)

data class MessageResponse(
    val message: String
)

data class RefreshResponse(
    val accessToken: String
)

data class ErrorResponse(
    val error: String,
    val message: String
)