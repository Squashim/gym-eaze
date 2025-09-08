package com.example.gymeaze.data.repository

import android.content.Context
import android.net.Uri
import com.example.gymeaze.BuildConfig
import com.example.gymeaze.data.UserPreferences
import com.example.gymeaze.data.api.UserApiService
import com.example.gymeaze.data.models.Badge
import com.example.gymeaze.data.models.UpdateProfileResponse
import com.example.gymeaze.data.models.UserProfileDetails
import com.example.gymeaze.util.ApiUtils
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
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

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

    suspend fun updateProfileDetails(
        description: String,
        goal: String?,
        pinnedBadges: List<Badge>?,
        profileImageFile: File?
    ): Result<UpdateProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val descriptionPart = ApiUtils.createPartFromString(description)
            val goalPart = goal?.let { ApiUtils.createPartFromString(it) }

            val pinnedBadgesParts = when {
                pinnedBadges == null -> {
                    emptyList<MultipartBody.Part>()
                }
                pinnedBadges.isEmpty() -> {
                    listOf(MultipartBody.Part.createFormData("pinnedBadges[]", ""))
                }
                else -> {
                    val badgeIds = pinnedBadges.map { it.id }
                    ApiUtils.createPartsFromStringArray("pinnedBadges[]", badgeIds)
                }
            }

            val imagePart = profileImageFile?.let { ApiUtils.prepareFilePart("profilePicture", it) }


            val response = api.updateUserProfileDetails(
                description = descriptionPart,
                goal = goalPart,
                pinnedBadges = pinnedBadgesParts,
                profilePicture = imagePart
            )

            val body = response.body()
            return@withContext if (response.isSuccessful && body != null) {
                profileImageFile?.delete()
                Result.success(body)
            } else {
                profileImageFile?.delete()
                val rawError = response.errorBody()?.string().orEmpty()
                val message = AuthErrorMapper.mapAuthErrorToUserMessage(context, rawError, response.code())
                Result.failure(IllegalStateException(message))
            }

        } catch (e: IOException) {
            profileImageFile?.delete()
            Result.failure(IllegalStateException("Błąd sieci: ${e.message}", e))
        } catch (e: Exception) {
            profileImageFile?.delete()
            Result.failure(e)
        }
    }

    suspend fun getUserBadges(): Result<List<Badge>> {
        return try {
            val response = api.getUserBadges()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch badges"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}