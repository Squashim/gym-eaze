package com.example.gymeaze.data.repository

import android.content.Context
import android.net.Uri
import com.example.gymeaze.BuildConfig
import com.example.gymeaze.R
import com.example.gymeaze.data.UserPreferences
import com.example.gymeaze.data.api.TrainingPlanApiService
import com.example.gymeaze.util.AuthErrorMapper
import com.example.gymeaze.util.AuthInterceptor
import com.example.gymeaze.util.TokenAuthenticator
import com.example.gymeaze.viewmodel.TrainingDay
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class TrainingPlanRepository(private val context: Context) {

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

    private val api: TrainingPlanApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrainingPlanApiService::class.java)
    }

    suspend fun createTrainingPlan(
        name: String,
        description: String,
        goal: String,
        visibility: String,
        advancedLevel: String,
        hashtags: List<String>,
        imageUri: Uri,
        days: List<TrainingDay>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val imageFile = uriToTempFile(imageUri, "training_plan_image", ".jpg")

            if (imageFile.length() > 5L * 1024L * 1024L) {
                imageFile.delete()
                return@withContext Result.failure(
                    IllegalArgumentException(context.getString(R.string.error_max_image_size))
                )
            }

            val text = "text/plain".toMediaType()
            val json = "application/json".toMediaType()

            val namePart = name.toRequestBody(text)
            val descriptionPart = description.toRequestBody(text)
            val goalPart = goal.toRequestBody(text)
            val visibilityPart = visibility.toRequestBody(text)
            val advancedLevelPart = advancedLevel.toRequestBody(text)
            val hashtagsPart = Gson().toJson(hashtags).toRequestBody(json)

            val backendDays = days.mapIndexed { index, day ->
                mapOf(
                    "name" to day.name,
                    "dayNumber" to (index + 1),
                    "isRestDay" to day.isRestDay,
                    "exercises" to if (day.isRestDay) {
                        emptyList<Map<String, Any>>()
                    } else {
                        day.exercises.map { exercise ->
                            mapOf(
                                "id" to exercise.exercise.id,
                                "isRepsSet" to exercise.isRepsSet,
                                "restAfterSet" to exercise.restAfterSet,
                                "sets" to exercise.sets.map { set ->
                                    val setMap = mutableMapOf<String, Any>()
                                    if (set.isRepsSet && set.repetitions != null) {
                                        setMap["repetitions"] = set.repetitions
                                        if (set.weight != null) {
                                            setMap["weight"] = set.weight
                                        }
                                    } else if (set.isDurationSet && set.duration != null) {
                                        setMap["duration"] = set.duration
                                    }
                                    setMap
                                }
                            )
                        }
                    }
                )
            }

            val daysPart = Gson().toJson(backendDays).toRequestBody(json)

            val imagePart = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )

            val response = api.createTrainingPlan(
                name = namePart,
                description = descriptionPart,
                goal = goalPart,
                visibility = visibilityPart,
                advancedLevel = advancedLevelPart,
                hashtags = hashtagsPart,
                days = daysPart,
                image = imagePart
            )

            imageFile.delete()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMessage = AuthErrorMapper.mapAuthErrorToUserMessage(
                    context,
                    response.errorBody()?.string(),
                    response.code()
                )
                Result.failure(IllegalStateException(errorMessage))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun uriToTempFile(uri: Uri, prefix: String, suffix: String): File {
        val input: InputStream? = context.contentResolver.openInputStream(uri)
        val file = File.createTempFile(prefix, suffix, context.cacheDir)
        FileOutputStream(file).use { out ->
            input?.use { inputStream -> inputStream.copyTo(out) }
        }
        return file
    }
}