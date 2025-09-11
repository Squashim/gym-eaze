package com.example.gymeaze.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.R
import com.example.gymeaze.data.models.Badge
import com.example.gymeaze.data.models.UpdateLoginRequest
import com.example.gymeaze.data.models.UpdateLoginResponse
import com.example.gymeaze.data.models.UpdateProfileResponse
import com.example.gymeaze.data.models.User
import com.example.gymeaze.data.models.UserFitnessData
import com.example.gymeaze.data.models.UserPersonalData
import com.example.gymeaze.data.models.UserProfileDetails
import com.example.gymeaze.data.repository.AuthRepository
import com.example.gymeaze.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class UserViewModel(application: Application,  private val onTokenExpired: (() -> Unit)? = null) : AndroidViewModel(application) {

    private val userRepository = UserRepository(application)

    private fun handleTokenExpiration() {
        viewModelScope.launch {
            val authRepository = AuthRepository(getApplication())
            authRepository.clearUserSession()
            _error.value = getApplication<Application>().getString(R.string.error_session_expired_please_login)

            onTokenExpired?.invoke()
        }
    }
    // PROFILE INFO -------------------------------------------------------------

    private val _userProfile = MutableStateFlow<UserProfileDetails?>(null)
    val userProfile: StateFlow<UserProfileDetails?> = _userProfile

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _availableBadges = MutableStateFlow<List<Badge>>(emptyList())
    val availableBadges: StateFlow<List<Badge>> = _availableBadges

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedPinnedBadges = MutableStateFlow<List<Badge>>(emptyList())
    val selectedPinnedBadges: StateFlow<List<Badge>> = _selectedPinnedBadges
    fun setSelectedImage(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun resetToSavedState() {
        _userProfile.value?.let { profile ->
            _selectedPinnedBadges.value = profile.pinnedBadges
        }
        _selectedImageUri.value = null
        _error.value = null
    }

    fun onScreenExit() {
        resetToSavedState()
    }

    fun setSelectedPinnedBadges(badges: List<Badge>) {
        _selectedPinnedBadges.value = badges
    }


    fun initializeProfileData() {
        fetchUserProfile()
        fetchUserBadges()
        fetchCurrentUser()
    }
    private suspend fun validateAndConvertImage(uri: Uri): Result<File> {
        return try {
            val context = getApplication<Application>()
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

            if (inputStream != null) {
                val mimeType = context.contentResolver.getType(uri)
                val uriString = uri.toString()

                val extension = when {
                    mimeType == "image/png" -> ".png"
                    mimeType == "image/jpeg" || mimeType == "image/jpg" -> ".jpg"
                    mimeType == "image/webp" -> ".webp"
                    uriString.contains(".png", ignoreCase = true) -> ".png"
                    uriString.contains(".jpg", ignoreCase = true) -> ".jpg"
                    uriString.contains(".jpeg", ignoreCase = true) -> ".jpg"
                    uriString.contains(".webp", ignoreCase = true) -> ".webp"
                    uri.scheme == "content" -> ".jpg"
                    else -> {
                        inputStream.close()
                        return Result.failure(IllegalArgumentException(getApplication<Application>().getString(R.string.error_unsupported_image_format, mimeType, uriString)))
                    }
                }

                val file = File(context.cacheDir, "profile_image_${System.currentTimeMillis()}$extension")
                val outputStream = FileOutputStream(file)
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()

                if (file.length() > 5L * 1024L * 1024L) {
                    file.delete()
                    return Result.failure(IllegalArgumentException(getApplication<Application>().getString(R.string.error_image_too_large, file.length() / 1024 / 1024)))
                }

                if (file.length() < 100) {
                    file.delete()
                    return Result.failure(IllegalArgumentException(getApplication<Application>().getString(R.string.error_file_too_small, file.length())))
                }

                Result.success(file)
            } else {
                return Result.failure(IllegalArgumentException(getApplication<Application>().getString(R.string.error_cannot_read_file)))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
    fun fetchUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val authRepository = AuthRepository(getApplication())
            val refreshResult = authRepository.refreshAccessToken()

            if (refreshResult.isFailure) {
                _error.value = getApplication<Application>().getString(R.string.error_session_expired_please_login)
                _isLoading.value = true
                handleTokenExpiration()
                _navigateToAuth.value = true
                return@launch
            }

            val result = userRepository.getUserProfileDetails()

            if (result.isSuccess) {
                _userProfile.value = result.getOrNull()
                _error.value = null
            } else {
                _error.value = result.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.error_unknown_occurred)
                _userProfile.value = null
            }

            _isLoading.value = false
        }
    }

    fun updateProfile(
        description: String,
        goal: String?,
        pinnedBadges: List<Badge>? = null,
        updateOnlyBadges: Boolean = false
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _success.value = null

            val authRepository = AuthRepository(getApplication())
            val refreshResult = authRepository.refreshAccessToken()

            if (refreshResult.isFailure) {
                _error.value = getApplication<Application>().getString(R.string.error_session_expired_please_login)
                _isLoading.value = false
                return@launch
            }

            val badgesToUse = pinnedBadges ?: _selectedPinnedBadges.value


            if (badgesToUse.size > 5) {
                _error.value = getApplication<Application>().getString(R.string.error_max_five_badges)
                _isLoading.value = false
                return@launch
            }

            var profileImageFile: File? = null
            if (!updateOnlyBadges) {
                _selectedImageUri.value?.let { uri ->
                    val imageResult = validateAndConvertImage(uri)
                    if (imageResult.isFailure) {
                        _error.value = imageResult.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.error_image_validation)
                        _isLoading.value = false
                        return@launch
                    }
                    profileImageFile = imageResult.getOrNull()
                }
            }


            val result = userRepository.updateProfileDetails(
                description = description,
                goal = goal,
                pinnedBadges = badgesToUse,
                profileImageFile = profileImageFile
            )

            if (result.isSuccess) {
                _selectedImageUri.value = null
                _selectedPinnedBadges.value = badgesToUse
                _error.value = null
                _success.value = getApplication<Application>().getString(R.string.profile_data_updated_successfully)
                fetchUserProfile()
            } else {
                _error.value = result.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.error_profile_update_failed)
            }

            _isLoading.value = false
        }
    }

    fun fetchUserBadges() {
        viewModelScope.launch {
            _error.value = null

            val result = userRepository.getUserBadges()

            if (result.isSuccess) {
                val badges = result.getOrNull() ?: emptyList()
                _availableBadges.value = badges
            } else {
                val error = result.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.error_fetching_badges)
                _error.value = error
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }

    // LOGIN DETAILS -------------------------------------------------------------
    private val _currentUsername = MutableStateFlow("")
    val currentUsername: StateFlow<String> = _currentUsername.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _newUsername = MutableStateFlow("")
    val newUsername: StateFlow<String> = _newUsername.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _loginDataError = MutableStateFlow<String?>(null)
    val loginDataError: StateFlow<String?> = _loginDataError.asStateFlow()

    private val _loginDataLoading = MutableStateFlow(false)
    val loginDataLoading: StateFlow<Boolean> = _loginDataLoading.asStateFlow()

    private val _loginDataSuccess = MutableStateFlow<String?>(null)
    val loginDataSuccess: StateFlow<String?> = _loginDataSuccess.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    private val _isBadgesDetailsPublic = MutableStateFlow(true)
    val isBadgesDetailsPublic: StateFlow<Boolean> = _isBadgesDetailsPublic

    private val _isGoalPublic = MutableStateFlow(true)
    val isGoalPublic: StateFlow<Boolean> = _isGoalPublic

    private val _confirmUsername = MutableStateFlow("")
    val confirmUsername: StateFlow<String> = _confirmUsername.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError: StateFlow<String?> = _deleteError.asStateFlow()

    // W UserViewModel dodaj:
    private val _navigateToAuth = MutableStateFlow(false)
    val navigateToAuth: StateFlow<Boolean> = _navigateToAuth.asStateFlow()

    fun resetNavigation() {
        _navigateToAuth.value = false
    }

    fun updateNewUsername(username: String) {
        _newUsername.value = username
    }

    fun updateNewPassword(password: String) {
        _newPassword.value = password
    }

    fun updateConfirmPassword(password: String) {
        _confirmPassword.value = password
    }

    fun updateConfirmUsername(username: String) {
        _confirmUsername.value = username
    }

    fun showDeleteDialog() {
        _showDeleteDialog.value = true
        _confirmUsername.value = ""
        _deleteError.value = null
    }

    fun hideDeleteDialog() {
        _showDeleteDialog.value = false
        _confirmUsername.value = ""
        _deleteError.value = null
    }

    fun resetLoginDataError() {
        _loginDataError.value = null
    }

    fun resetLoginDataSuccess() {
        _loginDataSuccess.value = null
    }

    fun resetUserState() {
        _currentUser.value = null
        _currentUsername.value = ""
        _newUsername.value = ""
        _newPassword.value = ""
        _confirmPassword.value = ""
        _loginDataError.value = null
        _loginDataSuccess.value = null
        _showDeleteDialog.value = false
        _confirmUsername.value = ""
    }

    private fun validateLoginDataChanges(
        newUsername: String,
        newPassword: String,
        confirmPassword: String
    ): String? {
        if (newUsername.isNotEmpty()) {
            if (newUsername.length < 6) {
                return getApplication<Application>().getString(R.string.error_username_min_chars)
            }
            if (newUsername.length > 32) {
                return getApplication<Application>().getString(R.string.error_username_max_chars)
            }
        }

        if (newPassword.isNotEmpty()) {
            if (newPassword.length < 14) {
                return getApplication<Application>().getString(R.string.error_password_min_chars)
            }
            if (newPassword.length > 32) {
                return getApplication<Application>().getString(R.string.error_password_max_chars)
            }
            if (newPassword != confirmPassword) {
                return getApplication<Application>().getString(R.string.error_passwords_no_match)
            }
            if (!newPassword.any { it.isUpperCase() }) {
                return getApplication<Application>().getString(R.string.error_password_uppercase)
            }
            if (!newPassword.any { it.isLowerCase() }) {
                return getApplication<Application>().getString(R.string.error_password_lowercase)
            }
            if (!newPassword.any { it.isDigit() }) {
                return getApplication<Application>().getString(R.string.error_password_number)
            }
            if (!newPassword.any { "!@#$%^&*()_+./".contains(it) }) {
                return getApplication<Application>().getString(R.string.error_password_special)
            }
        }

        return null
    }

    fun fetchCurrentUser() {
        viewModelScope.launch {
            _loginDataLoading.value = true
            _error.value = null
            _loginDataError.value = null

            val authRepository = AuthRepository(getApplication())
            val result = authRepository.getCurrentUser()

            if (result.isSuccess) {
                val user = result.getOrNull()
                _currentUser.value = user
                _currentUsername.value = user?.username ?: ""

                if (_newUsername.value.isEmpty() || _newUsername.value == _currentUsername.value) {
                    _newUsername.value = user?.username ?: ""
                }

                _error.value = null
                _loginDataError.value = null
            } else {
                _error.value = result.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.error_fetching_user_data)
            }

            _loginDataLoading.value = false
        }
    }

    fun updateLoginData() {
        viewModelScope.launch {
            _loginDataLoading.value = true
            _loginDataError.value = null

            val validationError = validateLoginDataChanges(
                _newUsername.value,
                _newPassword.value,
                _confirmPassword.value
            )

            if (validationError != null) {
                _loginDataError.value = validationError
                _loginDataLoading.value = false
                return@launch
            }

            val authRepository = AuthRepository(getApplication())
            val refreshResult = authRepository.refreshAccessToken()

            if (refreshResult.isFailure) {
                _loginDataError.value = getApplication<Application>().getString(R.string.error_session_expired_please_login)
                _loginDataLoading.value = true
                handleTokenExpiration()
                _navigateToAuth.value = true
                return@launch
            }

            val shouldUpdateUsername = _newUsername.value.isNotEmpty() && _newUsername.value != _currentUsername.value
            val shouldUpdatePassword = _newPassword.value.isNotEmpty()

            if (!shouldUpdateUsername && !shouldUpdatePassword) {
                _loginDataError.value = getApplication<Application>().getString(R.string.error_no_changes_to_save)
                _loginDataLoading.value = false
                return@launch
            }

            val result = userRepository.updateLoginData(
                username = if (shouldUpdateUsername) _newUsername.value else null,
                password = if (shouldUpdatePassword) _newPassword.value else null
            )

            if (result.isSuccess) {
                _loginDataError.value = null
                _loginDataSuccess.value = getApplication<Application>().getString(R.string.login_data_updated_successfully)

                if (shouldUpdateUsername) {
                    _currentUsername.value = _newUsername.value
                }

                _newPassword.value = ""
                _confirmPassword.value = ""
            } else {
                _loginDataError.value = result.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.error_login_data_update_failed)
            }

            _loginDataLoading.value = false
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _isDeleting.value = true
            _deleteError.value = null

            if (_confirmUsername.value != _currentUsername.value) {
                _deleteError.value = getApplication<Application>().getString(R.string.error_username_mismatch)
                _isDeleting.value = false
                return@launch
            }

            val authRepository = AuthRepository(getApplication())
            val refreshResult = authRepository.refreshAccessToken()

            if (refreshResult.isFailure) {
                _deleteError.value = getApplication<Application>().getString(R.string.error_session_expired_please_login)
                _isDeleting.value = true
                handleTokenExpiration()
                _navigateToAuth.value = true
                return@launch
            }

            val result = userRepository.deleteAccount()

            if (result.isSuccess) {
                authRepository.logout()
                hideDeleteDialog()
                _navigateToAuth.value = true
            } else {
                _deleteError.value = result.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.error_account_deletion_failed)
            }

            _isDeleting.value = false
        }
    }

    // PERSONAL DETAILS -------------------------------------------------------------
    private val _personalData = MutableStateFlow<UserPersonalData?>(null)
    val personalData: StateFlow<UserPersonalData?> = _personalData

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName

    private val _selectedCountry = MutableStateFlow<String?>(null)
    val selectedCountry: StateFlow<String?> = _selectedCountry

    private val _isPersonalDetailsPublic = MutableStateFlow(true)
    val isPersonalDetailsPublic: StateFlow<Boolean> = _isPersonalDetailsPublic

    private val _personalDataLoading = MutableStateFlow(false)
    val personalDataLoading: StateFlow<Boolean> = _personalDataLoading

    private val _personalDataError = MutableStateFlow<String?>(null)
    val personalDataError: StateFlow<String?> = _personalDataError

    private val _personalDataSuccess = MutableStateFlow<String?>(null)
    val personalDataSuccess: StateFlow<String?> = _personalDataSuccess

    fun updateFirstName(newFirstName: String) {
        _firstName.value = newFirstName
    }

    fun updateLastName(newLastName: String) {
        _lastName.value = newLastName
    }

    fun updateCountry(newCountry: String?) {
        _selectedCountry.value = newCountry
    }

    fun resetPersonalDataSuccess() {
        _personalDataSuccess.value = null
    }

    fun fetchUserPersonalData() {
        viewModelScope.launch {
            _personalDataLoading.value = true
            _personalDataError.value = null

            val authRepository = AuthRepository(getApplication())
            val refreshResult = authRepository.refreshAccessToken()

            if (refreshResult.isFailure) {
                _personalDataError.value = getApplication<Application>().getString(R.string.error_session_expired_please_login)
                _personalDataLoading.value = true
                handleTokenExpiration()
                _navigateToAuth.value = true
                return@launch
            }

            val result = userRepository.getUserPersonalData()

            if (result.isSuccess) {
                val data = result.getOrNull()
                _personalData.value = data
                _firstName.value = data?.firstName ?: ""
                _lastName.value = data?.lastName ?: ""
                _selectedCountry.value = data?.country
                _isPersonalDetailsPublic.value = data?.isPersonalDetailsPublic ?: true


                _personalDataError.value = null
            } else {
                _personalDataError.value = result.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.error_unknown_occurred)
                _personalData.value = null
            }

            _personalDataLoading.value = false
        }
    }

    fun updatePersonalData() {
        val currentFirstName = _firstName.value ?: ""
        val currentLastName = _lastName.value ?: ""
        val currentCountry = _selectedCountry.value

        viewModelScope.launch {
            _personalDataLoading.value = true
            _personalDataError.value = null
            _personalDataSuccess.value = null

            val authRepository = AuthRepository(getApplication())
            val refreshResult = authRepository.refreshAccessToken()

            if (refreshResult.isFailure) {
                _personalDataError.value = getApplication<Application>().getString(R.string.error_session_expired_please_login)
                _personalDataLoading.value = false
                return@launch
            }

            val result = userRepository.updateUserPersonalData(
                firstName = currentFirstName,
                lastName = currentLastName,
                country = currentCountry
            )

            if (result.isSuccess) {
                _personalDataError.value = null
                _personalDataSuccess.value = getApplication<Application>().getString(R.string.personal_data_updated_successfully)
                fetchUserPersonalData()
            } else {
                _personalDataError.value = result.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.error_personal_data_update_failed)
            }

            _personalDataLoading.value = false
        }
    }

    // FITNESS DETAILS -------------------------------------------------------------
    private val _fitnessData = MutableStateFlow<UserFitnessData?>(null)
    val fitnessData: StateFlow<UserFitnessData?> = _fitnessData.asStateFlow()

    private val _selectedGender = MutableStateFlow<String?>(null)
    val selectedGender: StateFlow<String?> = _selectedGender.asStateFlow()

    private val _isFitnessDetailsPublic = MutableStateFlow(true)
    val isFitnessDetailsPublic: StateFlow<Boolean> = _isFitnessDetailsPublic

    private val _age = MutableStateFlow<String>("")
    val age: StateFlow<String> = _age.asStateFlow()

    private val _height = MutableStateFlow<String>("")
    val height: StateFlow<String> = _height.asStateFlow()

    private val _weight = MutableStateFlow<String>("")
    val weight: StateFlow<String> = _weight.asStateFlow()

    private val _fitnessDataLoading = MutableStateFlow(false)
    val fitnessDataLoading: StateFlow<Boolean> = _fitnessDataLoading.asStateFlow()

    private val _fitnessDataError = MutableStateFlow<String?>(null)
    val fitnessDataError: StateFlow<String?> = _fitnessDataError.asStateFlow()

    private val _fitnessDataSuccess = MutableStateFlow<String?>(null)
    val fitnessDataSuccess: StateFlow<String?> = _fitnessDataSuccess.asStateFlow()

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success.asStateFlow()

    fun setSuccess(message: String) {
        _success.value = message
    }

    fun resetSuccess() {
        _success.value = null
    }

    fun updateGender(gender: String?) {
        _selectedGender.value = gender
    }

    fun updateAge(age: String) {
        _age.value = age
    }

    fun updateHeight(height: String) {
        _height.value = height
    }

    fun updateWeight(weight: String) {
        _weight.value = weight
    }

    fun resetFitnessDataSuccess() {
        _fitnessDataSuccess.value = null
    }

    fun fetchUserFitnessData() {
        viewModelScope.launch {
            _fitnessDataLoading.value = true
            _fitnessDataError.value = null

            val authRepository = AuthRepository(getApplication())
            val refreshResult = authRepository.refreshAccessToken()

            if (refreshResult.isFailure) {
                _fitnessDataError.value = getApplication<Application>().getString(R.string.error_session_expired_please_login)
                _fitnessDataLoading.value = true
                handleTokenExpiration()
                _navigateToAuth.value = true
                return@launch
            }

            val result = userRepository.getUserFitnessData()

            if (result.isSuccess) {
                val data = result.getOrNull()
                _fitnessData.value = data

                _selectedGender.value = data?.gender
                _age.value = data?.age?.toString() ?: ""
                _height.value = data?.height?.toString() ?: ""
                _weight.value = data?.weight?.toString() ?: ""
                _isFitnessDetailsPublic.value = data?.isFitnessDetailsPublic ?: true

                _fitnessDataError.value = null
            } else {
                _fitnessDataError.value = result.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.error_unknown_occurred)
                _fitnessData.value = null
            }

            _fitnessDataLoading.value = false
        }
    }

    fun updateFitnessData() {
        val currentGender = _selectedGender.value
        val currentAge = _age.value.toIntOrNull()
        val currentHeight = _height.value.toIntOrNull()
        val currentWeight = _weight.value.toIntOrNull()

        viewModelScope.launch {
            _fitnessDataLoading.value = true
            _fitnessDataError.value = null
            _fitnessDataError.value = null

            val authRepository = AuthRepository(getApplication())
            val refreshResult = authRepository.refreshAccessToken()

            if (refreshResult.isFailure) {
                _fitnessDataError.value = getApplication<Application>().getString(R.string.error_session_expired_please_login)
                _fitnessDataLoading.value = false
                return@launch
            }

            val result = userRepository.updateUserFitnessData(
                gender = currentGender,
                age = currentAge,
                height = currentHeight,
                weight = currentWeight
            )

            if (result.isSuccess) {
                _fitnessDataError.value = null
                _fitnessDataSuccess.value = getApplication<Application>().getString(R.string.fitness_data_updated_successfully)
                fetchUserFitnessData()
            } else {
                _fitnessDataError.value = result.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.error_fitness_data_update_failed)
            }

            _fitnessDataLoading.value = false
        }
    }
}