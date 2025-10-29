package com.example.gymeaze.data.repository

import android.content.Context
import com.example.gymeaze.BuildConfig
import com.example.gymeaze.data.UserPreferences
import com.example.gymeaze.data.api.StartedTrainingApiService
import com.example.gymeaze.data.api.TrainingPlanApiService
import com.example.gymeaze.data.api.UserApiService
import com.example.gymeaze.data.models.*
import com.example.gymeaze.util.AuthErrorMapper
import com.example.gymeaze.util.AuthInterceptor
import com.example.gymeaze.util.TokenAuthenticator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StartedTrainingRepository(private val context: Context) {

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

    private val api: StartedTrainingApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StartedTrainingApiService::class.java)
    }

    suspend fun getStartedTrainingPlans(userId: String, page: Int, pageSize: Int): Result<StartedTrainingPlansResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getStartedTrainingPlans(userId,page, pageSize)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val raw = response.errorBody()?.string()
                val msg = AuthErrorMapper.mapAuthErrorToUserMessage(context, raw, response.code())
                Result.failure(IllegalStateException(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserStartedPlans(username: String): List<StartedTrainingPlan> = withContext(Dispatchers.IO) {
        try {
            val trainingPlanRepo = TrainingPlanRepository(context)
            val userIdResult = trainingPlanRepo.getUserIdByUsername(username)

            userIdResult.fold(
                onSuccess = { userId ->
                    val result = getStartedTrainingPlans(userId, 1, 10)
                    if (result.isSuccess) {
                        result.getOrNull()?.items ?: emptyList()
                    } else {
                        emptyList()
                    }
                },
                onFailure = {
                    emptyList()
                }
            )
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun startTrainingPlan(trainingPlanId: String): Result<StartTrainingResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.startTrainingPlan(trainingPlanId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val raw = response.errorBody()?.string()
                val msg = AuthErrorMapper.mapAuthErrorToUserMessage(context, raw, response.code())
                Result.failure(IllegalStateException(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStartedTrainingPlanDays(userTrainingPlanId: String): Result<StartedTrainingPlanDaysResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getStartedTrainingPlanDays(userTrainingPlanId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val raw = response.errorBody()?.string()
                val msg = AuthErrorMapper.mapAuthErrorToUserMessage(context, raw, response.code())
                Result.failure(IllegalStateException(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStartedTrainingPlanDayDetails(userTrainingPlanId: String, dayNumber: Int): Result<StartedDayDetails> = withContext(Dispatchers.IO) {
        try {
            val response = api.getStartedTrainingPlanDayDetails(userTrainingPlanId, dayNumber)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val raw = response.errorBody()?.string()
                val msg = AuthErrorMapper.mapAuthErrorToUserMessage(context, raw, response.code())
                Result.failure(IllegalStateException(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeDayTraining(userTrainingPlanId: String, dayNumber: Int): Result<CompleteDayResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.completeDayTraining(userTrainingPlanId, dayNumber)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val raw = response.errorBody()?.string()
                val msg = AuthErrorMapper.mapAuthErrorToUserMessage(context, raw, response.code())
                Result.failure(IllegalStateException(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteStartedTrainingPlan(userTrainingPlanId: String): Result<MessageResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteStartedTrainingPlan(userTrainingPlanId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val raw = response.errorBody()?.string()
                val msg = AuthErrorMapper.mapAuthErrorToUserMessage(context, raw, response.code())
                Result.failure(IllegalStateException(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetStartedTrainingPlan(userTrainingPlanId: String): Result<MessageResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.resetStartedTrainingPlan(userTrainingPlanId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val raw = response.errorBody()?.string()
                val msg = AuthErrorMapper.mapAuthErrorToUserMessage(context, raw, response.code())
                Result.failure(IllegalStateException(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}