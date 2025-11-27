package com.example.gymeaze.constants

import com.example.gymeaze.R

object AdvancedLevel {
    const val NONE = "Nie wybrano"
    const val EASY = "Początkujący"
    const val MEDIUM = "Średniozaawansowany"
    const val HARD = "Zaawansowany"

    fun getLabelResId(backendValue: String?): Int {
        return when (backendValue) {
            EASY -> R.string.level_easy
            MEDIUM -> R.string.level_medium
            HARD -> R.string.level_hard
            else -> R.string.level_none
        }
    }

    fun getAllOptions(): List<String> = listOf(
        NONE,
        EASY,
        MEDIUM,
        HARD
    )
}