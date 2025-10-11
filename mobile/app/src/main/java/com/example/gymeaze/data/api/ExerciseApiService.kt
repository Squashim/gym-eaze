package com.example.gymeaze.data.api

import com.example.gymeaze.data.models.Equipment
import com.example.gymeaze.data.models.ExerciseQuery
import com.example.gymeaze.data.models.ExercisesResponse
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.data.models.FavoriteResponse
import com.example.gymeaze.data.models.LikeResponse
import com.example.gymeaze.data.models.MuscleGroup
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Multipart

interface ExerciseApiService {

    @GET("/api/muscleGroup")
    suspend fun getMuscleGroups(): Response<List<MuscleGroup>>

    @GET("/api/equipment")
    suspend fun getEquipment(): Response<List<Equipment>>

    @POST("/api/exercises/search")
    suspend fun getExercises(
        @Body query: ExerciseQuery
    ): Response<ExercisesResponse>

    @GET("/api/exercises/{id}")
    suspend fun getExerciseById(
        @Path("id") exerciseId: String
    ): Response<Exercise>

    @POST("/api/exercises/batch")
    suspend fun getExercisesByIds(
        @Body exerciseIds: List<String>
    ): Response<List<Exercise>>

    @PATCH("/api/exercises/{id}/like")
    suspend fun toggleExerciseLike(
        @Path("id") exerciseId: String
    ): Response<LikeResponse>

    @Multipart
    @POST("/api/exercises")
    suspend fun createExercise(
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

    @PATCH("/api/exercises/{id}/favorite")
    suspend fun toggleExerciseFavorite(@Path("id") exerciseId: String): Response<FavoriteResponse>
}