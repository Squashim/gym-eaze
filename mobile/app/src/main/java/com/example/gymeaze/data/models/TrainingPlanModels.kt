package com.example.gymeaze.data.models

import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.constants.Goals
import com.example.gymeaze.constants.PrivacySettings
import com.google.gson.annotations.SerializedName

data class TrainingPlan(
    @SerializedName("_id") val id: String,
    val name: String,
    val author: TrainingPlanAuthor,
    val description: String? = null,
    val image: String,
    val goal: String,
    val visibility: String,
    val advancedLevel: String,
    val likes: Int = 0,
    val days: List<TrainingDay>,
    val hashtags: List<String> = emptyList(),
    val createdAt: String,
    val updatedAt: String
)

data class TrainingPlanAuthor(
    @SerializedName("_id") val id: String,
    val username: String,
    val profileDetails: ProfileDetails? = null
)

data class TrainingDay(
    val dayNumber: Int = 0,
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
    val repetitions: Int,
    val weight: Double = 0.0
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
    val repetitions: Int = 1,
    val weight: Double = 0.0
)

data class TrainingPlansResponse(
    val items: List<TrainingPlan>,
    val total: Int,
    val page: Int,
    val pageSize: Int
)