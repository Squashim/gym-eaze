package com.example.gymeaze.data.api

import com.example.gymeaze.data.models.AddPostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PostApiService {

    @Multipart
    @POST("/api/posts")
    suspend fun addPost(
        @Part("content") content: RequestBody,
        @Part("hashtags") hashtags: RequestBody,
        @Part image: MultipartBody.Part?,
        @Part video: MultipartBody.Part?
    ): Response<AddPostResponse>

}