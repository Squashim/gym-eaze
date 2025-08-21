package com.example.gymeaze.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.AuthRepository
import com.example.gymeaze.util.Validation
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
    val isLoggedIn: Boolean = false,

    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val usernameOrEmailError: String? = null,
    val isRegisterValid: Boolean = false,
    val isLoginValid: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application.applicationContext)
    private val userPreferences = authRepository.getPreferences()

    private val privateUiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = privateUiState.asStateFlow()

    val isLoggedIn = userPreferences.isLoggedIn
    val isFirstLogin = userPreferences.firstLogin
    val isDarkTheme = userPreferences.isDarkTheme

    val username = userPreferences.username

    fun toggleAuthMode() {
        privateUiState.value = privateUiState.value.copy(
            isRegistering = !privateUiState.value.isRegistering,
            errorMessage = null
        ).also { validate(it) }
    }

    fun updateUsername(username: String){
        val newState = privateUiState.value.copy(username = username)
        privateUiState.value = newState
        validate(newState)
    }

    fun updateEmail(email: String){
        val newState = privateUiState.value.copy(email = email)
        privateUiState.value = newState
        validate(newState)
    }

    fun updatePassword(password: String) {
        val newState = privateUiState.value.copy(password = password)
        privateUiState.value = newState
        validate(newState)
    }

    fun updateConfirmPassword(confirmPassword: String){
        val newState = privateUiState.value.copy(confirmPassword = confirmPassword)
        privateUiState.value = newState
        validate(newState)
    }

    fun updateUsernameOrEmail(usernameOrEmail: String) {
        val newState = privateUiState.value.copy(usernameOrEmail = usernameOrEmail)
        privateUiState.value = newState
        validate(newState)
    }

    private fun validate(state: AuthUiState = privateUiState.value){
        val usernameError = Validation.validateUsername(state.username)
        val emailError = Validation.validateEmail(state.email)
        val passwordError = Validation.validatePassword(state.password)
        val confirmPasswordError = Validation.validateConfirmPassword(state.password, state.confirmPassword)
        val isRegisterValid = listOf(usernameError, emailError, passwordError, confirmPasswordError).all { it == null }

        val usernameOrEmailError = Validation.validateUsernameOrEmail(state.usernameOrEmail)
        val loginPasswordError = if(state.password.isBlank()) "Hasło jest wymagane" else null
        val isLoginValid = usernameOrEmailError == null && loginPasswordError == null

        privateUiState.value = state.copy(
            usernameError = usernameError,
            emailError = emailError,
            passwordError = if (state.isRegistering) passwordError else loginPasswordError,
            confirmPasswordError = confirmPasswordError,
            usernameOrEmailError = usernameOrEmailError,
            isRegisterValid = isRegisterValid,
            isLoginValid = isLoginValid
        )
    }

    fun register() {
        val currentState = privateUiState.value

        validate(currentState)
        if(!privateUiState.value.isRegisterValid){
            privateUiState.value = privateUiState.value.copy(errorMessage = "Popraw błędy!")
            return
        }

        privateUiState.value = currentState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch{
            val result = authRepository.register(
                privateUiState.value.username,
                privateUiState.value.email,
                privateUiState.value.password
            )
            if(result.isSuccess) {
                privateUiState.value = privateUiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true
                )
            } else {
                privateUiState.value = privateUiState.value.copy(
                    isLoading = false,
                    errorMessage = "Błąd rejestracji! ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    fun login() {
        val currentState = privateUiState.value

        validate(currentState)
        if(!privateUiState.value.isLoginValid){
            privateUiState.value = privateUiState.value.copy(
                errorMessage = "Popraw błędy"
            )
            return
        }

        privateUiState.value = currentState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = authRepository.login(
                privateUiState.value.usernameOrEmail,
                privateUiState.value.password
            )
            if (result.isSuccess){
                privateUiState.value = privateUiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true
                )
            } else{
                privateUiState.value = privateUiState.value.copy(
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

    private val privateShowProfileEdit = MutableStateFlow(false)
    val showProfileEdit = privateShowProfileEdit.asStateFlow()

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

    fun showProfileEditScreen() {
        privateShowProfileEdit.value = true
    }

    fun hideProfileEditScreen() {
        privateShowProfileEdit.value = false
    }

    fun selectTheme(isDark: Boolean){
        viewModelScope.launch {
            userPreferences.setDarkTheme(isDark)
        }
    }

    fun completeProfileSetup() {
        //TODO Zapisz dane
        clearFirstLogin()
        hideProfileEditScreen()
    }

    fun cancelProfileSetup() {
        clearFirstLogin()
        hideProfileEditScreen()
    }
}