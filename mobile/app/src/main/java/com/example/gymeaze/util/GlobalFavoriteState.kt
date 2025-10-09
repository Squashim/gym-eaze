package com.example.gymeaze.util

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GlobalFavoriteState private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: GlobalFavoriteState? = null

        fun getInstance(context: Context): GlobalFavoriteState {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: GlobalFavoriteState(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val prefs: SharedPreferences = context.getSharedPreferences("favorite_plans", Context.MODE_PRIVATE)

    private val _favoriteStates = MutableStateFlow<Map<String, Boolean>>(loadFavoritesFromPrefs())
    val favoriteStates: StateFlow<Map<String, Boolean>> = _favoriteStates.asStateFlow()

    private fun loadFavoritesFromPrefs(): Map<String, Boolean> {
        val favorites = mutableMapOf<String, Boolean>()
        val allFavorites = prefs.getStringSet("favorite_plan_ids", emptySet()) ?: emptySet()

        allFavorites.forEach { planId ->
            favorites[planId] = true
        }

        return favorites
    }

    private fun saveFavoritesToPrefs() {
        val favoriteIds = _favoriteStates.value.filter { it.value }.keys
        prefs.edit()
            .putStringSet("favorite_plan_ids", favoriteIds)
            .apply()

    }

    fun toggleFavorite(planId: String): Boolean {
        val currentStates = _favoriteStates.value.toMutableMap()
        val currentState = currentStates[planId] ?: false
        val newState = !currentState

        currentStates[planId] = newState
        _favoriteStates.value = currentStates

        saveFavoritesToPrefs()

        return newState
    }

    fun isFavorite(planId: String): Boolean {
        return _favoriteStates.value[planId] ?: false
    }

    fun setFavorite(planId: String, isFavorite: Boolean) {
        val currentStates = _favoriteStates.value.toMutableMap()
        currentStates[planId] = isFavorite
        _favoriteStates.value = currentStates

        saveFavoritesToPrefs()
    }
}