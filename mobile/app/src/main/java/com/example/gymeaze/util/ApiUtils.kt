package com.example.gymeaze.util

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object ApiUtils {
    fun createPartFromString(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun createPartsFromStringArray(fieldName: String, items: List<String>): List<MultipartBody.Part> {
        // POPRAWKA: Dla pustej listy wysyłamy jeden pusty element
        return if (items.isEmpty()) {
            listOf(MultipartBody.Part.createFormData(fieldName, ""))
        } else {
            items.map { item ->
                MultipartBody.Part.createFormData(fieldName, item)
            }
        }
    }


    fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
        // Określ dokładny MIME type na podstawie rozszerzenia
        val mimeType = when (file.extension.lowercase()) {
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            "webp" -> "image/webp"
            else -> "image/jpeg" // domyślny
        }

        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
}