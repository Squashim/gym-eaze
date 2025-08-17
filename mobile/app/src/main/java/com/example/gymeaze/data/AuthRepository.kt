package com.example.gymeaze.data

import android.content.Context

class AuthRepository(private val context: Context){
    private val userPreferences = UserPreferences(context)

    // TODO backend

    suspend fun register(username: String, email: String, password: String): Result<Boolean> {
        return try {
            // TODO API do rejestracji
            // val response = apiService.register(username, email, password)

            // symulacja
            userPreferences.setUsername(username)
            userPreferences.setEmail(email)
            userPreferences.setLoggedIn(true)
            userPreferences.setFirstLogin(true)

            Result.success(true)
        } catch(e: Exception){
            Result.failure(e)
        }
    }

    // TODO backend

    suspend fun login(usernameOrEmail: String, password: String): Result<Boolean> {
        return try{
            // TODO API do logowania
            // val response = apiService.login(usernameOrEmail, password)

            // TODO czy kiedys sie logowal
            // val userExists = apiService.checkUserExists(usernameOrEmail)

            // symulacja

            userPreferences.setEmail(usernameOrEmail)
            userPreferences.setLoggedIn(true)

            // TODO: Sprawdzenie z backendu czy to pierwszy login
            // userPreferences.setFirstLogin(!userExists)
            userPreferences.setFirstLogin(false) //symulacja false

            Result.success(true)
        } catch(e: Exception){
            Result.failure(e)
        }
    }

    // TODO backend Google Sign-In
    suspend fun loginWithGoogle(): Result<Boolean>{
        return try{
            // TODO: logowanie przez Google

            Result.success(true)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun logout() {
        userPreferences.clearData()
    }

    fun getPreferences() = userPreferences
}