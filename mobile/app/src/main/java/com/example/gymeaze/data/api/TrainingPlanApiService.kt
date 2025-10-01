package com.example.gymeaze.data.api

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

}