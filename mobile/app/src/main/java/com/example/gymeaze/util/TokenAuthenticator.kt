package com.example.gymeaze.util

import com.example.gymeaze.data.AuthRepository
import com.example.gymeaze.data.UserPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val userPreferences: UserPreferences,
    private val authRepository: AuthRepository
) : Authenticator{
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            runBlocking {
                authRepository.logout()
            }
            return null
        }

        return runBlocking {
            val refreshResult = authRepository.refreshAccessToken()

            if (refreshResult.isSuccess) {
                val newToken = refreshResult.getOrNull()
                response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            } else {
                authRepository.logout()
                null
            }
        }
    }


    private fun responseCount(response: Response): Int{
        var result = 1
        var currentResponse = response.priorResponse
        while(currentResponse != null) {
            result++
            currentResponse = currentResponse.priorResponse
        }
        return result
    }
}