package com.example.gymeaze.util

import com.example.gymeaze.constants.AdvancedLevel
import com.example.gymeaze.constants.Goals
import com.example.gymeaze.constants.PrivacySettings

object EnumMappers {

    fun mapAdvancedLevelToBackend(androidValue: String): String {
        return when (androidValue) {
            AdvancedLevel.NONE -> "Nie wybrano"
            AdvancedLevel.EASY -> "Początkujący"
            AdvancedLevel.MEDIUM -> "Średniozaawansowany"
            AdvancedLevel.HARD -> "Zaawansowany"
            else -> "Nie wybrano"
        }
    }

    fun mapGoalToBackend(androidValue: String): String {
        return when (androidValue) {
            Goals.NONE -> "Nie wybrano"
            Goals.RECOVERY -> "Regeneracja i odpoczynek"
            Goals.STRENGTH -> "Zwiększanie siły"
            Goals.POSTURE -> "Poprawa postawy ciała"
            Goals.MASS_GAIN -> "Zwiększenie masy ciała"
            Goals.COORDINATION -> "Poprawa koordynacji"
            Goals.CALORIE_BURN -> "Spalanie kalorii"
            Goals.MOBILITY -> "Poprawa mobilności"
            Goals.LEGS_GLUTES -> "Rozwój nóg i pośladków"
            Goals.ENDURANCE -> "Poprawa wytrzymałości"
            Goals.FAT_LOSS -> "Redukcja tkanki tłuszczowej"
            Goals.MUSCLE_GAIN -> "Budowanie masy mięśniowej"
            Goals.FLEXIBILITY -> "Poprawa gibkości"
            else -> "Nie wybrano"
        }
    }

    fun mapPrivacyToBackend(androidValue: String): String {
        return when (androidValue) {
            PrivacySettings.PUBLIC -> "Publiczny"
            PrivacySettings.PRIVATE -> "Prywatny"
            else -> "Publiczny"
        }
    }

    fun mapAdvancedLevelFromBackend(backendValue: String): String {
        return when (backendValue) {
            "Początkujący" -> AdvancedLevel.EASY
            "Średniozaawansowany" -> AdvancedLevel.MEDIUM
            "Zaawansowany" -> AdvancedLevel.HARD
            else -> AdvancedLevel.EASY
        }
    }

    fun mapGoalFromBackend(backendValue: String): String {
        return when (backendValue) {
            "Nie wybrano" -> Goals.NONE
            "Regeneracja i odpoczynek" -> Goals.RECOVERY
            "Zwiększanie siły" -> Goals.STRENGTH
            "Poprawa postawy ciała" -> Goals.POSTURE
            "Zwiększenie masy ciała" -> Goals.MASS_GAIN
            "Poprawa koordynacji" -> Goals.COORDINATION
            "Spalanie kalorii" -> Goals.CALORIE_BURN
            "Poprawa mobilności" -> Goals.MOBILITY
            "Rozwój nóg i pośladków" -> Goals.LEGS_GLUTES
            "Poprawa wytrzymałości" -> Goals.ENDURANCE
            "Redukcja tkanki tłuszczowej" -> Goals.FAT_LOSS
            "Budowanie masy mięśniowej" -> Goals.MUSCLE_GAIN
            "Poprawa gibkości" -> Goals.FLEXIBILITY
            else -> Goals.NONE
        }
    }

    fun mapPrivacyFromBackend(backendValue: String): String {
        return when (backendValue) {
            "Publiczny" -> PrivacySettings.PUBLIC
            "Prywatny" -> PrivacySettings.PRIVATE
            else -> PrivacySettings.PUBLIC
        }
    }
}