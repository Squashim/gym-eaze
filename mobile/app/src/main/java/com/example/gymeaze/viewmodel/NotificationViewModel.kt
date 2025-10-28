package com.example.gymeaze.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.Notification
import com.example.gymeaze.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.fold

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NotificationRepository(application)

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        loadNotifications(isRefresh = true)
    }

    fun loadNotifications(isRefresh: Boolean = false) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val cursor = if (isRefresh) null else _uiState.value.nextCursor

            repository.getNotifications(limit = 10, cursor = cursor).fold(
                onSuccess = { response ->
                    val currentItems = if (isRefresh) emptyList() else _uiState.value.notifications
                    _uiState.value = _uiState.value.copy(
                        notifications = currentItems + response.items,
                        hasMore = response.hasMore,
                        nextCursor = response.nextCursor,
                        total = response.total,
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Wystąpił błąd podczas ładowania powiadomień"
                    )
                }
            )
        }
    }

    fun deleteAllNotifications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeleting = true, error = null)

            repository.deleteAllNotifications().fold(
                onSuccess = { response ->
                    _uiState.value = NotificationUiState(
                        successMessage = response.message
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isDeleting = false,
                        error = error.message ?: "Nie udało się usunąć powiadomień"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    fun refresh() {
        loadNotifications(isRefresh = true)
    }
}

data class NotificationUiState(
    val notifications: List<Notification> = emptyList(),
    val hasMore: Boolean = false,
    val nextCursor: String? = null,
    val total: Int = 0,
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)