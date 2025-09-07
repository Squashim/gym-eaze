package com.example.gymeaze.util

import android.util.Log
import com.example.gymeaze.data.repository.AuthRepository
import com.example.gymeaze.data.UserPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val userPreferences: UserPreferences,
    private val authRepository: AuthRepository
) : Authenticator {

    companion object {
        private const val TAG = "TokenAuthenticator"
        private const val MAX_RETRY_COUNT = 2
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d(TAG, "authenticate() called for response code: ${response.code}")

        // Sprawdzamy czy to nie jest zbyt dużo prób
        if (responseCount(response) >= MAX_RETRY_COUNT) {
            Log.d(TAG, "Max retry count reached, logging out")
            runBlocking {
                authRepository.logout()
            }
            return null
        }

        // Sprawdzamy czy to faktycznie błąd autoryzacji
        if (response.code != 401 && response.code != 403) {
            Log.d(TAG, "Response code ${response.code} is not auth error, skipping token refresh")
            return null
        }

        return runBlocking {
            try {
                Log.d(TAG, "Attempting to refresh access token")
                val refreshResult = authRepository.refreshAccessToken()

                if (refreshResult.isSuccess) {
                    val newToken = refreshResult.getOrNull()
                    Log.d(TAG, "Token refresh successful, new token: ${newToken?.take(20)}...")

                    if (!newToken.isNullOrEmpty()) {
                        // Zapisujemy nowy token
                        userPreferences.saveAccessToken(newToken)
                        Log.d(TAG, "New token saved, creating new request")

                        // Tworzymy nowe żądanie z nowym tokenem
                        return@runBlocking response.request.newBuilder()
                            .header("Authorization", "Bearer $newToken")
                            .build()
                    } else {
                        Log.e(TAG, "New token is null or empty")
                        authRepository.logout()
                        return@runBlocking null
                    }
                } else {
                    val error = refreshResult.exceptionOrNull()
                    Log.e(TAG, "Token refresh failed: ${error?.message}")
                    authRepository.logout()
                    return@runBlocking null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during token refresh", e)
                runBlocking {
                    authRepository.logout()
                }
                return@runBlocking null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var currentResponse = response.priorResponse
        while (currentResponse != null) {
            result++
            currentResponse = currentResponse.priorResponse
        }
        Log.d(TAG, "Response count: $result")
        return result
    }
}