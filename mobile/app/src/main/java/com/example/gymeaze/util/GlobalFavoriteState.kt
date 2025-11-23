package com.example.gymeaze.util

import android.content.Context
import com.example.gymeaze.data.repository.TrainingPlanRepository
import com.example.gymeaze.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    private val userRepository = UserRepository(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _favoriteTrainingPlanIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteTrainingPlanIds: StateFlow<Set<String>> = _favoriteTrainingPlanIds.asStateFlow()

    private val trainingPlanRepository = TrainingPlanRepository(context)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var lastRefreshTime = 0L
    private val REFRESH_INTERVAL = 5000L

    init {
        loadFavoritesFromBackend()
    }

    fun loadFavoritesFromBackend() {
        scope.launch {
            try {
                _isLoading.value = true

                val result = trainingPlanRepository.getUserFavorites()

                result.onSuccess { userFavorites ->
                    val favoriteIds = userFavorites.trainingPlans.toSet()
                    _favoriteTrainingPlanIds.value = favoriteIds
                    lastRefreshTime = System.currentTimeMillis()

                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(planId: String): Boolean {
        val currentFavorites = _favoriteTrainingPlanIds.value.toMutableSet()
        val wasInFavorites = currentFavorites.contains(planId)
        val newState = !wasInFavorites

        if (wasInFavorites) {
            currentFavorites.remove(planId)
        } else {
            currentFavorites.add(planId)
        }

        _favoriteTrainingPlanIds.value = currentFavorites

        scope.launch {
            kotlinx.coroutines.delay(2000)
            loadFavoritesFromBackend()
        }

        return newState
    }

    fun isFavorite(planId: String): Boolean {
        return _favoriteTrainingPlanIds.value.contains(planId)
    }

    fun refreshFromBackend() {
        val now = System.currentTimeMillis()
        if (now - lastRefreshTime > REFRESH_INTERVAL) {
            loadFavoritesFromBackend()
        }
    }

    fun forceRefreshFromBackend() {
        loadFavoritesFromBackend()
    }
}