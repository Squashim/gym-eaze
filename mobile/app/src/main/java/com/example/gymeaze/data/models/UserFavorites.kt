package com.example.gymeaze.data.models

data class UserFavorites(
    val trainingPlans: List<String> = emptyList(),
    val exercises: List<String> = emptyList()
)