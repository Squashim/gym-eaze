package com.example.gymeaze.util

import android.content.Context
import android.content.Intent
import com.example.gymeaze.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class GoogleSignInHelper(private val context: Context) {
    private val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestServerAuthCode(BuildConfig.GOOGLE_CLIENT_ID)
        .requestEmail()
        .build()

    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun handleSignInResult(task: Task<GoogleSignInAccount>): Result<String> {
        return try {
            val account = task.getResult(ApiException::class.java)
            val authCode = account?.serverAuthCode

            if(authCode != null){
                Result.success(authCode)
            }else{
                Result.failure(Exception("Nie udało się uzyskać kodu autoryzacji z Google"))
            }
        } catch (e: ApiException) {
            val errorMessage = when (e.statusCode) {
                12501 -> "Logowanie zostało anulowane przez użytkownika" // SIGN_IN_CANCELLED
                12500 -> "Logowanie nie powiodło się" // SIGN_IN_FAILED
                7 -> "Wystąpił błąd połączenia sieciowego" // NETWORK_ERROR
                else -> "Błąd logowania Google: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    fun signOut() {
        googleSignInClient.signOut()
    }
}