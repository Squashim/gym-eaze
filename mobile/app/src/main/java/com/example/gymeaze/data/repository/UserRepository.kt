package com.example.gymeaze.data.repository

import android.content.Context
import com.example.gymeaze.BuildConfig
import com.example.gymeaze.R
import com.example.gymeaze.data.UserPreferences
import com.example.gymeaze.data.api.UserApiService
import com.example.gymeaze.data.models.Badge
import com.example.gymeaze.data.models.FavoritesResponse
import com.example.gymeaze.data.models.FollowResponse
import com.example.gymeaze.data.models.FollowerUser
import com.example.gymeaze.data.models.PrivacySettings
import com.example.gymeaze.data.models.UpdateFitnessDataRequest
import com.example.gymeaze.data.models.UpdateFitnessDataResponse
import com.example.gymeaze.data.models.UpdateLoginRequest
import com.example.gymeaze.data.models.UpdateLoginResponse
import com.example.gymeaze.data.models.UpdatePersonalDataRequest
import com.example.gymeaze.data.models.UpdatePersonalDataResponse
import com.example.gymeaze.data.models.UpdatePrivacySettingsRequest
import com.example.gymeaze.data.models.UpdatePrivacySettingsResponse
import com.example.gymeaze.data.models.UpdateProfileResponse
import com.example.gymeaze.data.models.UserFitnessData
import com.example.gymeaze.data.models.UserPersonalData
import com.example.gymeaze.data.models.UserProfileDetails
import com.example.gymeaze.util.ApiUtils
import com.example.gymeaze.util.AuthErrorMapper
import com.example.gymeaze.util.AuthInterceptor
import com.example.gymeaze.util.TokenAuthenticator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import kotlin.Boolean

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
            Result.failure(IllegalStateException(context.getString(R.string.network_error_with_message, e.message ?: ""), e))
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
            Result.failure(IllegalStateException(context.getString(R.string.network_error_with_message, e.message ?: ""), e))
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
                Result.failure(Exception(context.getString(R.string.error_fetch_badges_failed)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateLoginData(
        username: String? = null,
        password: String? = null
    ): Result<UpdateLoginResponse> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateLoginRequest(username, password)

            val response = api.updateLoginData(request)
            val body = response.body()


            return@withContext if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                val rawError = response.errorBody()?.string().orEmpty()
                val message = AuthErrorMapper.mapAuthErrorToUserMessage(context, rawError, response.code())
                Result.failure(IllegalStateException(message))
            }
        } catch (e: IOException) {
            Result.failure(IllegalStateException(context.getString(R.string.network_error_with_message, e.message ?: ""), e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteAccount()

            return@withContext if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val rawError = response.errorBody()?.string().orEmpty()
                val message = AuthErrorMapper.mapAuthErrorToUserMessage(context, rawError, response.code())
                Result.failure(IllegalStateException(message))
            }
        } catch (e: IOException) {
            Result.failure(IllegalStateException(context.getString(R.string.network_error_with_message, e.message ?: ""), e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserPersonalData(): Result<UserPersonalData> = withContext(Dispatchers.IO) {
        try {
            val response = api.getUserPersonalData()
            val body = response.body()

            return@withContext if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                val rawError = response.errorBody()?.string().orEmpty()
                val message = AuthErrorMapper.mapAuthErrorToUserMessage(context, rawError, response.code())
                Result.failure(IllegalStateException(message))
            }
        } catch (e: IOException) {
            Result.failure(IllegalStateException(context.getString(R.string.network_error_with_message, e.message ?: ""), e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserPersonalData(
        firstName: String,
        lastName: String,
        country: String?
    ): Result<UpdatePersonalDataResponse> = withContext(Dispatchers.IO) {
        try {
            val request = UpdatePersonalDataRequest(
                firstName = firstName,
                lastName = lastName,
                country = country
            )

            val response = api.updateUserPersonalData(request)
            val body = response.body()

            return@withContext if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                val rawError = response.errorBody()?.string().orEmpty()
                val message = AuthErrorMapper.mapAuthErrorToUserMessage(context, rawError, response.code())
                Result.failure(IllegalStateException(message))
            }
        } catch (e: IOException) {
            Result.failure(IllegalStateException(context.getString(R.string.network_error_with_message, e.message ?: ""), e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserFitnessData(): Result<UserFitnessData> = withContext(Dispatchers.IO) {
        try {
            val response = api.getUserFitnessData()
            val body = response.body()

            return@withContext if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                val rawError = response.errorBody()?.string().orEmpty()
                val message = AuthErrorMapper.mapAuthErrorToUserMessage(context, rawError, response.code())
                Result.failure(IllegalStateException(message))
            }
        } catch (e: IOException) {
            Result.failure(IllegalStateException(context.getString(R.string.network_error_with_message, e.message ?: ""), e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserFitnessData(
        gender: String?,
        age: Int?,
        height: Int?,
        weight: Int?
    ): Result<UpdateFitnessDataResponse> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateFitnessDataRequest(
                gender = gender,
                age = age,
                height = height,
                weight = weight
            )

            val response = api.updateUserFitnessData(request)
            val body = response.body()

            return@withContext if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                val rawError = response.errorBody()?.string().orEmpty()
                val message = AuthErrorMapper.mapAuthErrorToUserMessage(context, rawError, response.code())
                Result.failure(IllegalStateException(message))
            }
        } catch (e: IOException) {
            Result.failure(IllegalStateException(context.getString(R.string.network_error_with_message, e.message ?: ""), e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPrivacySettings(): Result<PrivacySettings> = withContext(Dispatchers.IO) {
        try {
            val response = api.getPrivacySettings()
            val body = response.body()

            return@withContext if (response.isSuccessful && body != null) {
                Result.success(body.data)
            } else {
                val rawError = response.errorBody()?.string().orEmpty()
                val message = AuthErrorMapper.mapAuthErrorToUserMessage(context, rawError, response.code())
                Result.failure(IllegalStateException(message))
            }
        } catch (e: IOException) {
            Result.failure(IllegalStateException(context.getString(R.string.network_error_with_message, e.message ?: ""), e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePrivacySettings(
        personalDataEnabled: Boolean,
        physicalInfoEnabled: Boolean,
        trainingGoalsEnabled: Boolean,
        badgeRemindersEnabled: Boolean,
        trainingStatsEnabled: Boolean
    ): Result<UpdatePrivacySettingsResponse> = withContext(Dispatchers.IO) {
        try {
            val privacySettings = PrivacySettings(
                isPersonalDetailsPublic = personalDataEnabled,
                isFitnessDetailsPublic = physicalInfoEnabled,
                isGoalPublic = trainingGoalsEnabled,
                isBadgesDetailsPublic = badgeRemindersEnabled,
                isTrainingStatisticsPublic = trainingStatsEnabled
            )


            val request = UpdatePrivacySettingsRequest(privacySettings = privacySettings)
            val response = api.updatePrivacySettings(request)
            val body = response.body()

            return@withContext if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                val rawError = response.errorBody()?.string().orEmpty()
                val message = AuthErrorMapper.mapAuthErrorToUserMessage(context, rawError, response.code())
                Result.failure(IllegalStateException(message))
            }
        } catch (e: IOException) {
            Result.failure(IllegalStateException(context.getString(R.string.network_error_with_message, e.message ?: ""), e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserFavorites(): Result<FavoritesResponse> {
        return try {
            val response = api.getUserFavorites()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(
                    context.getString(
                        R.string.error_get_favorites,
                        response.message()
                    )))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFollowers(): Result<List<FollowerUser>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getFollowers()
            val body = response.body()

            return@withContext if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                val rawError = response.errorBody()?.string().orEmpty()
                val message = AuthErrorMapper.mapAuthErrorToUserMessage(context, rawError, response.code())
                Result.failure(IllegalStateException(message))
            }
        } catch (e: IOException) {
            Result.failure(IllegalStateException(context.getString(R.string.network_error_with_message, e.message ?: ""), e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFollowing(): Result<List<FollowerUser>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getFollowing()
            val body = response.body()

            return@withContext if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                val rawError = response.errorBody()?.string().orEmpty()
                val message = AuthErrorMapper.mapAuthErrorToUserMessage(context, rawError, response.code())
                Result.failure(IllegalStateException(message))
            }
        } catch (e: IOException) {
            Result.failure(IllegalStateException(context.getString(R.string.network_error_with_message, e.message ?: ""), e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(username: String): Result<UserProfileDetails> = withContext(Dispatchers.IO) {
        try {
            val response = api.getUserProfile(username)
            val body = response.body()

            return@withContext if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                val rawError = response.errorBody()?.string().orEmpty()
                val message = AuthErrorMapper.mapAuthErrorToUserMessage(context, rawError, response.code())
                Result.failure(IllegalStateException(message))
            }
        } catch (e: IOException) {
            Result.failure(IllegalStateException(context.getString(R.string.network_error_with_message, e.message ?: ""), e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleUserFollow(userId: String): Result<FollowResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.toggleUserFollow(userId)
            val body = response.body()

            return@withContext if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                val rawError = response.errorBody()?.string().orEmpty()
                val message = AuthErrorMapper.mapAuthErrorToUserMessage(context, rawError, response.code())
                Result.failure(IllegalStateException(message))
            }
        } catch (e: IOException) {
            Result.failure(IllegalStateException(context.getString(R.string.network_error_with_message, e.message ?: ""), e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}