package com.example.gymeaze.data.models

data class Hashtag(
    val _id: String,
    val name: String,
    val description: String? = null,
    val category: String? = null,
    val usageCount: Int = 0
)