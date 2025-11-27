package com.example.gymeaze.data.models

import com.google.gson.annotations.SerializedName

data class StartedTrainingPlansResponse(
    val items: List<StartedTrainingPlan>,
    val total: Int,
    val page: Int,
    val pageSize: Int
)

data class StartedTrainingPlan(
    @SerializedName("_id") val id: String,
    val name: String,
    val image: String,
    val goal: String,
    val advancedLevel: String,
    val author: TrainingPlanAuthor,
    val status: String,
    val totalDays: Int,
    val trainingDays: Int,
    val restDays: Int,
    val completedDays: Int,
    val createdAt: String
)

data class StartTrainingResponse(
    val message: String,
    val userTrainingPlanId: String
)

data class StartedTrainingPlanDaysResponse(
    val days: List<StartedDay>
)

data class StartedDay(
    val dayNumber: Int,
    val name: String,
    val totalDuration: Int,
    val isRestDay: Boolean,
    val isCompleted: Boolean
)

data class StartedDayDetails(
    val dayNumber: Int,
    val name: String,
    val totalDuration: Int,
    val exercises: List<StartedExercise>
)

data class StartedExercise(
    val originalId: String,
    val name: String,
    val description: String,
    val image: String,
    val video: String,
    val muscleGroup: List<MuscleGroupSimple>,
    val equipment: List<EquipmentSimple>,
    val isRepsSet: Boolean,
    val duration: Int?,
    val restAfterSet: Int,
    val sets: List<ExerciseSet>
)

data class MuscleGroupSimple(
    val id: String,
    val name: String
)

data class EquipmentSimple(
    val id: String,
    val name: String
)

data class CompleteDayResponse(
    val message: String,
    val isTrainingCompleted: Boolean
)