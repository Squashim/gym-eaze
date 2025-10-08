package com.example.gymeaze.data.api

import com.example.gymeaze.data.models.AddPostResponse
import com.example.gymeaze.data.models.CreateCommentRequest
import com.example.gymeaze.data.models.GetCommentsResponse
import com.example.gymeaze.data.models.GetPostsResponse
import com.example.gymeaze.data.models.LikesResponse
import com.example.gymeaze.data.models.MessageCommentResponse
import com.example.gymeaze.data.models.MessageResponse
import com.example.gymeaze.data.models.PostResponse
import com.example.gymeaze.data.models.ToggleLikeCommentResponse
import com.example.gymeaze.data.models.ToggleLikeResponse
import com.example.gymeaze.data.models.UpdateCommentRequest
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
    ): Response<MessageCommentResponse>

    @DELETE("/api/posts/{id}")
    suspend fun deletePost(
        @Path("id") postId: String
    ): Response<MessageCommentResponse>

    @GET("/api/posts/{userId}")
    suspend fun getUserPosts(
        @Path("userId") userId: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 10
    ): PostResponse

    @GET("/api/posts/{id}/comments")
    suspend fun getComments(
        @Path("id") postId: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 10
    ): Response<GetCommentsResponse>

    @GET("/api/comments/{id}/replies")
    suspend fun getReplies(
        @Path("id") commentId: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 10
    ): Response<GetCommentsResponse>

    @POST("/api/comments")
    suspend fun createComment(
        @Body body: CreateCommentRequest
    ): Response<MessageCommentResponse>

    @PATCH("/api/comments/{id}/like")
    suspend fun toggleCommentLike(
        @Path("id") commentId: String
    ): Response<ToggleLikeCommentResponse>

    @PATCH("/api/comments/{id}")
    suspend fun updateComment(
        @Path("id") commentId: String,
        @Body body: UpdateCommentRequest
    ): Response<MessageCommentResponse>

    @DELETE("/api/comments/{id}")
    suspend fun deleteComment(
        @Path("id") commentId: String
    ): Response<MessageCommentResponse>
}