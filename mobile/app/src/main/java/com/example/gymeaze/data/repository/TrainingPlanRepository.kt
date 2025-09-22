package com.example.gymeaze.data.repository

import android.content.Context
import android.net.Uri
import com.example.gymeaze.BuildConfig
import com.example.gymeaze.data.UserPreferences
import com.example.gymeaze.data.api.TrainingPlanApiService
import com.example.gymeaze.data.models.*
import com.example.gymeaze.util.AuthErrorMapper
import com.example.gymeaze.util.AuthInterceptor
import com.example.gymeaze.util.TokenAuthenticator
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream

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

    private val MAX_IMAGE_BYTES = 5L * 1024L * 1024L // 5MB

    suspend fun createTrainingPlan(
        request: CreateTrainingPlanRequest,
        imageUri: Uri
    ): Result<TrainingPlan> = withContext(Dispatchers.IO) {
        try {
            // Validate image
            val imageSuffix = guessSuffix(imageUri)
            if (imageSuffix !in listOf(".png", ".webp", ".jpg", ".jpeg")) {
                return@withContext Result.failure(
                    IllegalArgumentException("Dozwolone formaty obrazów: PNG, WEBP, JPG, JPEG")
                )
            }

            // Convert Uri to file
            val imageFile = uriToTempFile(imageUri, "training_plan_image", imageSuffix)

            if (imageFile.length() > MAX_IMAGE_BYTES) {
                imageFile.delete()
                return@withContext Result.failure(
                    IllegalArgumentException("Zdjęcie jest zbyt duże (maksymalnie 5MB)")
                )
            }

            // Prepare form data
            val text = "text/plain".toMediaType()
            val json = "application/json".toMediaType()

            val titleBody = request.title.toRequestBody(text)
            val descriptionBody = (request.description ?: "").toRequestBody(text)
            val goalBody = request.goal.toRequestBody(text)
            val visibilityBody = request.visibility.toRequestBody(text)
            val advancedLevelBody = request.advancedLevel.toRequestBody(text)
            val hashtagsBody = Gson().toJson(request.hashtags).toRequestBody(json)
            val daysBody = Gson().toJson(request.days).toRequestBody(json)

            // Prepare image part
            val imageMime = when (imageSuffix) {
                ".png" -> "image/png"
                ".jpg", ".jpeg" -> "image/jpeg"
                ".webp" -> "image/webp"
                else -> "application/octet-stream"
            }

            val imagePart = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                imageFile.asRequestBody(imageMime.toMediaType())
            )

            // Make API call
            val response = api.createTrainingPlan(
                title = titleBody,
                description = descriptionBody,
                goal = goalBody,
                visibility = visibilityBody,
                advancedLevel = advancedLevelBody,
                hashtags = hashtagsBody,
                days = daysBody,
                image = imagePart
            )

            // Cleanup temp file
            imageFile.delete()

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

    suspend fun getTrainingPlans(
        page: Int = 1,
        pageSize: Int = 9,
        sortBy: String = "latest"
    ): Result<TrainingPlansResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTrainingPlans(
                page = page,
                pageSize = pageSize,
                sortBy = sortBy
            )

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

    suspend fun getTrainingPlanById(id: String): Result<TrainingPlan> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTrainingPlanById(id)

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

    suspend fun toggleTrainingPlanLike(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.toggleTrainingPlanLike(id)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val raw = response.errorBody()?.string()
                val msg = AuthErrorMapper.mapAuthErrorToUserMessage(context, raw, response.code())
                Result.failure(IllegalStateException(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun uriToTempFile(uri: Uri, prefix: String, suffix: String): File {
        val input = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Nie można odczytać pliku")

        val file = File.createTempFile(prefix, suffix, context.cacheDir)
        FileOutputStream(file).use { out ->
            input.use { inputStream ->
                inputStream.copyTo(out)
            }
        }
        return file
    }

    private fun guessSuffix(uri: Uri): String {
        val type = context.contentResolver.getType(uri) ?: return ".bin"
        return when {
            type.startsWith("image/png") -> ".png"
            type.startsWith("image/jpeg") -> ".jpg"
            type.startsWith("image/webp") -> ".webp"
            else -> ".bin"
        }
    }
}