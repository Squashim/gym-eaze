package com.example.gymeaze.data.repository

import android.content.Context
import com.example.gymeaze.BuildConfig
import com.example.gymeaze.data.UserPreferences
import com.example.gymeaze.data.api.ReportsApiService
import com.example.gymeaze.data.models.CreateReportRequest
import com.example.gymeaze.data.models.ReportResponse
import com.example.gymeaze.util.AuthInterceptor
import com.example.gymeaze.util.TokenAuthenticator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReportRepository(private val context: Context) {

    private val userPreferences = UserPreferences(context)
    private val authRepository = AuthRepository(context)

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(AuthInterceptor(userPreferences))
        .authenticator(TokenAuthenticator(userPreferences, authRepository))
        .build()
    private val api: ReportsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReportsApiService::class.java)
    }

    suspend fun reportItem(
        itemId: String,
        itemType: String,
        reportType: String,
        reason: String? = null
    ): ReportResponse? {
        return try {
            val request = CreateReportRequest(
                reportedItemId = itemId,
                reportedItemType = itemType,
                reportType = reportType,
                reason = reason
            )
            val response = api.createReport(request)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }
}
