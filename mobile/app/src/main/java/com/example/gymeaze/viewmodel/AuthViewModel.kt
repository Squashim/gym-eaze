package com.example.gymeaze.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.AuthRepository
import com.example.gymeaze.util.Validation
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


data class ValidationErrors(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
    val confirmPassword: String? = null,
    val usernameOrEmail: String? = null
)
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

    val registerErrors: ValidationErrors = ValidationErrors(),
    val loginErrors: ValidationErrors = ValidationErrors(),
    val touchedFields: Set<String> = emptySet()
) {

    val usernameError: String?
        get() = if("username" in touchedFields && isRegistering) registerErrors.username else null

    val emailError: String?
        get() = if("email" in touchedFields && isRegistering) registerErrors.email else null

    val passwordError: String?
        get() = if("password" in touchedFields) {
            if(isRegistering) registerErrors.password else loginErrors.password
        } else null

    val confirmPasswordError: String?
        get() = if("confirmPassword" in touchedFields && isRegistering) registerErrors.confirmPassword else null

    val usernameOrEmailError: String?
        get() = if("usernameOrEmail" in touchedFields && !isRegistering) loginErrors.usernameOrEmail else null

    val isRegisterValid: Boolean
        get() = registerErrors.username == null &&
                registerErrors.email == null &&
                registerErrors.password == null &&
                registerErrors.confirmPassword == null &&
                username.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank()

    val isLoginValid: Boolean
        get() = loginErrors.usernameOrEmail == null &&
                loginErrors.password == null &&
                usernameOrEmail.isNotBlank() &&
                password.isNotBlank()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application.applicationContext)
    private val userPreferences = authRepository.getPreferences()

    private val privateUiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = privateUiState.asStateFlow()

    val isLoggedIn = userPreferences.isLoggedIn
    val isFirstLogin = userPreferences.firstLogin
    val isDarkTheme = userPreferences.isDarkTheme

    val username = userPreferences.username

    private val privateShowAccountLinking = MutableStateFlow(false)
    val showAccountLinking = privateShowAccountLinking.asStateFlow()

    private val privateLinkingEmail = MutableStateFlow("")
    val linkingEmail = privateLinkingEmail.asStateFlow()

    private val privateLinkingToken = MutableStateFlow("")
    val linkingToken = privateLinkingToken.asStateFlow()

    private val privateLinkingError = MutableStateFlow<String?>(null)
    val linkingError = privateLinkingError.asStateFlow()

    fun toggleAuthMode() {
        privateUiState.value = privateUiState.value.copy(
            isRegistering = !privateUiState.value.isRegistering,
            errorMessage = null,
            touchedFields = emptySet(),
            password = ""
        )
    }

    fun updateUsername(username: String){
        val currentState = privateUiState.value
        val newRegisterErrors = currentState.registerErrors.copy(
            username = Validation.validateUsername(username)
        )

        privateUiState.value = currentState.copy(
            username = username,
            registerErrors = newRegisterErrors,
            touchedFields = currentState.touchedFields + "username"
        )
    }

    fun updateEmail(email: String){
        val currentState = privateUiState.value
        val newRegisterErrors = currentState.registerErrors.copy(
            email = Validation.validateEmail(email)
        )

        privateUiState.value = currentState.copy(
            email = email,
            registerErrors = newRegisterErrors,
            touchedFields = currentState.touchedFields + "email"
        )
    }

    fun updatePassword(password: String) {
        val currentState = privateUiState.value

        if(currentState.isRegistering) {
            val newRegisterErrors = currentState.registerErrors.copy(
                password = Validation.validatePassword(password),
                confirmPassword = if (currentState.confirmPassword.isNotEmpty()) {
                    Validation.validateConfirmPassword(password, currentState.confirmPassword)
                } else currentState.registerErrors.confirmPassword
            )

            privateUiState.value = currentState.copy(
                password = password,
                registerErrors = newRegisterErrors,
                touchedFields = currentState.touchedFields + "password"
            )
        } else {
            val newLoginErrors = currentState.loginErrors.copy(
                password = if(password.isBlank()) "Hasło jest wymagane" else null
            )

            privateUiState.value = currentState.copy(
                password = password,
                loginErrors = newLoginErrors,
                touchedFields = currentState.touchedFields + "password"
            )
        }
    }

    fun updateConfirmPassword(confirmPassword: String){
        val currentState = privateUiState.value
        val newRegisterErrors = currentState.registerErrors.copy(
            confirmPassword = Validation.validateConfirmPassword(currentState.password, confirmPassword)
        )

        privateUiState.value = currentState.copy(
            confirmPassword = confirmPassword,
            registerErrors = newRegisterErrors,
            touchedFields = currentState.touchedFields + "confirmPassword"
        )
    }

    fun updateUsernameOrEmail(usernameOrEmail: String) {
        val currentState = privateUiState.value
        val newLoginErrors = currentState.loginErrors.copy(
            usernameOrEmail = Validation.validateUsernameOrEmail(usernameOrEmail)
        )

        privateUiState.value = currentState.copy(
            usernameOrEmail = usernameOrEmail,
            loginErrors = newLoginErrors,
            touchedFields = currentState.touchedFields + "usernameOrEmail"
        )
    }

    private fun validateAllFields(): Boolean{
       val currentState = privateUiState.value

        if(currentState.isRegistering) {
            val errors = ValidationErrors(
                username = Validation.validateUsername(currentState.username),
                email = Validation.validateEmail(currentState.email),
                password = Validation.validatePassword(currentState.password),
                confirmPassword = Validation.validateConfirmPassword(currentState.password, currentState.confirmPassword)
            )

            privateUiState.value = currentState.copy(
                registerErrors = errors,
                touchedFields = setOf("username", "email", "password", "confirmPassword")
            )

            return errors.username == null && errors.email == null &&
                    errors.password == null && errors.confirmPassword == null
        } else {
            val errors = ValidationErrors(
                usernameOrEmail = Validation.validateUsernameOrEmail(currentState.usernameOrEmail),
                password = if (currentState.password.isBlank()) "Hasło jest wymagane" else null
            )

            privateUiState.value = currentState.copy(
                loginErrors = errors,
                touchedFields = setOf("usernameOrEmail", "password")
            )

            return errors.usernameOrEmail == null && errors.password == null
        }
    }

    fun register() {
        if(!validateAllFields()){
            privateUiState.value = privateUiState.value.copy(errorMessage = "Popraw błędy!")
            return
        }

        privateUiState.value = privateUiState.value.copy(isLoading = true, errorMessage = null)

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
        if(!validateAllFields()) {
            privateUiState.value = privateUiState.value.copy(errorMessage = "Popraw błędy")
            return
        }

        privateUiState.value = privateUiState.value.copy(isLoading = true, errorMessage = null)

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

    fun loginWithGoogle(googleAuthCode: String) {
        privateUiState.value = privateUiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = authRepository.loginWithGoogle(googleAuthCode)

            if(result.isSuccess) {
                val googleResult = result.getOrNull()!!

                when {
                    googleResult.success -> {
                        privateUiState.value = privateUiState.value.copy(
                            isLoading = false,
                            isLoggedIn = true
                        )
                    }

                    googleResult.needsLinking -> {
                        privateLinkingEmail.value = googleResult.email ?: ""
                        privateLinkingToken.value = googleResult.token ?: ""
                        privateShowAccountLinking.value = true
                        privateUiState.value = privateUiState.value.copy(isLoading = false)
                    }

                    else -> {
                        privateUiState.value = privateUiState.value.copy(
                            isLoading = false,
                            errorMessage = googleResult.error ?: "Google login failed"
                        )
                    }
                }
            } else {
                privateUiState.value = privateUiState.value.copy(
                    isLoading = false,
                    errorMessage = "Google login error: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    fun confirmAccountLinking(password: String) {
        privateUiState.value = privateUiState.value.copy(isLoading = true)
        privateLinkingError.value = null

        viewModelScope.launch {
            val result = authRepository.linkGoogleAccount(
                privateLinkingEmail.value,
                password,
                privateLinkingToken.value
            )

            if (result.isSuccess) {
                privateShowAccountLinking.value = false
                privateUiState.value = privateUiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true
                )
            } else{
                privateLinkingError.value = result.exceptionOrNull()?.message
                privateUiState.value = privateUiState.value.copy(isLoading = false)
            }
        }
    }

    fun dismissAccountLinking() {
        privateShowAccountLinking.value = false
        privateLinkingError.value = null
        privateLinkingEmail.value = ""
        privateLinkingToken.value = ""
        privateUiState.value = privateUiState.value.copy(isLoading = false)
    }

    fun startGoogleSignIn(): Intent {
        return authRepository.getGoogleSignInHelper().getSignInIntent()
    }

    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        val result = authRepository.getGoogleSignInHelper().handleSignInResult(task)

        if(result.isSuccess) {
            val authCode = result.getOrNull()!!
            loginWithGoogle(authCode);
        } else{
            privateUiState.value = privateUiState.value.copy(
                errorMessage = result.exceptionOrNull()?.message ?: "Google Sing-In failed"
            )
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