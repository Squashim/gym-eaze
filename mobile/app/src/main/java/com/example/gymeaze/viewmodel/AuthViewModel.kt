package com.example.gymeaze.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymeaze.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isRegistering: Boolean = true, //true - rejestracja, false - logowanie
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val confirmPassword: String = "",
    val usernameOrEmail: String = "",
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application.applicationContext)
    private val userPreferences = authRepository.getPreferences()

    private val privateUiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = privateUiState.asStateFlow()

    val isLoggedIn = userPreferences.isLoggedIn
    val isFirstLogin = userPreferences.firstLogin
    val isDarkTheme = userPreferences.isDarkTheme

    fun toggleAuthMode() {
        privateUiState.value = privateUiState.value.copy(
            isRegistering = !privateUiState.value.isRegistering,
            errorMessage = null
        )
    }

    fun updateUsername(username: String){
        privateUiState.value = privateUiState.value.copy(username = username)

    }

    fun updateEmail(email: String){
        privateUiState.value = privateUiState.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        privateUiState.value = privateUiState.value.copy(password = password)
    }

    fun updateConfirmPassword(confirmPassword: String){
        privateUiState.value = privateUiState.value.copy(confirmPassword = confirmPassword)
    }

    fun updateUsernameOrEmail(usernameOrEmail: String) {
        privateUiState.value = privateUiState.value.copy(usernameOrEmail = usernameOrEmail)
    }

    fun register() {
        val currentState = privateUiState.value

        if(currentState.password!=currentState.confirmPassword){
            privateUiState.value = currentState.copy(errorMessage = "Hasła nie są identyczne!")
            return
        }

        privateUiState.value = currentState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch{
            val result = authRepository.register(
                currentState.username,
                currentState.email,
                currentState.password
            )
            if(result.isSuccess) {
                privateUiState.value = currentState.copy(
                    isLoading = false,
                    isLoggedIn = true
                )
            } else {
                privateUiState.value = currentState.copy(
                    isLoading = false,
                    errorMessage = "Błąd rejestracji! ${result.exceptionOrNull()?.message}"
                )
            }


        }
    }

    fun login() {
        val currentState = privateUiState.value
        privateUiState.value = currentState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = authRepository.login(
                currentState.usernameOrEmail,
                currentState.password
            )
            if (result.isSuccess){
                privateUiState.value = currentState.copy(
                    isLoading = false,
                    isLoggedIn = true
                )
            } else{
                privateUiState.value = currentState.copy(
                    isLoading = false,
                    errorMessage = "Błąd logowania! ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    fun loginWithGoogle() {
        privateUiState.value = privateUiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = authRepository.loginWithGoogle()

            if (result.isSuccess){
                privateUiState.value = privateUiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true
                )
            } else {
                privateUiState.value = privateUiState.value.copy(
                    isLoading = false,
                    errorMessage = "Błąd logowania przez Google: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            privateUiState.value = AuthUiState() //Stan poczatkowy
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            val currentTheme = userPreferences.isDarkTheme.first()
            userPreferences.setDarkTheme(!currentTheme)
            }
    }

    fun clearFirstLogin(){
        viewModelScope.launch {
            userPreferences.setFirstLogin(false)
        }
    }

    private val privateShowSettings = MutableStateFlow(false)
    val showSettings = privateShowSettings.asStateFlow()

    private val privateShowThemeSelection = MutableStateFlow(false)
    val showThemeSelection = privateShowThemeSelection.asStateFlow()

    fun showSettingsScreen() {
        privateShowSettings.value = true
    }

    fun hideSettingsScreen() {
        privateShowSettings.value = false
    }

    fun showThemeSelectionScreen() {
        privateShowThemeSelection.value = true
    }

    fun hideThemeSelectionScreen() {
        privateShowThemeSelection.value = false
    }

    fun selectTheme(isDark: Boolean){
        viewModelScope.launch {
            userPreferences.setDarkTheme(isDark)
        }
    }
}