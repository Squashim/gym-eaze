package com.example.gymeaze.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

    private val _userProfile = MutableStateFlow<UserProfileDetails?>(null)
    val userProfile: StateFlow<UserProfileDetails?> = _userProfile

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _availableBadges = MutableStateFlow<List<Badge>>(emptyList())
    val availableBadges: StateFlow<List<Badge>> = _availableBadges


    private val _selectedPinnedBadges = MutableStateFlow<List<Badge>>(emptyList())
    val selectedPinnedBadges: StateFlow<List<Badge>> = _selectedPinnedBadges
    fun setSelectedImage(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _newUsername = MutableStateFlow("")
    val newUsername: StateFlow<String> = _newUsername.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _isUpdateSuccessful = MutableStateFlow<Boolean?>(null)
    val isUpdateSuccessful: StateFlow<Boolean?> = _isUpdateSuccessful.asStateFlow()

    // Nowe zmienne dla usuwania konta
    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog = _showDeleteDialog.asStateFlow()

    private val _confirmUsername = MutableStateFlow("")
    val confirmUsername = _confirmUsername.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting = _isDeleting.asStateFlow()

    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError = _deleteError.asStateFlow()

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

    fun updateConfirmUsername(username: String) {
        _confirmUsername.value = username
    }

    fun deleteAccount() {
        val currentUsername = _currentUser.value?.username ?: return
        val confirmedUsername = _confirmUsername.value

        if (confirmedUsername != currentUsername) {
            _deleteError.value = "Nazwa użytkownika nie jest poprawna"
            return
        }

        viewModelScope.launch {
            _isDeleting.value = true
            _deleteError.value = null

            // Odśwież token przed usunięciem konta
            val authRepository = AuthRepository(getApplication())
            val refreshResult = authRepository.refreshAccessToken()

            if (refreshResult.isFailure) {
                _deleteError.value = "Sesja wygasła. Zaloguj się ponownie."
                _isDeleting.value = false
                return@launch
            }

            val result = userRepository.deleteAccount()

            if (result.isSuccess) {
                // Wyloguj użytkownika po pomyślnym usunięciu konta
                authRepository.logout()
                hideDeleteDialog()
                // Tutaj możesz dodać nawigację do ekranu logowania
                // navController.navigate("login") {
                //     popUpTo(0) { inclusive = true }
                // }
            } else {
                _deleteError.value = result.exceptionOrNull()?.message ?: "Nie udało się usunąć konta"
            }

            _isDeleting.value = false
        }
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
                        return Result.failure(IllegalArgumentException("Nieobsługiwany format obrazu. MIME: $mimeType, URI: $uriString"))
                    }
                }

                val file = File(context.cacheDir, "profile_image_${System.currentTimeMillis()}$extension")
                val outputStream = FileOutputStream(file)
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()

                if (file.length() > 5L * 1024L * 1024L) {
                    file.delete()
                    return Result.failure(IllegalArgumentException("Maksymalny rozmiar obrazu to 5MB (aktualnie: ${file.length() / 1024 / 1024}MB)"))
                }

                if (file.length() < 100) {
                    file.delete()
                    return Result.failure(IllegalArgumentException("Plik jest za mały aby być obrazem (${file.length()} bajtów)"))
                }

                Result.success(file)
            } else {
                Result.failure(IllegalArgumentException("Nie można odczytać pliku z URI"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
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

    fun clearUpdateSuccessState() {
        _isUpdateSuccessful.value = null
    }
    fun fetchCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val authRepository = AuthRepository(getApplication())
            val result = authRepository.getCurrentUser()

            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
                // Pre-fill username field
                _newUsername.value = result.getOrNull()?.username ?: ""
                _error.value = null
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Error fetching user data"
            }

            _isLoading.value = false
        }
    }

    private fun handleTokenExpiration() {
        viewModelScope.launch {
            val authRepository = AuthRepository(getApplication())
            authRepository.clearUserSession()
            _error.value = "Session expired. Please log in again."

            // Wywołaj callback do AuthViewModel
            onTokenExpired?.invoke()
        }
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val authRepository = AuthRepository(getApplication())
            val refreshResult = authRepository.refreshAccessToken()

            if (refreshResult.isFailure) {
                _error.value = "Session expired. Please log in again."
                _isLoading.value = false
                handleTokenExpiration()
                return@launch
            }

            val result = userRepository.getUserProfileDetails()

            if (result.isSuccess) {
                _userProfile.value = result.getOrNull()
                _error.value = null
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Unknown error occurred"
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

            val authRepository = AuthRepository(getApplication())
            val refreshResult = authRepository.refreshAccessToken()

            if (refreshResult.isFailure) {
                _error.value = "Session expired. Please log in again."
                _isLoading.value = false
                return@launch
            }

            // POPRAWKA: Jeśli pinnedBadges jest null, użyj selectedPinnedBadges
            // Jeśli pinnedBadges to pusta lista, zostaw pustą listę (pozwoli to na wyczyszczenie)
            val badgesToUse = pinnedBadges ?: _selectedPinnedBadges.value

            // POPRAWKA: Walidacja tylko gdy lista nie jest pusta
            if (badgesToUse.size > 5) {
                _error.value = "Maksymalnie można przypiąć 5 odznak"
                _isLoading.value = false
                return@launch
            }

            var profileImageFile: File? = null
            if (!updateOnlyBadges) {
                _selectedImageUri.value?.let { uri ->
                    val imageResult = validateAndConvertImage(uri)
                    if (imageResult.isFailure) {
                        _error.value = imageResult.exceptionOrNull()?.message ?: "Błąd walidacji obrazu"
                        _isLoading.value = false
                        return@launch
                    }
                    profileImageFile = imageResult.getOrNull()
                }
            }

            // DEBUG: Dodaj logi do sprawdzenia
            println("DEBUG: Saving ${badgesToUse.size} badges to server")
            println("DEBUG: Badge objects: ${badgesToUse.map { "${it.id} - ${it.title}" }}")

            val result = userRepository.updateProfileDetails(
                description = description,
                goal = goal,
                pinnedBadges = badgesToUse, // POPRAWKA: Przekazuj Badge obiekty, nie IDs
                profileImageFile = profileImageFile
            )

            if (result.isSuccess) {
                _selectedImageUri.value = null
                _selectedPinnedBadges.value = badgesToUse // Zapisz nowy stan (może być pusty)
                fetchUserProfile() // Odśwież dane z serwera
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Błąd podczas aktualizacji profilu"
            }

            _isLoading.value = false
        }
    }

    fun fetchUserBadges() {
        viewModelScope.launch {
            _error.value = null
            println("DEBUG: ViewModel - Starting fetchUserBadges()")

            val result = userRepository.getUserBadges()

            if (result.isSuccess) {
                val badges = result.getOrNull() ?: emptyList()
                _availableBadges.value = badges
                println("DEBUG: ViewModel - Successfully fetched ${badges.size} badges")
                badges.forEach { badge ->
                    println("DEBUG: ViewModel - Badge: ID='${badge.id}', title='${badge.title}'")
                }
            } else {
                val error = result.exceptionOrNull()?.message ?: "Błąd podczas pobierania odznak"
                _error.value = error
                println("DEBUG: ViewModel - Error fetching badges: $error")
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }

    fun updateLoginData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _isUpdateSuccessful.value = null

            // Validate input
            val validationError = validateLoginDataChanges(
                _newUsername.value,
                _newPassword.value,
                _confirmPassword.value
            )

            if (validationError != null) {
                _error.value = validationError
                _isLoading.value = false
                return@launch
            }

            // Utwórz instancję AuthRepository z kontekstem
            val authRepository = AuthRepository(getApplication())

            // Odśwież token
            val refreshResult = authRepository.refreshAccessToken()
            if (refreshResult.isFailure) {
                _error.value = "Session expired"
                _isLoading.value = false
                handleTokenExpiration()
                return@launch
            }

            // Pobierz aktualne dane użytkownika
            val currentUserResult = authRepository.getCurrentUser()
            if (currentUserResult.isFailure) {
                _error.value = "Failed to fetch user data"
                _isLoading.value = false
                return@launch
            }

            val currentUser = currentUserResult.getOrNull()
            val currentUsername = currentUser?.username ?: ""

            // Sprawdź czy username się faktycznie zmienia
            val shouldUpdateUsername = _newUsername.value.isNotEmpty() &&
                    _newUsername.value != currentUsername
            val shouldUpdatePassword = _newPassword.value.isNotEmpty()

            if (!shouldUpdateUsername && !shouldUpdatePassword) {
                _error.value = "No changes to save"
                _isLoading.value = false
                return@launch
            }

            val result = userRepository.updateLoginData(
                username = if (shouldUpdateUsername) _newUsername.value else null,
                password = if (shouldUpdatePassword) _newPassword.value else null
            )

            if (result.isSuccess) {
                _isUpdateSuccessful.value = true
                _error.value = null
                // Refresh user data
                fetchCurrentUser()
                // Clear password fields
                _newPassword.value = ""
                _confirmPassword.value = ""
            } else {
                _isUpdateSuccessful.value = false
                _error.value = result.exceptionOrNull()?.message ?: "Error updating login data"
            }

            _isLoading.value = false
        }
    }

    private fun validateLoginDataChanges(
        newUsername: String,
        newPassword: String,
        confirmPassword: String
    ): String? {
        if (newUsername.isEmpty() && newPassword.isEmpty()) {
            return "Wprowadź co najmniej jedną zmianę"
        }

        // Username validation if provided
        if (newUsername.isNotEmpty()) {
            if (newUsername.length < 6) {
                return "\n" +
                        "Nazwa użytkownika musi mieć co najmniej 6 znaków"
            }
            if (newUsername.length > 32) {
                return "Nazwa użytkownika nie może przekraczać 16 znaków"
            }
        }

        // Password validation if provided
        if (newPassword.isNotEmpty()) {
            if (newPassword.length < 14) {
                return "Hasło musi mieć co najmniej 14 znaków"
            }
            if (newPassword.length > 32) {
                return "Hasło może mieć maksymalnie 32 znaki"
            }
            if (newPassword != confirmPassword) {
                return "Hasła nie pasują"
            }
        }

        return null
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

    fun updateFirstName(newFirstName: String) {
        _firstName.value = newFirstName
    }

    fun updateLastName(newLastName: String) {
        _lastName.value = newLastName
    }

    fun updateCountry(newCountry: String?) {
        _selectedCountry.value = newCountry
    }

    fun fetchUserPersonalData() {
        viewModelScope.launch {
            _personalDataLoading.value = true
            _personalDataError.value = null

            val authRepository = AuthRepository(getApplication())
            val refreshResult = authRepository.refreshAccessToken()

            if (refreshResult.isFailure) {
                _personalDataError.value = "Session expired. Please log in again."
                _personalDataLoading.value = false
                handleTokenExpiration()
                return@launch
            }

            val result = userRepository.getUserPersonalData()

            if (result.isSuccess) {
                val data = result.getOrNull()
                _personalData.value = data

                // Zaktualizuj pola formularza
                _firstName.value = data?.firstName ?: ""
                _lastName.value = data?.lastName ?: ""
                _selectedCountry.value = data?.country
                _isPersonalDetailsPublic.value = data?.isPersonalDetailsPublic ?: true


                _personalDataError.value = null
            } else {
                _personalDataError.value = result.exceptionOrNull()?.message ?: "Unknown error occurred"
                _personalData.value = null
            }

            _personalDataLoading.value = false
        }
    }

    fun updatePersonalData() {
        val currentFirstName = _firstName.value ?: ""
        val currentLastName = _lastName.value ?: ""
        val currentCountry = _selectedCountry.value

        // Walidacja
        if (currentFirstName.isBlank()) {
            _personalDataError.value = "Imię nie może być puste"
            return
        }

        if (currentLastName.isBlank()) {
            _personalDataError.value = "Nazwisko nie może być puste"
            return
        }

        viewModelScope.launch {
            _personalDataLoading.value = true
            _personalDataError.value = null

            val authRepository = AuthRepository(getApplication())
            val refreshResult = authRepository.refreshAccessToken()

            if (refreshResult.isFailure) {
                _personalDataError.value = "Session expired. Please log in again."
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
                // Opcjonalnie: odśwież dane
                fetchUserPersonalData()
            } else {
                _personalDataError.value = result.exceptionOrNull()?.message ?: "Błąd podczas aktualizacji danych"
            }

            _personalDataLoading.value = false
        }
    }

    // FITNESS DETAILS -------------------------------------------------------------
    private val _fitnessData = MutableStateFlow<UserFitnessData?>(null)
    val fitnessData: StateFlow<UserFitnessData?> = _fitnessData.asStateFlow()

    private val _fitnessDataLoading = MutableStateFlow(false)
    val fitnessDataLoading: StateFlow<Boolean> = _fitnessDataLoading.asStateFlow()

    private val _fitnessDataError = MutableStateFlow<String?>(null)
    val fitnessDataError: StateFlow<String?> = _fitnessDataError.asStateFlow()

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

    fun fetchUserFitnessData() {
        viewModelScope.launch {
            _fitnessDataLoading.value = true
            _fitnessDataError.value = null

            val authRepository = AuthRepository(getApplication())
            val refreshResult = authRepository.refreshAccessToken()

            if (refreshResult.isFailure) {
                _fitnessDataError.value = "Session expired. Please log in again."
                _fitnessDataLoading.value = false
                handleTokenExpiration()
                return@launch
            }

            val result = userRepository.getUserFitnessData()

            if (result.isSuccess) {
                val data = result.getOrNull()
                _fitnessData.value = data

                // Zaktualizuj pola formularza
                _selectedGender.value = data?.gender
                _age.value = data?.age?.toString() ?: ""
                _height.value = data?.height?.toString() ?: ""
                _weight.value = data?.weight?.toString() ?: ""
                _isFitnessDetailsPublic.value = data?.isFitnessDetailsPublic ?: true

                _fitnessDataError.value = null
            } else {
                _fitnessDataError.value = result.exceptionOrNull()?.message ?: "Unknown error occurred"
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

            val authRepository = AuthRepository(getApplication())
            val refreshResult = authRepository.refreshAccessToken()

            if (refreshResult.isFailure) {
                _fitnessDataError.value = "Session expired. Please log in again."
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
                fetchUserFitnessData()
            } else {
                _fitnessDataError.value = result.exceptionOrNull()?.message ?: "Błąd podczas aktualizacji danych"
            }

            _fitnessDataLoading.value = false
        }
    }
}