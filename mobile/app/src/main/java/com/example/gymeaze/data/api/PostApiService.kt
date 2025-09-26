package com.example.gymeaze.data.api

import com.example.gymeaze.data.models.AddPostResponse
import com.example.gymeaze.data.models.GetPostsResponse
import com.example.gymeaze.data.models.LikesResponse
import com.example.gymeaze.data.models.MessageResponse
import com.example.gymeaze.data.models.PostResponse
import com.example.gymeaze.data.models.ToggleLikeResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApiService {

    @Multipart
    @POST("/api/posts")
    suspend fun addPost(
        @Part("content") content: RequestBody,
        @Part("hashtags") hashtags: RequestBody,
        @Part image: MultipartBody.Part?,
        @Part video: MultipartBody.Part?
    ): Response<AddPostResponse>

    @GET("/api/posts")
    suspend fun getPosts(
        @Query("hashtags") hashtags: List<String>? = null,
        @Query("followedOnly") followedOnly: Boolean? = null,
        @Query("search") search: String? = null,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 10
    ): Response<GetPostsResponse>

    @PATCH("/api/posts/{id}/like")
    suspend fun toggleLike(
        @Path("id") postId: String
    ): Response<ToggleLikeResponse>

    @GET("/api/user/likes")
    suspend fun getUserLikes(): Response<LikesResponse>

    @PATCH("/api/posts/{id}")
    suspend fun updatePost(
        @Path("id") postId: String,
        @Body requestBody: RequestBody
    ): Response<MessageResponse>

    @DELETE("/api/posts/{id}")
    suspend fun deletePost(
        @Path("id") postId: String
    ): Response<MessageResponse>

    @GET("/api/posts/{userId}")
    suspend fun getUserPosts(
        @Path("userId") userId: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 10
    ): PostResponse
}