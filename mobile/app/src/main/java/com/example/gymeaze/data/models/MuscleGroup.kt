package com.example.gymeaze.data.models

data class MuscleGroup(
    val _id: String,
    val name: String,
    val description: String? = null,
    val image: String? = null
)

data class Equipment(
    val _id: String,
    val name: String,
    val description: String? = null,
    val image: String? = null,
    val imageCredit: String? = null
)