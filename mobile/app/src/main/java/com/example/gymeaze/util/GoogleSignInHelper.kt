package com.example.gymeaze.util

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class GoogleSignInHelper(private val context: Context) {
    companion object {
        const val WEB_CLIENT_ID = "606457019708-osfclcvreqsj55bqu72ncjqr6s0k3o9c.apps.googleusercontent.com"
    }

    private val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(WEB_CLIENT_ID)
        .requestEmail()
        .build()

    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun handleSignInResult(task: Task<GoogleSignInAccount>): Result<GoogleSignInAccount> {
        return try {
            val account = task.getResult(ApiException::class.java)
            if (account != null && account.idToken != null) {
                Result.success(account)
            } else {
                Result.failure(Exception("Google Sign-In failed - no account or token"))
            }
        } catch (e: ApiException) {
            Result.failure(Exception("Google Sign-In failed ${e.message}"))
        }
    }

    fun signOut() {
        googleSignInClient.signOut()
    }
}