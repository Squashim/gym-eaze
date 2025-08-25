package com.example.gymeaze.util

import android.content.Context
import android.content.Intent
import com.example.gymeaze.BuildConfig
import com.example.gymeaze.R
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
                Result.failure(Exception(context.getString(R.string.google_auth_code_error)))
            }
        } catch (e: ApiException) {
            val errorMessage = when (e.statusCode) {
                12501 -> context.getString(R.string.google_signin_cancelled) // SIGN_IN_CANCELLED
                12500 -> context.getString(R.string.google_signin_failed_error) // SIGN_IN_FAILED
                7 -> context.getString(R.string.google_network_error) // NETWORK_ERROR
                else -> context.getString(R.string.google_login_error_format, e.message ?: "")
            }
            Result.failure(Exception(errorMessage))
        }
    }

    fun signOut() {
        googleSignInClient.signOut()
    }
}