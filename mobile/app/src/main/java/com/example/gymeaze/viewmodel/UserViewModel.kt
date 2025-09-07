package com.example.gymeaze.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.UserProfileDetails
import com.example.gymeaze.data.repository.AuthRepository
import com.example.gymeaze.data.repository.ExerciseRepository
import com.example.gymeaze.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(application)

    // StateFlow przechowuje aktualny stan profilu
    private val _userProfile = MutableStateFlow<UserProfileDetails?>(null)
    val userProfile: StateFlow<UserProfileDetails?> = _userProfile

    // StateFlow do przechowywania błędów
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // StateFlow do statusu ładowania
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Funkcja do pobrania danych użytkownika
    fun fetchUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Sprawdź token przed wywołaniem repository
            val authRepository = AuthRepository(getApplication())
            val refreshResult = authRepository.refreshAccessToken()

            if (refreshResult.isFailure) {
                _error.value = "Session expired. Please log in again."
                _isLoading.value = false
                return@launch
            }

            val result = userRepository.getUserProfileDetails()
            result.fold(
                onSuccess = { profile ->
                    _userProfile.value = profile
                    _error.value = null
                },
                onFailure = { throwable ->
                    _error.value = throwable.message ?: "Unknown error occurred"
                    _userProfile.value = null
                }
            )
            _isLoading.value = false
        }
    }

    // Funkcja do odświeżenia danych
    fun refreshUserProfile() {
        fetchUserProfile()
    }
}
