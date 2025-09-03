package com.example.gymeaze.data.api

import com.example.gymeaze.data.models.Equipment
import com.example.gymeaze.data.models.Hashtag
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
import retrofit2.http.Query

interface ExerciseApiService {

    @GET("/api/muscleGroup")
    suspend fun getMuscleGroups(): Response<List<MuscleGroup>>

    @GET("/api/equipment")
    suspend fun getEquipment(): Response<List<Equipment>>

    @GET("/api/hashtags")
    suspend fun getHashtags(): Response<List<Hashtag>>

    @GET("/api/hashtags/popular")
    suspend fun getPopularHashtags(@Query("limit") limit: Int = 20): Response<List<Hashtag>>

    @Multipart
    @POST("/api/exercises")
    suspend fun createExercise(
        @Header("Authorization") auth: String,
        @Part("author") author: RequestBody,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("advancedLevel") advancedLevel: RequestBody,
        @Part("visibility") visibility: RequestBody,
        @Part image: MultipartBody.Part,
        @Part video: MultipartBody.Part,
        @Part("muscleGroup") muscleGroup: Array<RequestBody>,
        @Part("equipment") equipment: Array<RequestBody>,
        @Part("hashtags") hashtags: Array<RequestBody>,
    ): Response<ResponseBody>
}