package com.example.gymeaze.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.repository.PostsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PostUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    val content: String = "",
    val imageFile: Uri? = null,
    val videoFile: Uri? = null,
    val hashtags: List<String> = emptyList()
)

class PostsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostsRepository(application)

    private val _uiState = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    private val _imageFile = MutableStateFlow<Uri?>(null)
    val imageFile: StateFlow<Uri?> = _imageFile.asStateFlow()

    private val _videoFile = MutableStateFlow<Uri?>(null)
    val videoFile: StateFlow<Uri?> = _videoFile.asStateFlow()

    private val _hashtags = MutableStateFlow<List<String>>(emptyList())
    val hashtags: StateFlow<List<String>> = _hashtags.asStateFlow()

    fun updateContent(newContent: String) {
        _content.value = newContent
        _uiState.value = _uiState.value.copy(content = newContent)
    }

    fun setImage(uri: Uri?) {
        if (uri == null) {
            _imageFile.value = null
            _uiState.value = _uiState.value.copy(imageFile = null)
            return
        }

        val context = getApplication<Application>().applicationContext
        val cr = context.contentResolver
        val type = cr.getType(uri)
        val allowedTypes = listOf("image/jpeg", "image/png", "image/webp")
        val size = cr.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
            if (cursor.moveToFirst() && sizeIndex != -1) cursor.getLong(sizeIndex) else null
        }

        when {
            type == null || !(allowedTypes.contains(type)) -> {
                _uiState.value = _uiState.value.copy(error = "Dozwolone formaty zdjęć: JPG, PNG, WEBP.")
                return
            }
            size != null && size > 5 * 1024 * 1024 -> { // 5MB
                _uiState.value = _uiState.value.copy(error = "Obraz jest za duży (maks. 5MB).")
                return
            }
            else -> {
                _imageFile.value = uri
                _videoFile.value = null
                _uiState.value = _uiState.value.copy(imageFile = uri, videoFile = null)
            }
        }
    }


    fun setVideo(uri: Uri?) {
        if (uri == null) {
            _videoFile.value = null
            _uiState.value = _uiState.value.copy(videoFile = null)
            return
        }

        val context = getApplication<Application>().applicationContext
        val cr = context.contentResolver
        val type = cr.getType(uri)
        val allowedTypes = listOf("video/mp4", "video/webm")
        val size = cr.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
            if (cursor.moveToFirst() && sizeIndex != -1) cursor.getLong(sizeIndex) else null
        }

        when {
            type == null || !(allowedTypes.contains(type)) -> {
                _uiState.value = _uiState.value.copy(error = "Dozwolone formaty filmów: MP4, WEBM.")
                return
            }
            size != null && size > 20 * 1024 * 1024 -> {
                _uiState.value = _uiState.value.copy(error = "Film jest za duży (maks. 20MB).")
                return
            }
            else -> {
                _videoFile.value = uri
                _imageFile.value = null
                _uiState.value = _uiState.value.copy(videoFile = uri, imageFile = null)
            }
        }
    }

    fun setHashtags(tags: List<String>) {
        _hashtags.value = tags
        _uiState.value = _uiState.value.copy(hashtags = tags)
    }

    fun createPost() {
        val currentState = _uiState.value
        val content = currentState.content
        val image = currentState.imageFile
        val video = currentState.videoFile
        val hashtagsList = currentState.hashtags

        _uiState.value = currentState.copy(isLoading = true, error = null, success = false)
        viewModelScope.launch {
            val result = repository.createPost(content, hashtagsList, image, video)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, success = true)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
            }
        }
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(success = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearData() {
        _content.value = ""
        _imageFile.value = null
        _videoFile.value = null
        _hashtags.value = emptyList()
        _uiState.value = PostUiState()
    }
}