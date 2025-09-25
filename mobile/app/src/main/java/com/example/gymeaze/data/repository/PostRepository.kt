package com.example.gymeaze.data.repository

import android.content.Context
import android.net.Uri
import com.example.gymeaze.BuildConfig
import com.example.gymeaze.R
import com.example.gymeaze.data.UserPreferences
import com.example.gymeaze.data.api.PostApiService
import com.example.gymeaze.data.models.GetPostsResponse
import com.example.gymeaze.data.models.LikesResponse
import com.example.gymeaze.data.models.ToggleLikeResponse
import com.example.gymeaze.util.AuthInterceptor
import com.example.gymeaze.util.TokenAuthenticator
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class PostsRepository(private val context: Context) {

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

    private val api: PostApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PostApiService::class.java)
    }

    private val MAX_IMAGE_BYTES = 5L * 1024L * 1024L
    private val MAX_VIDEO_BYTES = 20L * 1024L * 1024L

    suspend fun createPost(
        content: String,
        hashtags: List<String>,
        imageUri: Uri?,
        videoUri: Uri?
    ): Result<String> {
        return try {
            val imageSuffix = imageUri?.let { guessSuffix(it) }
            val videoSuffix = videoUri?.let { guessSuffix(it) }

            if (imageUri != null && imageSuffix !in listOf(".png", ".webp", ".jpg", ".jpeg")) {
                return Result.failure(IllegalArgumentException(context.getString(R.string.error_allowed_image_extensions)))
            }
            if (videoUri != null && videoSuffix !in listOf(".mp4", ".webm")) {
                return Result.failure(IllegalArgumentException(context.getString(R.string.error_allowed_video_extensions)))
            }

            val imageFile = imageUri?.let { uriToTempFile(it, "post_image", imageSuffix!!) }
            val videoFile = videoUri?.let { uriToTempFile(it, "post_video", videoSuffix!!) }

            if (imageFile != null && imageFile.length() > MAX_IMAGE_BYTES) {
                imageFile.delete()
                return Result.failure(IllegalArgumentException(context.getString(R.string.error_max_image_size)))
            }
            if (videoFile != null && videoFile.length() > MAX_VIDEO_BYTES) {
                videoFile.delete()
                return Result.failure(IllegalArgumentException(context.getString(R.string.error_max_video_size)))
            }

            val text = "text/plain".toMediaType()
            val contentBody: RequestBody = content.toRequestBody(text)
            val hashtagsBody: RequestBody =
                Gson().toJson(hashtags).toRequestBody("application/json".toMediaType())

            val imageMime = when (imageSuffix) {
                ".png" -> "image/png"
                ".jpg", ".jpeg" -> "image/jpeg"
                ".webp" -> "image/webp"
                else -> "application/octet-stream"
            }

            val videoMime = when (videoSuffix) {
                ".mp4" -> "video/mp4"
                ".webm" -> "video/webm"
                else -> "application/octet-stream"
            }

            val imagePart = imageFile?.let {
                MultipartBody.Part.createFormData(
                    "image",
                    it.name,
                    it.asRequestBody(imageMime.toMediaTypeOrNull())
                )
            }
            val videoPart = videoFile?.let {
                MultipartBody.Part.createFormData(
                    "video",
                    it.name,
                    it.asRequestBody(videoMime.toMediaTypeOrNull())
                )
            }

            val response = api.addPost(contentBody, hashtagsBody, imagePart, videoPart)
            imageFile?.delete()
            videoFile?.delete()

            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Post dodany")
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Błąd serwera"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun uriToTempFile(uri: Uri, prefix: String, suffix: String): File {
        val input: InputStream? = context.contentResolver.openInputStream(uri)
        val file = File.createTempFile(prefix, suffix, context.cacheDir)
        FileOutputStream(file).use { out ->
            input?.use { inputStream: InputStream -> inputStream.copyTo(out) }
        }
        return file
    }

    private fun guessSuffix(uri: Uri): String {
        val type = context.contentResolver.getType(uri) ?: return ".bin"
        return when {
            type.startsWith("image/png") -> ".png"
            type.startsWith("image/jpeg") -> ".jpg"
            type.startsWith("image/webp") -> ".webp"
            type.startsWith("video/mp4") -> ".mp4"
            type.startsWith("video/webm") -> ".webm"
            else -> ".bin"
        }
    }

    suspend fun getPosts(
        hashtags: List<String>? = null,
        followedOnly: Boolean? = null,
        cursor: String? = null,
        limit: Int = 10
    ): GetPostsResponse? {
        return try {
            val response = api.getPosts(
                hashtags = hashtags,
                followedOnly = followedOnly,
                cursor = cursor,
                limit = limit
            )
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun toggleLike(postId: String): Result<ToggleLikeResponse> {
        return try {
            val response = api.toggleLike(postId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Błąd polubienia"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserLikes(): Result<LikesResponse> = try {
        val response = api.getUserLikes()
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Błąd pobierania polubień"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}