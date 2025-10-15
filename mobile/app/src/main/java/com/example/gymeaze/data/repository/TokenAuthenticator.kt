package com.example.gymeaze.data.api

import com.example.gymeaze.data.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val authRepository: AuthRepository
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshResult = runBlocking {
            authRepository.refreshAccessToken()
        }

        if (refreshResult.isFailure) {
            runBlocking { authRepository.logout() }
            return null
        }

        val newAccessToken = refreshResult.getOrNull() ?: return null

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()
    }
}
