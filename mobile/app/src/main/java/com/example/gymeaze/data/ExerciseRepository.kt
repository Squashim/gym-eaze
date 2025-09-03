package com.example.gymeaze.data

import android.content.Context
import android.net.Uri
import com.example.gymeaze.BuildConfig
import com.example.gymeaze.R
import com.example.gymeaze.data.api.ExerciseApiService
import com.example.gymeaze.data.models.Equipment
import com.example.gymeaze.data.models.Hashtag
import com.example.gymeaze.data.models.MuscleGroup
import com.example.gymeaze.util.AuthErrorMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ExerciseRepository(private val context: Context) {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val api: ExerciseApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExerciseApiService::class.java)
    }

    private val userPreferences = UserPreferences(context)

    private val MAX_IMAGE_BYTES = 5L * 1024L * 1024L
    private val MAX_VIDEO_BYTES = 20L * 1024L * 1024L

    private fun mapDifficultyToBackend(d: String): String = when (d) {
        "easy" -> "easy"
        "medium" -> "medium"
        "hard" -> "hard"
        else -> "easy"
    }

    private fun mapVisibilityToBackend(isPublic: Boolean): String =
        if (isPublic) "public" else "private"

    suspend fun getMuscleGroups(): Result<List<MuscleGroup>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getMuscleGroups()
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

    suspend fun getEquipment(): Result<List<Equipment>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getEquipment()
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

    suspend fun getHashtags(): Result<List<Hashtag>> {
        return try {
            val response = api.getHashtags()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch hashtags"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPopularHashtags(limit: Int = 20): Result<List<Hashtag>> {
        return try {
            val response = api.getPopularHashtags(limit)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch popular hashtags"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createExercise(
        name: String,
        description: String,
        imageUri: Uri,
        videoUri: Uri,
        difficulty: String,
        isPublic: Boolean,
        muscleGroups: List<String>,
        equipment: List<String>,
        hashtags: List<String>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val accessToken = userPreferences.accessToken.first()
            if (accessToken.isNullOrBlank()) {
                return@withContext Result.failure(IllegalStateException(context.getString(R.string.unauthorized)))
            }

            val imageFile = uriToTempFile(imageUri, "exercise_image", guessSuffix(imageUri))
            val videoFile = uriToTempFile(videoUri, "exercise_video", guessSuffix(videoUri))

            if (imageFile.length() > MAX_IMAGE_BYTES) {
                imageFile.delete(); videoFile.delete()
                return@withContext Result.failure(IllegalArgumentException("Maksymalny rozmiar obrazu to 5MB"))
            }
            if (videoFile.length() > MAX_VIDEO_BYTES) {
                imageFile.delete(); videoFile.delete()
                return@withContext Result.failure(IllegalArgumentException("Maksymalny rozmiar filmiku to 20MB"))
            }

            val text = "text/plain".toMediaType()
            val nameBody: RequestBody = name.toRequestBody(text)
            val descriptionBody: RequestBody = description.toRequestBody(text)
            val advancedBody: RequestBody = mapDifficultyToBackend(difficulty).toRequestBody(text)
            val visibilityBody: RequestBody = mapVisibilityToBackend(isPublic).toRequestBody(text)

            val imagePart = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            )
            val videoPart = MultipartBody.Part.createFormData(
                "video",
                videoFile.name,
                videoFile.asRequestBody("video/*".toMediaTypeOrNull())
            )

            val mgBodies: Array<RequestBody> = muscleGroups.map { id: String -> id.toRequestBody(text) }.toTypedArray()
            val eqBodies: Array<RequestBody> = equipment.map { id: String -> id.toRequestBody(text) }.toTypedArray()
            val tagBodies: Array<RequestBody> = hashtags.map { tag: String -> tag.toRequestBody(text) }.toTypedArray()

            val authorBody: RequestBody = "".toRequestBody(text)

            val res = api.createExercise(
                auth = "Bearer $accessToken",
                author = authorBody,
                name = nameBody,
                description = descriptionBody,
                advancedLevel = advancedBody,
                visibility = visibilityBody,
                image = imagePart,
                video = videoPart,
                muscleGroup = mgBodies,
                equipment = eqBodies,
                hashtags = tagBodies
            )

            imageFile.delete(); videoFile.delete()

            if (res.isSuccessful) {
                Result.success(Unit)
            } else {
                val raw = res.errorBody()?.string()
                val msg = AuthErrorMapper.mapAuthErrorToUserMessage(context, raw, res.code())
                Result.failure(IllegalStateException(msg))
            }
        } catch (e: HttpException) {
            val raw = e.response()?.errorBody()?.string()
            val msg = AuthErrorMapper.mapAuthErrorToUserMessage(context, raw, e.code())
            Result.failure(IllegalStateException(msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun uriToTempFile(uri: Uri, prefix: String, suffix: String): File {
        val input: InputStream? = context.contentResolver.openInputStream(uri)
        val file = File.createTempFile(prefix, suffix, context.cacheDir)
        FileOutputStream(file).use { out ->
            input?.use { inputStream: InputStream -> inputStream.copyTo(out) }
        }
        return file
    }

    private fun guessSuffix(uri: Uri): String {
        val type = context.contentResolver.getType(uri) ?: return ".bin"
        return when {
            type.startsWith("image/") -> ".jpg"
            type.startsWith("video/") -> ".mp4"
            else -> ".bin"
        }
    }
}