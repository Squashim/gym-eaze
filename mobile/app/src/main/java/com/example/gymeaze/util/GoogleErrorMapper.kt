package com.example.gymeaze.util

import android.content.Context
import com.example.gymeaze.R
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException

object GoogleErrorMapper {

    private fun parseJson(errorBody: String?): JsonObject? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            Gson().fromJson(errorBody, JsonObject::class.java)
        } catch (_: JsonSyntaxException) {
            null
        }
    }

    private fun extractServerMessage(errorBody: String?): String? {
        val obj = parseJson(errorBody) ?: return null
        if (obj.has("message") && obj.get("message").isJsonPrimitive) {
            val msg = obj.get("message").asString
            if (msg.isNotBlank()) return msg
        }
        return null
    }

    fun mapGoogleAuthApiError(context: Context, errorBody: String?, httpCode: Int? = null): String {

        extractServerMessage(errorBody)?.let { return it }

        return when (httpCode) {
            400 -> context.getString(R.string.google_code_invalid)
            401 -> context.getString(R.string.google_auth_code_invalid)
            409 -> context.getString(R.string.google_login_conflict)
            404 -> context.getString(R.string.error_user_not_found)
            429 -> context.getString(R.string.error_too_many_attempts)
            500, 502, 503 -> context.getString(R.string.error_server_error)
            else -> context.getString(R.string.error_google_unknown)
        }
    }

    fun mapGoogleSignInSdkStatus(context: Context, statusCode: Int): String {
        return when (statusCode) {
            12501 -> context.getString(R.string.error_google_signin_cancelled)
            12500 -> context.getString(R.string.error_google_signin_failed)
            7, 8 -> context.getString(R.string.error_google_network_error)
            10 -> context.getString(R.string.error_google_developer_error)
            4 -> context.getString(R.string.error_google_invalid_account)
            15 -> context.getString(R.string.error_timeout)
            17, 3, 2 -> context.getString(R.string.error_google_service_unavailable)
            else -> context.getString(R.string.error_google_unknown)
        }
    }
}