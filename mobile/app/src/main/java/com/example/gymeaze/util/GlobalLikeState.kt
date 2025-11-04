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
import kotlin.onFailure

class GlobalLikeState private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: GlobalLikeState? = null

        fun getInstance(context: Context): GlobalLikeState {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: GlobalLikeState(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _likedTrainingPlanIds = MutableStateFlow<Set<String>>(emptySet())
    val likedTrainingPlanIds: StateFlow<Set<String>> = _likedTrainingPlanIds.asStateFlow()

    private val trainingPlanRepository = TrainingPlanRepository(context)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var lastRefreshTime = 0L
    private val REFRESH_INTERVAL = 5000L

    init {
        loadLikesFromBackend()
    }

    fun loadLikesFromBackend() {
        scope.launch {
            try {
                _isLoading.value = true

                val result = trainingPlanRepository.getUserLikes()

                result.onSuccess { userLikes ->
                    val likesIds = userLikes.trainingPlans.toSet()
                    _likedTrainingPlanIds.value = likesIds
                    lastRefreshTime = System.currentTimeMillis()

                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleLike(planId: String): Boolean {
        val currentLikes = _likedTrainingPlanIds.value.toMutableSet()
        val wasInLikes = currentLikes.contains(planId)
        val newState = !wasInLikes

        if (wasInLikes) {
            currentLikes.remove(planId)
        } else {
            currentLikes.add(planId)
        }

        _likedTrainingPlanIds.value = currentLikes

        scope.launch {
            kotlinx.coroutines.delay(2000)
            loadLikesFromBackend()
        }

        return newState
    }

    fun isLiked(planId: String): Boolean {
        return _likedTrainingPlanIds.value.contains(planId)
    }

    fun refreshFromBackend() {
        val now = System.currentTimeMillis()
        if (now - lastRefreshTime > REFRESH_INTERVAL) {
            loadLikesFromBackend()
        }
    }
}