package com.example.gymeaze.data.repository

import android.content.Context
import android.widget.Toast
import com.example.gymeaze.BuildConfig
import com.example.gymeaze.R
import com.example.gymeaze.data.UserPreferences
import com.example.gymeaze.data.api.AuthApiService
import com.example.gymeaze.data.models.AccountLinkingError
import com.example.gymeaze.data.models.GoogleAuthLoginRequest
import com.example.gymeaze.data.models.GoogleLinkRequest
import com.example.gymeaze.data.models.GoogleSignInResult
import com.example.gymeaze.data.models.LoginRequest
import com.example.gymeaze.data.models.LogoutRequest
import com.example.gymeaze.data.models.RefreshTokenRequest
import com.example.gymeaze.data.models.RegisterRequest
import com.example.gymeaze.data.models.User
import com.example.gymeaze.util.AuthErrorMapper
import com.example.gymeaze.util.GoogleErrorMapper
import com.example.gymeaze.util.GoogleSignInHelper
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthRepository(private val context: Context){
    private val userPreferences = UserPreferences(context)

    private val loggingInterceptor = HttpLoggingInterceptor().apply{
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val api: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    private val googleSignInHelper = GoogleSignInHelper(context)

    suspend fun register(username: String, email: String, password: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.register(RegisterRequest(username, email, password))
                if (response.isSuccessful) {
                    login(email, password)
                } else {
                    val raw = response.errorBody()?.string()
                    val userMessage = AuthErrorMapper.mapAuthErrorToUserMessage(
                        context,
                        raw,
                        response.code()
                    )
                    return@withContext Result.failure(Exception(userMessage))
                }
            } catch (_: IOException) {
                return@withContext Result.failure(Exception(context.getString(R.string.network_error)))
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }
    }

    suspend fun login(usernameOrEmail: String, password: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.login(
                    LoginRequest(
                        login = usernameOrEmail,
                        password = password,
                        rememberMe = true
                    )
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        userPreferences.saveTokens(body.accessToken, body.refreshToken)

                        runCatching {
                            val meRes = api.getCurrentUser("Bearer ${body.accessToken}")
                            if (meRes.isSuccessful && meRes.body() != null) {
                                val user = meRes.body()!!
                                userPreferences.saveUserData(user._id, user.username, user.email)
                            }
                        }

                        userPreferences.setEmail(usernameOrEmail)
                        userPreferences.setLoggedIn(true)
                        userPreferences.setFirstLogin(body.isFirstLogin)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.login_success),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    return@withContext Result.success(true)
                } else {
                    val raw = response.errorBody()?.string()
                    val userMessage = AuthErrorMapper.mapAuthErrorToUserMessage(
                        context,
                        raw,
                        response.code()
                    )
                    Result.failure(Exception(userMessage))
                }
            } catch (_: IOException) {
                return@withContext Result.failure(Exception(context.getString(R.string.network_error)))
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }
    }


    suspend fun loginWithGoogle(googleAuthCode: String): Result<GoogleSignInResult> {
        return withContext(Dispatchers.IO) {
            try {
                val request = GoogleAuthLoginRequest(code = googleAuthCode)
                val response = api.googleAuth(request)

                when {
                    response.isSuccessful && response.body() != null -> {
                        val authResponse = response.body()!!

                        val userResult = getUserWithToken(authResponse.accessToken)
                        if (userResult.isSuccess) {
                            val user = userResult.getOrNull()!!

                            userPreferences.saveTokens(
                                authResponse.accessToken,
                                authResponse.refreshToken
                            )
                            userPreferences.saveUserData(user._id, user.username, user.email)
                            userPreferences.setLoggedIn(true)

                            userPreferences.setFirstLogin(authResponse.isFirstLogin)

                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.login_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            Result.success(GoogleSignInResult(success = true))
                        } else {
                            Result.success(
                                GoogleSignInResult(
                                    success = false,
                                    error = context.getString(R.string.error_google_user_data_fetch_failed)
                                )
                            )
                        }
                    }

                    response.code() == 409 -> {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody?.contains("linkRequired") == true) {
                            try {
                                val gson = Gson()
                                val linkingError =
                                    gson.fromJson(errorBody, AccountLinkingError::class.java)
                                Result.success(
                                    GoogleSignInResult(
                                        success = false,
                                        needsLinking = true,
                                        email = linkingError.email,
                                        token = linkingError.token
                                    )
                                )
                            } catch (_: Exception) {
                                Result.success(
                                    GoogleSignInResult(
                                        success = false,
                                        error = context.getString(R.string.account_linking_required)
                                    )
                                )
                            }
                        } else {
                            Result.success(
                                GoogleSignInResult(
                                    success = false,
                                    error = context.getString(R.string.google_login_conflict)
                                )
                            )
                        }
                    }

                    else -> {
                        val errorBody = response.errorBody()?.string()
                        val userMessage = GoogleErrorMapper.mapGoogleAuthApiError(
                            context,
                            errorBody,
                            response.code()
                        )
                        Result.success(
                            GoogleSignInResult(
                                success = false,
                                error = userMessage
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                val userMessage = GoogleErrorMapper.mapGoogleAuthApiError(context, e.message)
                Result.success(
                    GoogleSignInResult(
                        success = false,
                        error = userMessage
                    )
                )
            }
        }
    }


    suspend fun linkGoogleAccount(email: String, password: String, token: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.linkGoogleAccount(GoogleLinkRequest(token, password, email))

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!

                    val userResult = getUserWithToken(authResponse.accessToken)
                    if (userResult.isSuccess) {
                        val user = userResult.getOrNull()!!

                        userPreferences.saveTokens(
                            authResponse.accessToken,
                            authResponse.refreshToken
                        )
                        userPreferences.saveUserData(user._id, user.username, user.email)
                        userPreferences.setLoggedIn(true)
                        userPreferences.setFirstLogin(authResponse.isFirstLogin)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.link_accounts_success),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        Result.success(true)
                    } else {
                        Result.failure(Exception(context.getString(R.string.user_data_fetch_failed_after_linking)))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val userMessage = when (response.code()) {
                        400 -> context.getString(R.string.error_validation_failed)
                        401 -> context.getString(R.string.error_invalid_credentials)
                        404 -> context.getString(R.string.error_user_not_found)
                        409 -> context.getString(R.string.error_google_login_conflict)
                        else -> GoogleErrorMapper.mapGoogleAuthApiError(
                            context,
                            errorBody,
                            response.code()
                        )
                    }
                    Result.failure(Exception(userMessage))
                }
            } catch (e: Exception) {
                val userMessage = GoogleErrorMapper.mapGoogleAuthApiError(context, e.message)
                Result.failure(Exception(userMessage))
            }
        }
    }

    fun getGoogleSignInHelper() = googleSignInHelper

    suspend fun refreshAccessToken(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val refreshToken = userPreferences.refreshToken.first()
                if (refreshToken.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception(context.getString(R.string.no_refresh_token)))
                }

                val response = api.refreshToken(RefreshTokenRequest(refreshToken))
                if (response.isSuccessful && response.body() != null) {
                    val newAccessToken = response.body()!!.accessToken
                    userPreferences.saveTokens(newAccessToken, refreshToken)
                    Result.success(newAccessToken)
                } else {
                    logout()
                    Result.failure(Exception("Refresh token wygasł - użytkownik został wylogowany"))
                }
            } catch (e: Exception) {
                logout()
                Result.failure(e)
            }
        }
    }

    suspend fun logout(): Result<String> {
        return try {
            val refreshToken = userPreferences.refreshToken.first()
            val accessToken = userPreferences.accessToken.first()

            if (!refreshToken.isNullOrEmpty()){
                runCatching { api.logout(LogoutRequest(refreshToken), "Bearer $accessToken") }
            }
            userPreferences.clearTokens()
            userPreferences.clearUserData()
            userPreferences.setLoggedIn(false)

            Result.success(context.getString(R.string.logout_success))
        } catch(e: Exception) {
            userPreferences.clearTokens()
            userPreferences.clearUserData()
            userPreferences.setLoggedIn(false)
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): Result<User> {
        return try {
            val accessToken = userPreferences.accessToken.first()
            if( accessToken.isNullOrEmpty()){
                return Result.failure(Exception(context.getString(R.string.no_access_token)))
            }

            val response = api.getCurrentUser("Bearer $accessToken")
            if(response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.code() == 401) {
                val refreshResult = refreshAccessToken()
                if(refreshResult.isSuccess) {
                    getCurrentUser()
                } else {
                    Result.failure(Exception(context.getString(R.string.session_expired)))
                }
            } else {
                Result.failure(Exception(context.getString(R.string.user_data_fetch_general_error)))
            }
        } catch (e: Exception){
            Result .failure(e)
        }
    }

    private suspend fun getUserWithToken(accessToken: String): Result<User> {
        return try {
            val response = api.getCurrentUser("Bearer $accessToken")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(context.getString(R.string.user_fetch_error_code, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    fun getPreferences() = userPreferences

    suspend fun clearUserSession(): Result<String> {
        return try {
            userPreferences.clearTokens()
            userPreferences.clearUserData()
            userPreferences.setLoggedIn(false)
            Result.success("Session cleared")
        } catch(e: Exception) {
            userPreferences.clearTokens()
            userPreferences.clearUserData()
            userPreferences.setLoggedIn(false)
            Result.failure(e)
        }
    }
}