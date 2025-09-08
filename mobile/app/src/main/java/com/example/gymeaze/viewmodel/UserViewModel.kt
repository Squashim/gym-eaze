package com.example.gymeaze.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.Badge
import com.example.gymeaze.data.models.UpdateProfileResponse
import com.example.gymeaze.data.models.UserProfileDetails
import com.example.gymeaze.data.repository.AuthRepository
import com.example.gymeaze.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private fun handleTokenExpiration() {
        viewModelScope.launch {
            val authRepository = AuthRepository(getApplication())
            authRepository.clearUserSession()
            _error.value = "Session expired. Please log in again."

            // Wywołaj callback do AuthViewModel
            onTokenExpired?.invoke()
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
    }

}