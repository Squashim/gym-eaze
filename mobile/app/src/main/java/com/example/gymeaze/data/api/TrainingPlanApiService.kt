package com.example.gymeaze.data.api

import com.example.gymeaze.data.models.TrainingPlan
import com.example.gymeaze.data.models.TrainingPlansResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface TrainingPlanApiService {

    @Multipart
    @POST("/api/training-plans")
    suspend fun createTrainingPlan(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("goal") goal: RequestBody,
        @Part("visibility") visibility: RequestBody,
        @Part("advancedLevel") advancedLevel: RequestBody,
        @Part("hashtags") hashtags: RequestBody,
        @Part("days") days: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<TrainingPlan>

    @GET("/api/training-plans")
    suspend fun getTrainingPlans(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 9,
        @Query("sortBy") sortBy: String = "latest"
    ): Response<TrainingPlansResponse>

    @GET("/api/training-plans/{id}")
    suspend fun getTrainingPlanById(
        @Path("id") id: String
    ): Response<TrainingPlan>

    @PATCH("/api/training-plans/{id}/like")
    suspend fun toggleTrainingPlanLike(
        @Path("id") id: String
    ): Response<ResponseBody>

    @DELETE("/api/training-plans/{id}")
    suspend fun deleteTrainingPlan(
        @Path("id") id: String
    ): Response<ResponseBody>
}