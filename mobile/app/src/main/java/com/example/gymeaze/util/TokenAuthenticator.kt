package com.example.gymeaze.util

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
        private const val MAX_RETRY_COUNT = 2
    }

    override fun authenticate(route: Route?, response: Response): Request? {

        if (responseCount(response) >= MAX_RETRY_COUNT) {
            runBlocking {
                authRepository.logout()
            }
            return null
        }

        if (response.code != 401 && response.code != 403) {
            return null
        }

        return runBlocking {
            try {
                val refreshResult = authRepository.refreshAccessToken()

                if (refreshResult.isSuccess) {
                    val newToken = refreshResult.getOrNull()

                    if (!newToken.isNullOrEmpty()) {
                        userPreferences.saveAccessToken(newToken)

                        return@runBlocking response.request.newBuilder()
                            .header("Authorization", "Bearer $newToken")
                            .build()
                    } else {
                        authRepository.logout()
                        return@runBlocking null
                    }
                } else {
                    val error = refreshResult.exceptionOrNull()
                    authRepository.logout()
                    return@runBlocking null
                }
            } catch (e: Exception) {
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
        return result
    }
}