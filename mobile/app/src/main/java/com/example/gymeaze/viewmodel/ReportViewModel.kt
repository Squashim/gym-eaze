package com.example.gymeaze.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ReportRepository(application)
    private val _reportState = MutableStateFlow<ReportState>(ReportState.Idle)
    val reportState: StateFlow<ReportState> = _reportState.asStateFlow()

    fun reportItem(
        itemId: String,
        itemType: ReportItemType,
        reportType: ReportType,
        reason: String? = null
    ) {
        viewModelScope.launch {
            try {
                _reportState.value = ReportState.Loading

                val response = repository.reportItem(
                    itemId = itemId,
                    itemType = itemType.value,
                    reportType = reportType.value,
                    reason = reason
                )

                if (response != null) {
                    _reportState.value = ReportState.Success("Zgłoszenie zostało wysłane")
                } else {
                    _reportState.value = ReportState.Error("Nie udało się wysłać zgłoszenia")
                }
            } catch (e: Exception) {
                _reportState.value = ReportState.Error(
                    e.message ?: "Wystąpił błąd"
                )
            }
        }
    }

    fun resetState() {
        _reportState.value = ReportState.Idle
    }
}

sealed class ReportState {
    object Idle : ReportState()
    object Loading : ReportState()
    data class Success(val message: String) : ReportState()
    data class Error(val message: String) : ReportState()
}

enum class ReportItemType(val value: String, val displayName: String) {
    EXERCISE("Ćwiczenie", "Ćwiczenie"),
    TRAINING("Trening", "Trening"),
    POST("Post", "Post"),
    COMMENT("Komentarz", "Komentarz")
}

enum class ReportType(val value: String, val displayName: String) {
    INAPPROPRIATE_CONTENT("Nieodpowiednia treść", "Nieodpowiednia treść"),
    SPAM_OR_AD("Spam lub reklama", "Spam lub reklama"),
    MISINFORMATION_OR_DANGEROUS("Dezinformacja lub niebezpieczne treści", "Dezinformacja lub niebezpieczne treści"),
    PRIVACY_VIOLATION("Naruszenie prywatności", "Naruszenie prywatności"),
    COPYRIGHT_VIOLATION("Naruszenie praw autorskich", "Naruszenie praw autorskich"),
    OFF_TOPIC_OR_LOW_QUALITY("Treść nie na temat / niskiej jakości", "Treść nie na temat / niskiej jakości"),
    USER_BEHAVIOR("Nieodpowiednie zachowanie użytkownika", "Nieodpowiednie zachowanie użytkownika"),
    OTHER("Inny powód", "Inny powód")
}