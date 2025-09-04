package com.example.gymeaze.data

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.example.gymeaze.BuildConfig
import com.example.gymeaze.R
import com.example.gymeaze.data.api.ExerciseApiService
import com.example.gymeaze.data.models.Equipment
import com.example.gymeaze.data.models.MuscleGroup
import com.example.gymeaze.util.AuthErrorMapper
import com.google.gson.Gson
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

    suspend fun createExercise(
        name: String,
        description: String,
        imageUri: Uri,
        videoUri: Uri,
        difficulty: String,
        isPublic: String,
        muscleGroup: List<String>,
        equipment: List<String>,
        hashtags: List<String>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val accessToken = userPreferences.accessToken.first()
            if (accessToken.isNullOrBlank()) {
                return@withContext Result.failure(IllegalStateException(context.getString(R.string.unauthorized)))
            }

            val imageSuffix = guessSuffix(imageUri)
            val videoSuffix = guessSuffix(videoUri)

            if(imageSuffix !in listOf(".png", ".webp", ".jpg", ".jpeg")){
                return@withContext Result.failure(IllegalArgumentException("Dozwolone rozszerzenia zdjęcia to .png, .webp, .jpg"))
            }

            if(videoSuffix !in listOf(".mp4", ".webm")){
                return@withContext Result.failure(IllegalArgumentException("Dozwolone rozszerzenia filmiku to .mp4, .webm"))
            }

            val imageFile = uriToTempFile(imageUri, "exercise_image", imageSuffix)
            val videoFile = uriToTempFile(videoUri, "exercise_video", videoSuffix)

            if (imageFile.length() > MAX_IMAGE_BYTES) {
                imageFile.delete(); videoFile.delete()
                return@withContext Result.failure(IllegalArgumentException("Maksymalny rozmiar obrazu to 5MB"))
            }
            if (videoFile.length() > MAX_VIDEO_BYTES) {
                imageFile.delete(); videoFile.delete()
                return@withContext Result.failure(IllegalArgumentException("Maksymalny rozmiar filmiku to 20MB"))
            }

            val text = "text/plain".toMediaType()
            val json = "application/json".toMediaType()

            val nameBody: RequestBody = name.toRequestBody(text)
            val descriptionBody: RequestBody = description.toRequestBody(text)
            val advancedBody: RequestBody = difficulty.toRequestBody(text)
            val visibilityBody: RequestBody = isPublic.toRequestBody(text)

            val imageMime = when(imageSuffix){
                ".png" -> "image/png"
                ".jpg" -> "image/jpg"
                ".webp" -> "image/webp"
                else -> "application/octet-stream"
            }

            val videoMime = when(videoSuffix){
                ".mp4" -> "video/mp4"
                ".webm" -> "video/webm"
                else -> "application/octet-stream"
            }

            val imagePart = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                imageFile.asRequestBody(imageMime.toMediaTypeOrNull())
            )
            val videoPart = MultipartBody.Part.createFormData(
                "video",
                videoFile.name,
                videoFile.asRequestBody(videoMime.toMediaTypeOrNull())
            )

            val mgBodies: RequestBody = Gson().toJson(muscleGroup).toRequestBody(json)
            val eqBodies: RequestBody = Gson().toJson(equipment).toRequestBody(json)
            val tagBodies: RequestBody = Gson().toJson(hashtags).toRequestBody(json)


            val res = api.createExercise(
                auth = "Bearer $accessToken",
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
                withContext(Dispatchers.Main){
                    Toast.makeText(context, context.getString(R.string.exercise_create_success), Toast.LENGTH_SHORT).show()
                }

                //TODO: przekieruj do widoku ze szczegolami stworzonego cwiczenia

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
            type.startsWith("image/png") -> ".png"
            type.startsWith("image/jpeg") -> ".jpg"
            type.startsWith("image/webp") -> ".webp"
            type.startsWith("video/mp4") -> ".mp4"
            type.startsWith("video/webm") -> ".webm"
            else -> ".bin"
        }
    }
}