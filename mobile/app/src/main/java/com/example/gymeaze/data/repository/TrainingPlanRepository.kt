package com.example.gymeaze.data.repository

import android.content.Context
import android.net.Uri
import com.example.gymeaze.BuildConfig
import com.example.gymeaze.R
import com.example.gymeaze.data.UserPreferences
import com.example.gymeaze.data.api.TrainingPlanApiService
import com.example.gymeaze.data.models.*
import com.example.gymeaze.util.AuthErrorMapper
import com.example.gymeaze.util.AuthInterceptor
import com.example.gymeaze.util.TokenAuthenticator
import com.example.gymeaze.viewmodel.TrainingDay
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit
import com.example.gymeaze.data.models.UserFavorites

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
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(180, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .callTimeout(300, TimeUnit.SECONDS)
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

    suspend fun getTrainingPlans(
        filters: TrainingPlanFilters = TrainingPlanFilters(),
        sortBy: String = "latest",
        page: Int = 1,
        pageSize: Int = 9
    ): Result<TrainingPlansResponse> = withContext(Dispatchers.IO) {
        try {
            val query = TrainingPlanQuery(
                filters = filters,
                sortBy = sortBy,
                page = page,
                pageSize = pageSize
            )
            val response = api.getTrainingPlans(query)
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

    suspend fun getTrainingPlanById(planId: String): Result<TrainingPlan> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTrainingPlanById(planId)

            if (response.isSuccessful && response.body() != null) {
                val plan = response.body()!!
                Result.success(plan)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Nie można pobrać planu treningowego"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyTrainingPlans(
        page: Int = 1,
        pageSize: Int = 9
    ): Result<TrainingPlansResponse> = withContext(Dispatchers.IO) {
        try {
            val userId = authRepository.getCurrentUserId()

            if (userId == null) {
                return@withContext Result.failure(IllegalStateException("Nie można pobrać ID użytkownika"))
            }

            val response = api.getMyTrainingPlansByUserId(userId, page, pageSize)

            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                Result.success(result)
            } else {
                val raw = response.errorBody()?.string()
                val msg = AuthErrorMapper.mapAuthErrorToUserMessage(context, raw, response.code())
                Result.failure(IllegalStateException(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFavoriteTrainingPlans(
        page: Int = 1,
        pageSize: Int = 9
    ): Result<TrainingPlansResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getFavoriteTrainingPlans(page, pageSize)
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

    suspend fun toggleTrainingPlanLike(planId: String): Result<LikeResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.toggleTrainingPlanLike(planId)

            if (response.isSuccessful && response.body() != null) {
                val likeResponse = response.body()!!
                Result.success(likeResponse)
            } else {
                val raw = response.errorBody()?.string()
                val msg = AuthErrorMapper.mapAuthErrorToUserMessage(context, raw, response.code())
                Result.failure(IllegalStateException(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleTrainingPlanFavorite(planId: String): Result<MessageResponse> = withContext(Dispatchers.IO) {
        try {

            val token = userPreferences.accessToken.first()

            val response = api.toggleTrainingPlanFavorite(planId)

            if (response.isSuccessful && response.body() != null) {
                val messageResponse = response.body()!!
                Result.success(messageResponse)
            } else {
                val errorBody = response.errorBody()?.string()

                val msg = AuthErrorMapper.mapAuthErrorToUserMessage(context, errorBody, response.code())
                Result.failure(IllegalStateException(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserFavorites(): Result<UserFavorites> = withContext(Dispatchers.IO) {
        try {

            val response = api.getUserFavorites()

            if (response.isSuccessful && response.body() != null) {
                val userFavorites = response.body()!!
                Result.success(userFavorites)
            } else {
                val errorBody = response.errorBody()?.string()

                val msg = AuthErrorMapper.mapAuthErrorToUserMessage(context, errorBody, response.code())
                Result.failure(IllegalStateException(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun downloadTrainingPlanPDF(planId: String): Result<ByteArray> = withContext(Dispatchers.IO) {
        try {
            val response = api.downloadTrainingPlanPDF(planId)

            if (response.isSuccessful && response.body() != null) {
                val pdfBytes = response.body()!!.bytes()
                Result.success(pdfBytes)
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Plan treningowy nie został znaleziony"
                    403 -> "Plan treningowy jest prywatny"
                    401 -> "Musisz być zalogowany aby pobrać PDF"
                    408 -> "Timeout - spróbuj ponownie za chwilę"
                    504 -> "Serwer nie odpowiada - spróbuj ponownie"
                    else -> "Błąd podczas pobierania PDF (${response.code()})"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Pobieranie PDF trwa zbyt długo. Plan jest duży - spróbuj ponownie."))
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Brak połączenia z serwerem"))
        } catch (e: java.io.IOException) {
            Result.failure(Exception("Błąd pobierania pliku. Sprawdź połączenie internetowe."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTrainingPlan(planId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteTrainingPlan(planId)
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

    suspend fun updateTrainingPlan(
        planId: String,
        updateData: UpdateTrainingPlanData
    ): Result<TrainingPlan> = withContext(Dispatchers.IO) {
        try {
            val nameBody = updateData.name.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionBody = (updateData.description ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
            val goalBody = updateData.goal.toRequestBody("text/plain".toMediaTypeOrNull())
            val visibilityBody = updateData.visibility.toRequestBody("text/plain".toMediaTypeOrNull())
            val advancedLevelBody = updateData.advancedLevel.toRequestBody("text/plain".toMediaTypeOrNull())

            val hashtagsJson = Gson().toJson(updateData.hashtags)
            val hashtagsBody = hashtagsJson.toRequestBody("application/json".toMediaTypeOrNull())

            val daysJson = Gson().toJson(updateData.days)
            val daysBody = daysJson.toRequestBody("application/json".toMediaTypeOrNull())

            val imagePart = updateData.imageUri?.let { imageUri ->
                try {
                    val file = createTempFileFromUri(context, Uri.parse(imageUri))
                    val mimeType = context.contentResolver.getType(Uri.parse(imageUri)) ?: "image/jpeg"

                    val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", file.name, requestFile)
                } catch (e: Exception) {
                    null
                }
            }

            val response = api.updateTrainingPlan(
                planId = planId,
                name = nameBody,
                description = descriptionBody,
                goal = goalBody,
                visibility = visibilityBody,
                advancedLevel = advancedLevelBody,
                hashtags = hashtagsBody,
                days = daysBody,
                image = imagePart
            )

            if (response.isSuccessful && response.body() != null) {
                val updatedPlan = response.body()!!
                Result.success(updatedPlan)
            } else {
                val raw = response.errorBody()?.string()
                val msg = AuthErrorMapper.mapAuthErrorToUserMessage(context, raw, response.code())
                Result.failure(IllegalStateException(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createTempFileFromUri(context: Context, uri: Uri): File {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)

            val mimeType = context.contentResolver.getType(uri)
            val extension = when (mimeType) {
                "image/jpeg", "image/jpg" -> ".jpg"
                "image/png" -> ".png"
                "image/webp" -> ".webp"
                else -> ".jpg"
            }

            val tempFile = File.createTempFile("training_plan_image", extension, context.cacheDir)

            tempFile.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            inputStream?.close()

            tempFile
        } catch (e: Exception) {
            throw e
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