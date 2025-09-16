package com.example.gymeaze.util

import android.content.Context
import com.example.gymeaze.R
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException

object AuthErrorMapper {

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

        if (obj.has("error") && obj.get("error").isJsonPrimitive){
            val errorMsg = obj.get("error").asString
            if (errorMsg.isNotBlank()) return errorMsg
        }

        if (obj.has("message") && obj.get("message").isJsonPrimitive) {
            val msg = obj.get("message").asString
            if (msg.isNotBlank()) return msg
        }
        return null
    }

    fun mapAuthErrorToUserMessage(context: Context, errorBody: String?, httpCode: Int? = null): String {
        // 1) Priorytet: message z backendu
        extractServerMessage(errorBody)?.let { return it }

        // 2) Fallback: po statusie HTTP (backend i tak daje message, więc to pójdzie rzadko)
        when (httpCode) {
            400 -> return context.getString(R.string.error_validation_failed)
            401 -> return context.getString(R.string.error_invalid_credentials)
            403 -> return context.getString(R.string.error_account_disabled)
            404 -> return context.getString(R.string.error_user_not_found)
            409 -> return context.getString(R.string.error_email_taken)
            408 -> return context.getString(R.string.error_timeout)
            in 500..599 -> return context.getString(R.string.error_server_error)
        }

        // 3) Ostateczny fallback
        return context.getString(R.string.error_unknown)
    }
}