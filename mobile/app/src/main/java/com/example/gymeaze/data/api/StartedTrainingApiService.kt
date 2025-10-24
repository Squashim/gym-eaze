package com.example.gymeaze.data.api

import com.example.gymeaze.data.models.*
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StartedTrainingApiService {

    @GET("/api/started-training-plans/user/{userId}")
    suspend fun getStartedTrainingPlans(
        @Path("userId") userId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): Response<StartedTrainingPlansResponse>

    @POST("/api/started-training-plans/{trainingPlanId}/start")
    suspend fun startTrainingPlan(
        @Path("trainingPlanId") trainingPlanId: String
    ): Response<StartTrainingResponse>

    @DELETE("/api/started-training-plans/{userTrainingPlanId}")
    suspend fun deleteStartedTrainingPlan(
        @Path("userTrainingPlanId") userTrainingPlanId: String
    ): Response<MessageResponse>


    @PATCH("/api/started-training-plans/{userTrainingPlanId}/reset")
    suspend fun resetStartedTrainingPlan(
        @Path("userTrainingPlanId") userTrainingPlanId: String
    ): Response<MessageResponse>

    @GET("/api/started-training-plans/{userTrainingPlanId}/days")
    suspend fun getStartedTrainingPlanDays(
        @Path("userTrainingPlanId") userTrainingPlanId: String
    ): Response<StartedTrainingPlanDaysResponse>

    @GET("/api/started-training-plans/{userTrainingPlanId}/days/{dayNumber}")
    suspend fun getStartedTrainingPlanDayDetails(
        @Path("userTrainingPlanId") userTrainingPlanId: String,
        @Path("dayNumber") dayNumber: Int
    ): Response<StartedDayDetails>

    @PATCH("/api/started-training-plans/{userTrainingPlanId}/days/{dayNumber}/complete")
    suspend fun completeDayTraining(
        @Path("userTrainingPlanId") userTrainingPlanId: String,
        @Path("dayNumber") dayNumber: Int
    ): Response<CompleteDayResponse>
}