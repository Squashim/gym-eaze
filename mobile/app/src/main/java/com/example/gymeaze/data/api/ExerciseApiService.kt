package com.example.gymeaze.data.api

import com.example.gymeaze.data.models.Equipment
import com.example.gymeaze.data.models.MuscleGroup
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ExerciseApiService {

    @GET("/api/muscleGroup")
    suspend fun getMuscleGroups(): Response<List<MuscleGroup>>

    @GET("/api/equipment")
    suspend fun getEquipment(): Response<List<Equipment>>

    @Multipart
    @POST("/api/exercises")
    suspend fun createExercise(
        @Header("Authorization") auth: String,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("advancedLevel") advancedLevel: RequestBody,
        @Part("visibility") visibility: RequestBody,
        @Part image: MultipartBody.Part,
        @Part video: MultipartBody.Part,
        @Part("muscleGroup") muscleGroup: RequestBody,
        @Part("equipment") equipment: RequestBody,
        @Part("hashtags") hashtags: RequestBody,
    ): Response<ResponseBody>
}