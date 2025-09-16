package com.example.gymeaze.util

import com.example.gymeaze.data.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val userPreferences: UserPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()


        if (originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest)
        }

        val token = runBlocking {
            userPreferences.accessToken.first()
        }

        val newRequest = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}