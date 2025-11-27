package com.example.gymeaze.constants

import com.example.gymeaze.R

object Goals {
    const val NONE = "Nie wybrano"
    const val RECOVERY = "Regeneracja i odpoczynek"
    const val STRENGTH = "Zwiększanie siły"
    const val POSTURE = "Poprawa postawy ciała"
    const val MASS_GAIN = "Zwiększenie masy ciała"
    const val COORDINATION = "Poprawa koordynacji"
    const val CALORIE_BURN = "Spalanie kalorii"
    const val MOBILITY = "Poprawa mobilności"
    const val LEGS_GLUTES = "Rozwój nóg i pośladków"
    const val ENDURANCE = "Poprawa wytrzymałości"
    const val FAT_LOSS = "Redukcja tkanki tłuszczowej"
    const val MUSCLE_GAIN = "Budowanie masy mięśniowej"
    const val FLEXIBILITY = "Poprawa gibkości"

    fun getLabelResId(backendValue: String): Int {
        return when (backendValue) {
            MUSCLE_GAIN -> R.string.goal_muscle_gain
            STRENGTH -> R.string.goal_strength
            ENDURANCE -> R.string.goal_endurance
            FAT_LOSS -> R.string.goal_fat_loss
            CALORIE_BURN -> R.string.goal_calorie_burn
            MASS_GAIN -> R.string.goal_mass_gain
            COORDINATION -> R.string.goal_coordination
            MOBILITY -> R.string.goal_mobility
            FLEXIBILITY -> R.string.goal_flexibility
            LEGS_GLUTES -> R.string.goal_legs_glutes
            POSTURE -> R.string.goal_posture
            RECOVERY -> R.string.goal_recovery
            else -> R.string.goal_none
        }
    }

    fun getAllOptions(): List<String> = listOf(
        NONE, MUSCLE_GAIN, STRENGTH, ENDURANCE, FAT_LOSS,
        CALORIE_BURN, MASS_GAIN, COORDINATION, MOBILITY,
        FLEXIBILITY, LEGS_GLUTES, POSTURE, RECOVERY
    )
}
