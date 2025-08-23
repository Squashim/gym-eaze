package com.example.gymeaze.data

import android.content.Context
import com.example.gymeaze.BuildConfig
import com.example.gymeaze.data.api.AuthApiService
import com.example.gymeaze.data.models.AccountLinkingError
import com.example.gymeaze.data.models.GoogleAuthLoginRequest
import com.example.gymeaze.data.models.GoogleLinkRequest
import com.example.gymeaze.data.models.GoogleSignInResult
import com.example.gymeaze.data.models.LoginRequest
import com.example.gymeaze.data.models.LogoutRequest
import com.example.gymeaze.data.models.RegisterRequest
import com.example.gymeaze.data.models.RefreshTokenRequest
import com.example.gymeaze.data.models.User
import com.example.gymeaze.util.GoogleSignInHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
                    userPreferences.setUsername(username)
                    userPreferences.setEmail(email)
                    userPreferences.setLoggedIn(true)
                    userPreferences.setFirstLogin(true)
                    Result.success(true)
                } else {
                    val msg = response.errorBody()?.string()?.ifBlank { null }
                    Result.failure(Exception(msg ?: "Błąd rejestracji (kod ${response.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun login(usernameOrEmail: String, password: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.login(LoginRequest(login = usernameOrEmail, password = password, rememberMe = true))
                if (response.isSuccessful) {
                    val body = response.body()
                    if(body != null) {
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
                    }
                    Result.success(true)
                } else {
                    val msg = response.errorBody()?.string()?.ifBlank { null }
                    Result.failure(Exception(msg ?: "Błąd logowania (kod ${response.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    suspend fun loginWithGoogle(googleAuthCode: String): Result<GoogleSignInResult> {
        return withContext(Dispatchers.IO) {
            try{
                val request = GoogleAuthLoginRequest(code = googleAuthCode)
                val response = api.googleAuth(request)

                when {
                    response.isSuccessful && response.body() != null -> {
                        val authResponse = response.body()!!

                        val userResult = getUserWithToken(authResponse.accessToken)
                        if (userResult.isSuccess) {
                            val user = userResult.getOrNull()!!

                            userPreferences.saveTokens(authResponse.accessToken, authResponse.refreshToken)
                            userPreferences.saveUserData(user._id, user.username, user.email)
                            userPreferences.setLoggedIn(true)

                            userPreferences.setFirstLogin(authResponse.isFirstLogin)

                            Result.success(GoogleSignInResult(success = true))
                        } else {
                            Result.success(GoogleSignInResult(
                                success = false,
                                error = "Nie udało się pobrać danych użytkownika"
                            ))
                        }
                    }

                    response.code() == 409 -> {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody?.contains("linkRequired") == true) {
                            try {
                                val gson = com.google.gson.Gson()
                                val linkingError = gson.fromJson(errorBody, AccountLinkingError::class.java)
                                Result.success(GoogleSignInResult(
                                    success = false,
                                    needsLinking = true,
                                    email = linkingError.email,
                                    token = linkingError.token
                                ))
                            } catch(_: Exception) {
                                Result.success(GoogleSignInResult(
                                    success = false,
                                    error = "Konto wymaga połączenia"
                                ))
                            }
                        } else {
                            Result.success(GoogleSignInResult(
                                success = false,
                                error = "Konflikt podczas logowania Google"
                            ))
                        }
                    }

                    response.code() == 400 -> {
                        Result.success(GoogleSignInResult(
                            success = false,
                            error = "Kod Google jest wymagany lub nieprawidłowy"
                        ))
                    }

                    response.code() == 401 -> {
                        Result.success(GoogleSignInResult(
                            success = false,
                            error = "Nieprawidłowy kod autoryzacji Google"
                        ))
                    }

                    else -> {
                        val errorMsg = response.errorBody()?.string() ?: "Nieznany błąd"
                        Result.success(GoogleSignInResult(
                            success = false,
                            error = "Błąd logowania Google: $errorMsg"
                        ))
                    }
                }
            } catch(e: Exception) {
                Result.success(GoogleSignInResult(
                    success = false,
                    error = e.message ?: "Nieznany błąd podczas logowania Google"
                ))
            }
        }
    }


    suspend fun linkGoogleAccount(email: String, password: String, token: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.linkGoogleAccount(GoogleLinkRequest(token, password, email))

                if(response.isSuccessful && response.body() != null ) {
                    val authResponse = response.body()!!

                    val userResult = getUserWithToken(authResponse.accessToken)
                    if (userResult.isSuccess) {
                        val user = userResult.getOrNull()!!

                        userPreferences.saveTokens(authResponse.accessToken, authResponse.refreshToken)
                        userPreferences.saveUserData(user._id, user.username, user.email)
                        userPreferences.setLoggedIn(true)
                        userPreferences.setFirstLogin(authResponse.isFirstLogin)

                        Result.success(true)
                    } else {
                        Result.failure(Exception("Nie udało się pobrać danych użytkownika po połączeniu kont"))
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Link accounts failed"
                    when (response.code()) {
                        400 -> Result.failure(Exception("Nieprawidłowe dane: $errorMsg"))
                        401 -> Result.failure(Exception("Nieprawidłowe hasło lub token Google"))
                        404 -> Result.failure(Exception("Nie znaleziono konta z tym emailem"))
                        409 -> Result.failure(Exception("Konto Google jest już połączone"))
                        else -> Result.failure(Exception("Błąd łączenia kont (${response.code()}): $errorMsg"))
                    }
                }
            } catch(e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun getGoogleSignInHelper() = googleSignInHelper

    suspend fun refreshAccessToken(): Result<String> {
        return try {
            val refreshToken = userPreferences.refreshToken.first()
            if (refreshToken.isNullOrEmpty()) {
                return Result.failure(Exception("Brak tokenu refresh."))
            }

            val response = api.refreshToken(RefreshTokenRequest(refreshToken))
            if(response.isSuccessful && response.body() != null) {
                val newAccessToken = response.body()!!.accessToken
                userPreferences.saveTokens(newAccessToken, refreshToken)
                Result.success(newAccessToken)
            } else {
                logout()
                Result.failure(Exception("Refresh token wygasl lub jest niepoprawny."))
            }
        } catch (e: Exception) {
            Result.failure(e)
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
            Result.success("Pomyślnie wylogowano")
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
                return Result.failure(Exception("Brak tokenu access"))
            }

            val response = api.getCurrentUser("Bearer $accessToken")
            if(response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.code() == 401) {
                val refreshResult = refreshAccessToken()
                if(refreshResult.isSuccess) {
                    getCurrentUser()
                } else {
                    Result.failure(Exception("Sesja wygasła"))
                }
            } else {
                Result.failure(Exception("Niepowodzenie. Nie udało się pobrać danych użytkownika"))
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
                Result.failure(Exception("Błąd pobierania użytkownika: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    fun getPreferences() = userPreferences
}