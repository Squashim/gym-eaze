package com.example.gymeaze.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.R
import com.example.gymeaze.data.repository.AuthRepository
import com.example.gymeaze.data.UserPreferences
import com.example.gymeaze.data.models.Exercise
import com.example.gymeaze.data.models.User
import com.example.gymeaze.util.Validation
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
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

sealed class LogoutEvent {
    object TokenExpired : LogoutEvent()
    object Manual : LogoutEvent()
    object AccountDeleted : LogoutEvent()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application.applicationContext)
    private val userPreferences = UserPreferences(application.applicationContext)
    private val _showBrowseExercises = MutableStateFlow(false)
    val showBrowseExercises: StateFlow<Boolean> = _showBrowseExercises.asStateFlow()
    private val _isCheckingToken = MutableStateFlow(true)
    val isCheckingToken: StateFlow<Boolean> = _isCheckingToken.asStateFlow()

    init {
        checkTokenValidityOnStartup()
    }

    private val _logoutEvent = MutableStateFlow<LogoutEvent?>(null)
    val logoutEvent: StateFlow<LogoutEvent?> = _logoutEvent.asStateFlow()

    private val _showTrainingPlanDetail = MutableStateFlow(false)
    val showTrainingPlanDetail: StateFlow<Boolean> = _showTrainingPlanDetail.asStateFlow()

    private val _selectedTrainingPlanId = MutableStateFlow<String?>(null)
    val selectedTrainingPlanId: StateFlow<String?> = _selectedTrainingPlanId.asStateFlow()

    private val _showBrowseTrainingPlans = MutableStateFlow(false)
    val showBrowseTrainingPlans = _showBrowseTrainingPlans.asStateFlow()

    private val _showMyTrainingPlans = MutableStateFlow(false)
    val showMyTrainingPlans = _showMyTrainingPlans.asStateFlow()

    private val _showFavoriteTrainingPlans = MutableStateFlow(false)
    val showFavoriteTrainingPlans = _showFavoriteTrainingPlans.asStateFlow()

    private val _showExerciseSelectionForDay = MutableStateFlow(false)
    val showExerciseSelectionForDay: StateFlow<Boolean> = _showExerciseSelectionForDay.asStateFlow()

    private val _selectedDayIndex = MutableStateFlow<Int?>(null)
    val selectedDayIndex: StateFlow<Int?> = _selectedDayIndex.asStateFlow()

    private val _showCreateTrainingPlan = MutableStateFlow(false)
    val showCreateTrainingPlan: StateFlow<Boolean> = _showCreateTrainingPlan.asStateFlow()

    private val _showTrainingPlansMenu = MutableStateFlow(false)
    val showTrainingPlansMenu: StateFlow<Boolean> = _showTrainingPlansMenu.asStateFlow()

    private val _showEditTrainingPlan = MutableStateFlow(false)
    val showEditTrainingPlan: StateFlow<Boolean> = _showEditTrainingPlan.asStateFlow()

    private val _selectedEditPlanId = MutableStateFlow<String?>(null)
    val selectedEditPlanId: StateFlow<String?> = _selectedEditPlanId.asStateFlow()

    private val _showStartedPlanDays = MutableStateFlow(false)
    val showStartedPlanDays = _showStartedPlanDays.asStateFlow()

    private val _selectedStartedPlan = MutableStateFlow<Pair<String, String>?>(null)
    val selectedStartedPlan = _selectedStartedPlan.asStateFlow()

    private val _showWorkoutPlayer = MutableStateFlow(false)
    val showWorkoutPlayer = _showWorkoutPlayer.asStateFlow()

    private val _selectedWorkoutParams = MutableStateFlow<Pair<String, Int>?>(null) // Para (userTrainingPlanId, dayNumber)
    val selectedWorkoutParams = _selectedWorkoutParams.asStateFlow()

    fun navigateToWorkoutPlayer(userTrainingPlanId: String, planName: String, dayNumber: Int) {
        _selectedWorkoutParams.value = Pair(userTrainingPlanId, dayNumber)

        _showTrainingPlanDetail.value = false
        _showStartedPlanDays.value = false

        _showWorkoutPlayer.value = true
    }


    fun showWorkoutPlayerScreen(userTrainingPlanId: String, dayNumber: Int) {
        _selectedWorkoutParams.value = Pair(userTrainingPlanId, dayNumber)
        _showWorkoutPlayer.value = true
    }

    fun hideWorkoutPlayerScreen() {
        _showWorkoutPlayer.value = false
        _selectedWorkoutParams.value = null
    }

    fun showStartedPlanDaysScreen(userTrainingPlanId: String, planName: String) {
        _selectedStartedPlan.value = Pair(userTrainingPlanId, planName)
        _showStartedPlanDays.value = true
    }

    fun hideStartedPlanDaysScreen() {
        _showStartedPlanDays.value = false
        _selectedStartedPlan.value = null
    }

    fun showEditTrainingPlanScreen(planId: String) {
        _showMyTrainingPlans.value = false
        _selectedEditPlanId.value = planId
        _showEditTrainingPlan.value = true
    }

    fun hideEditTrainingPlanScreen() {
        _showEditTrainingPlan.value = false
        _selectedEditPlanId.value = null
    }

    private var exerciseSelectionCallback: ((Exercise) -> Unit)? = null

    fun setExerciseSelectionCallback(callback: (Exercise) -> Unit) {
        exerciseSelectionCallback = callback
    }

    fun clearExerciseSelectionCallback() {
        exerciseSelectionCallback = null
    }

    fun onExerciseSelectedForDay(exercise: Exercise) {
        exerciseSelectionCallback?.invoke(exercise)
        clearExerciseSelectionCallback()
    }

    fun showBrowseTrainingPlansScreen() {
        _showBrowseTrainingPlans.value = true
    }

    fun hideBrowseTrainingPlansScreen() {
        _showBrowseTrainingPlans.value = false
    }

    fun showMyTrainingPlansScreen() {
        _showMyTrainingPlans.value = true
    }

    fun hideMyTrainingPlansScreen() {
        _showMyTrainingPlans.value = false
    }

    fun showFavoriteTrainingPlansScreen() {
        _showFavoriteTrainingPlans.value = true
    }

    fun hideFavoriteTrainingPlansScreen() {
        _showFavoriteTrainingPlans.value = false
    }

    fun showCreateTrainingPlanScreen() {
        _showCreateTrainingPlan.value = true
    }

    private var editPlanExerciseSelectionCallback: ((Exercise) -> Unit)? = null

    fun isEditPlanExerciseSelectionActive(): Boolean {
        return editPlanExerciseSelectionCallback != null
    }

    fun setEditPlanExerciseSelectionCallback(callback: (Exercise) -> Unit) {
        editPlanExerciseSelectionCallback = callback
    }

    fun clearEditPlanExerciseSelectionCallback() {
        editPlanExerciseSelectionCallback = null
    }

    fun onExerciseSelectedForEditPlan(exercise: Exercise) {
        editPlanExerciseSelectionCallback?.invoke(exercise)
        clearEditPlanExerciseSelectionCallback()
        hideExerciseSelectionForDayScreen()
    }
    fun showExerciseSelectionForDayScreen(dayIndex: Int) {
        _selectedDayIndex.value = dayIndex
        _showExerciseSelectionForDay.value = true
    }
    fun hideCreateTrainingPlanScreen() {
        _showCreateTrainingPlan.value = false
    }

    fun showTrainingPlansMenuScreen() {
        _showTrainingPlansMenu.value = true
    }

    fun hideTrainingPlansMenuScreen() {
        _showTrainingPlansMenu.value = false
    }

    fun showTrainingPlanDetailScreen(planId: String) {
        _selectedTrainingPlanId.value = planId
        _showTrainingPlanDetail.value = true
    }

    fun hideTrainingPlanDetailScreen() {
        _showTrainingPlanDetail.value = false
        _selectedTrainingPlanId.value = null
    }

    fun hideExerciseSelectionForDayScreen() {
        _showExerciseSelectionForDay.value = false
        _selectedDayIndex.value = null
    }

    private fun checkTokenValidityOnStartup() {
        viewModelScope.launch {
            try {
                val isLoggedIn = userPreferences.isLoggedIn.first()
                val accessToken = userPreferences.accessToken.first()

                if (isLoggedIn && !accessToken.isNullOrEmpty()) {
                    val result = authRepository.getCurrentUser()
                    if (result.isFailure) {
                        val refreshResult = authRepository.refreshAccessToken()
                        if (refreshResult.isFailure) {
                            handleTokenExpiration()
                        }
                    }
                }
            } catch(e: Exception) {
                e.printStackTrace()
            } finally {
                _isCheckingToken.value = false
            }
        }
    }

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

    private val _showExerciseDetail = MutableStateFlow(false)
    val showExerciseDetail = _showExerciseDetail.asStateFlow()

    private val _selectedExerciseId = MutableStateFlow<String?>(null)
    val selectedExerciseId = _selectedExerciseId.asStateFlow()


    fun showExerciseDetailScreen(exerciseId: String) {
        _selectedExerciseId.value = exerciseId
        _showExerciseDetail.value = true
    }

    fun hideExerciseDetailScreen() {
        _showExerciseDetail.value = false
        _selectedExerciseId.value = null
    }

    fun showBrowseExercisesScreen() {
        _showBrowseExercises.value = true
    }

    fun hideBrowseExercisesScreen() {
        _showBrowseExercises.value = false
    }
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
                password = if(password.isBlank()) getApplication<Application>().getString(R.string.error_password_required) else null
            )

            privateUiState.value = currentState.copy(
                password = password,
                loginErrors = newLoginErrors,
                touchedFields = currentState.touchedFields + "password"
            )
        }
    }

    suspend fun ensureValidToken(): Boolean {
        return try {
            val isLoggedIn = userPreferences.isLoggedIn.first()
            val accessToken = userPreferences.accessToken.first()

            if (!isLoggedIn || accessToken.isNullOrEmpty()) {
                return false
            }

            val result = authRepository.getCurrentUser()
            if (result.isFailure) {
                val refreshResult = authRepository.refreshAccessToken()
                if (refreshResult.isFailure) {
                    handleTokenExpiration()
                    return false
                }
            }
            true
        } catch (e: Exception) {
            false
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
                password = if (currentState.password.isBlank()) getApplication<Application>().getString(R.string.error_password_required) else null
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
            privateUiState.value = privateUiState.value.copy(
                errorMessage = getApplication<Application>().getString(R.string.error_fix_validation_errors)
            )
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
                    isLoading = false
                )
            } else {
                privateUiState.value = privateUiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?:
                    getApplication<Application>().getString(R.string.error_registration_failed)
                )
            }
        }
    }

    fun login() {
        if(!validateAllFields()) {
            privateUiState.value = privateUiState.value.copy(
                errorMessage = getApplication<Application>().getString(R.string.error_fix_validation_errors)
            )
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
                    isLoading = false
                )
            } else{
                privateUiState.value = privateUiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?:
                    getApplication<Application>().getString(R.string.error_login_failed)
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
                            isLoading = false
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
                            errorMessage = googleResult.error ?: getApplication<Application>().getString(R.string.error_google_login_general, "Unknown error")
                        )
                    }
                }
            } else {
                privateUiState.value = privateUiState.value.copy(
                    isLoading = false,
                    errorMessage = getApplication<Application>().getString(R.string.error_google_login_general, result.exceptionOrNull()?.message ?: "Unknown error")
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
                    isLoading = false
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
                errorMessage = result.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.error_google_signin_failed)
            )
        }
    }

    private fun handleTokenExpiration() {
        viewModelScope.launch {
            authRepository.clearUserSession()
            privateUiState.value = AuthUiState()
            _logoutEvent.value = LogoutEvent.TokenExpired
        }
    }

    fun logout(manual: Boolean = true) {
        viewModelScope.launch {
            if (manual) {
                authRepository.logout()
                _logoutEvent.value = LogoutEvent.Manual
            } else {
                handleTokenExpiration()
            }
            privateUiState.value = AuthUiState()
        }
    }

    fun delete() {
        viewModelScope.launch {
            authRepository.clearUserSession()
            privateUiState.value = AuthUiState()
            privateUiState.value = AuthUiState()
            _logoutEvent.value = LogoutEvent.AccountDeleted
        }
    }

    fun clearLogoutEvent() {
        _logoutEvent.value = null
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

    private val privateShowExerciseCreator = MutableStateFlow(false)
    val showExerciseCreator = privateShowExerciseCreator.asStateFlow()

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

    private val _showPostScreen = MutableStateFlow(false)
    val showPostScreen = _showPostScreen.asStateFlow()

    private val _showCreatePost = MutableStateFlow(false)
    val showCreatePost = _showCreatePost.asStateFlow()

    private val _showBrowsePost = MutableStateFlow(false)
    val showBrowsePost = _showBrowsePost.asStateFlow()

    private val _showFollowedUserPosts = MutableStateFlow(false)
    val showFollowedUserPosts = _showFollowedUserPosts.asStateFlow()

    private val _showMyPosts = MutableStateFlow(false)
    val showMyPosts = _showMyPosts.asStateFlow()


    fun showExerciseCreatorScreen() { privateShowExerciseCreator.value = true }
    fun hideExerciseCreatorScreen() { privateShowExerciseCreator.value = false }

    fun selectTheme(isDark: Boolean){
        viewModelScope.launch {
            userPreferences.setDarkTheme(isDark)
        }
    }

    suspend fun getCurrentUser(): Result<User> {
        return authRepository.getCurrentUser()
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



    fun showMyPostsScreen(){
       _showMyPosts.value = true
    }
    fun hideMyPostsScreen(){
        _showMyPosts.value = false
    }

    fun showFollowedUserPostsScreen(){
       _showFollowedUserPosts.value = true
    }
    fun hideFollowedUserPostsScreen(){
        _showFollowedUserPosts.value = false
    }

    fun showPostScreen() {
        _showPostScreen.value = true
    }

    fun hidePostScreen() {
        _showPostScreen.value = false
    }

    fun showCreatePostScreen() {
       _showCreatePost.value = true
    }

    fun hideCreatePostScreen() {
       _showCreatePost.value = false
    }

    fun showBrowsePostScreen(){
        _showBrowsePost.value = true
    }
    fun hideBrowsePostScreen(){
        _showBrowsePost.value = false
    }
}