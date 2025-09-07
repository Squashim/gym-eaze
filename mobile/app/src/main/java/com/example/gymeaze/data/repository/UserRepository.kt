package com.example.gymeaze.data.repository

import android.content.Context
import com.example.gymeaze.BuildConfig
import com.example.gymeaze.data.UserPreferences
import com.example.gymeaze.data.api.UserApiService
import com.example.gymeaze.data.models.UserProfileDetails
import com.example.gymeaze.util.AuthErrorMapper
import com.example.gymeaze.util.AuthInterceptor
import com.example.gymeaze.util.TokenAuthenticator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException

class UserRepository(private val context: Context) {

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

    private val api: UserApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApiService::class.java)
    }

    suspend fun getUserProfileDetails(): Result<UserProfileDetails> = withContext(Dispatchers.IO) {
        try {
            val response = api.getUserProfileDetails()
            val body = response.body()

            return@withContext if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                val rawError = response.errorBody()?.string().orEmpty()
                val message = AuthErrorMapper.mapAuthErrorToUserMessage(context, rawError, response.code())
                Result.failure(IllegalStateException(message))
            }

        } catch (e: IOException) {
            Result.failure(IllegalStateException("Błąd sieci: ${e.message}", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
