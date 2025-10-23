package com.example.gymeaze.data.models

import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.constants.Goals
import com.example.gymeaze.constants.PrivacySettings
import com.google.gson.annotations.SerializedName

data class TrainingPlan(
    @SerializedName("_id") val id: String,
    val name: String,
    val author: TrainingPlanAuthor? = null,
    val description: String? = null,
    val image: String,
    val goal: String,
    val visibility: String,
    val advancedLevel: String,
    val likes: Int = 0,
    val days: List<TrainingDay> = emptyList(),
    val hashtags: List<String> = emptyList(),
    val createdAt: String,
    val updatedAt: String,

    val totalExercises: Int? = null,
    val totalDuration: Int? = null,
    val daysCount: DaysCount? = null,
    val isFavorite: Boolean? = null
)

data class TrainingPlanAuthor(
    @SerializedName("_id") val id: String,
    val username: String,
    val profileDetails: ProfileDetails? = null
)

data class TrainingDay(
    val dayNumber: Int,
    val name: String,
    val totalDuration: Int? = null,
    val isRestDay: Boolean = false,
    val exercises: List<DayExercise> = emptyList()
)

data class DayExercise(
    val id: String,
    val exercise: Exercise? = null,
    val isRepsSet: Boolean = true,
    val duration: Int? = null,
    val restAfterSet: Int = 60,
    val sets: List<ExerciseSet> = emptyList()
)

data class ExerciseSet(
    val repetitions: Int? = null,
    val weight: Double? = null,
    val duration: Int? = null
)

data class CreateTrainingPlanRequest(
    val title: String,
    val description: String? = null,
    val goal: String,
    val visibility: String,
    val advancedLevel: String,
    val hashtags: List<String> = emptyList(),
    val days: List<CreateTrainingDay>
)

data class CreateTrainingDay(
    val name: String,
    val isRestDay: Boolean = false,
    val exercises: List<CreateDayExercise> = emptyList()
)

data class CreateDayExercise(
    val id: String,
    val isRepsSet: Boolean = true,
    val duration: Int? = null,
    val restAfterSet: Int = 60,
    val sets: List<CreateExerciseSet> = emptyList()
)

data class CreateExerciseSet(
    val repetitions: Int,
    val weight: Double = 0.0
)

data class TrainingPlanFormData(
    val title: String = "",
    val description: String = "",
    val goal: String = Goals.NONE,
    val visibility: String = PrivacySettings.PUBLIC,
    val advancedLevel: String = AdvancedLevel.EASY,
    val hashtags: List<String> = emptyList(),
    val days: List<TrainingDayFormData> = emptyList(),
    val selectedImageUri: String? = null
)

data class TrainingDayFormData(
    val name: String = "",
    val isRestDay: Boolean = false,
    val exercises: List<DayExerciseFormData> = emptyList()
)

data class DayExerciseFormData(
    val exerciseId: String = "",
    val exercise: Exercise? = null,
    val isRepsSet: Boolean = true,
    val duration: Int? = null,
    val restAfterSet: Int = 60,
    val sets: List<ExerciseSetFormData> = listOf(ExerciseSetFormData())
)

data class ExerciseSetFormData(
    val repetitions: Int = 10,
    val weight: Double = 0.5
)

data class TrainingPlansResponse(
    val items: List<TrainingPlan>,
    val total: Int,
    val page: Int,
    val pageSize: Int
)

data class TrainingPlanFilters(
    val search: String? = null,
    val goal: String? = null,
    val difficulty: String? = null,
    val minDuration: Int? = null,
    val maxDuration: Int? = null,
    val hashtags: List<String>? = null,
    val followers: Boolean? = null
)

data class TrainingPlanQuery(
    val filters: TrainingPlanFilters = TrainingPlanFilters(),
    val sortBy: String = "latest",
    val page: Int = 1,
    val pageSize: Int = 9
)

data class DaysCount(
    val total: Int,
    val rest: Int,
    val training: Int
)

data class UpdateTrainingPlanData(
    val name: String,
    val description: String?,
    val goal: String,
    val visibility: String,
    val advancedLevel: String,
    val hashtags: List<String>,
    val days: List<UpdateTrainingDay>,
    val imageUri: String? = null
)

data class UpdateTrainingDay(
    val dayNumber: Int,
    val name: String,
    val isRestDay: Boolean,
    val exercises: List<UpdateDayExercise>
)

data class UpdateDayExercise(
    val id: String,
    val isRepsSet: Boolean,
    val duration: Int?,
    val restAfterSet: Int,
    val sets: List<UpdateExerciseSet>
)

data class UpdateExerciseSet(
    val repetitions: Int?,
    val weight: Double?,
    val duration: Int?
)

data class DeleteResponse(
    val message: String
)


