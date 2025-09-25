package com.example.gymeaze.data.models

import com.google.gson.annotations.SerializedName

data class Exercise(
    @SerializedName("_id") val id: String,
    val name: String,
    val description: String,
    val image: String,
    val video: String,
    val likes: Int = 0,
    val visibility: String,
    val advancedLevel: String,
    val muscleGroup: List<MuscleGroup>,
    val equipment: List<Equipment>,
    val hashtags: List<String> = emptyList(),
    val author: ExerciseAuthor,
    val createdAt: String,
    val updatedAt: String
)

data class ExerciseAuthor(
    @SerializedName("_id") val id: String,
    val username: String,
    val profileDetails: ProfileDetails? = null
)


data class ExercisesResponse(
    val items: List<Exercise>,
    val total: Int,
    val page: Int,
    val pageSize: Int
)

data class ExerciseFilters(
    val search: String? = null,
    val muscleGroups: List<String>? = null,
    val equipment: List<String>? = null,
    val hashtags: List<String>? = null,
    val difficulty: String? = null,
    val followers: Boolean? = null
)

data class ExerciseQuery(
    val filters: ExerciseFilters = ExerciseFilters(),
    val sortBy: String = "latest",
    val page: Int = 1,
    val pageSize: Int = 9
)

data class LikeResponse(
    val message: String,
    val likes: Int
)