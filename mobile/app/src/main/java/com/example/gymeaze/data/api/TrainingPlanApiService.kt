package com.example.gymeaze.data.api

import com.example.gymeaze.data.models.GetCommentsResponse
import com.example.gymeaze.data.models.LikeResponse
import com.example.gymeaze.data.models.MessageResponse
import com.example.gymeaze.data.models.TrainingPlan
import com.example.gymeaze.data.models.TrainingPlanQuery
import com.example.gymeaze.data.models.TrainingPlansResponse
import com.example.gymeaze.data.models.UserFavorites
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface TrainingPlanApiService {

    @Multipart
    @POST("/api/training-plans")
    suspend fun createTrainingPlan(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("goal") goal: RequestBody,
        @Part("visibility") visibility: RequestBody,
        @Part("advancedLevel") advancedLevel: RequestBody,
        @Part("hashtags") hashtags: RequestBody,
        @Part("days") days: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<ResponseBody>

    @POST("/api/training-plans/search")
    suspend fun getTrainingPlans(
        @Body query: TrainingPlanQuery
    ): Response<TrainingPlansResponse>

    @GET("/api/training-plans/{id}")
    suspend fun getTrainingPlanById(
        @Path("id") planId: String
    ): Response<TrainingPlan>

    @GET("/api/training-plans/user/me")
    suspend fun getMyTrainingPlans(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 9
    ): Response<TrainingPlansResponse>

    @GET("/api/training-plans/user/{userId}")
    suspend fun getMyTrainingPlansByUserId(
        @Path("userId") userId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 9
    ): Response<TrainingPlansResponse>

    @GET("/api/training-plans/favorite")
    suspend fun getFavoriteTrainingPlans(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 9
    ): Response<TrainingPlansResponse>

    @PATCH("/api/training-plans/{id}/like")
    suspend fun toggleTrainingPlanLike(
        @Path("id") planId: String
    ): Response<LikeResponse>

    @PATCH("/api/training-plans/{id}/favorite")
    suspend fun toggleTrainingPlanFavorite(
        @Path("id") planId: String
    ): Response<MessageResponse>


    @Multipart
    @PATCH("/api/training-plans/{id}")
    suspend fun updateTrainingPlan(
        @Path("id") planId: String,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("goal") goal: RequestBody,
        @Part("visibility") visibility: RequestBody,
        @Part("advancedLevel") advancedLevel: RequestBody,
        @Part("hashtags") hashtags: RequestBody,
        @Part("days") days: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<TrainingPlan>

    @DELETE("/api/training-plans/{id}")
    suspend fun deleteTrainingPlan(
        @Path("id") planId: String
    ): Response<ResponseBody>

    @GET("/api/training-plans/{id}/pdf")
    suspend fun downloadTrainingPlanPDF(
        @Path("id") planId: String
    ): Response<ResponseBody>

    @GET("/api/user/favorites")
    suspend fun getUserFavorites(): Response<UserFavorites>

    @GET("/api/training-plans/{id}/comments")
    suspend fun getTrainingPlanComments(
        @Path("id") planId: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 10
    ): Response<GetCommentsResponse>
}