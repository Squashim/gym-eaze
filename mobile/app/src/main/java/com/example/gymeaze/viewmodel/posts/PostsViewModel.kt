package com.example.gymeaze.viewmodel.posts

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.Post
import com.example.gymeaze.data.models.PostUi
import com.example.gymeaze.data.repository.PostsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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

    private val _posts = MutableStateFlow<List<PostUi>>(emptyList())
    val posts: StateFlow<List<PostUi>> = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var currentCursor: String? = null
    private var hasMore = true
    private var currentHashtags: List<String>? = null
    private var loadMoreJob: Job? = null
    private var searchJob: Job? = null

    private var cachedPosts = mutableListOf<PostUi>()
    private var lastLoadTime = 0L

    private var likedPosts: List<String> = emptyList()

    init {
        fetchPosts()
    }

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
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
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
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
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
                fetchPosts(refresh = true)
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
        _error.value = null
    }

    fun clearData() {
        _content.value = ""
        _imageFile.value = null
        _videoFile.value = null
        _hashtags.value = emptyList()
        _uiState.value = PostUiState()
    }

    fun fetchPosts(refresh: Boolean = false) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            try {
                if (refresh) {
                    currentCursor = null
                    hasMore = true
                    cachedPosts.clear()
                    _posts.value = emptyList()
                }

                if (!hasMore && !refresh) return@launch
                if (_isLoading.value && !refresh) return@launch

                _isLoading.value = true
                _error.value = null

                if (!refresh && System.currentTimeMillis() - lastLoadTime < 1000) {
                    delay(1000 - (System.currentTimeMillis() - lastLoadTime))
                }

                val response = repository.getPosts(
                    hashtags = currentHashtags,
                    followedOnly = false,
                    cursor = currentCursor,
                    limit = 15
                )

                if (response != null) {
                    // NAJPIERW zaktualizuj polubienia
                    fetchUserLikes()

                    // POTEM dopiero mapuj posty (z już zaktualizowanymi polubieniami)
                    val newPostsUi = response.items.map { it.toUiModel() }

                    val updatedPosts = if (refresh || currentCursor == null) {
                        cachedPosts.clear()
                        cachedPosts.addAll(newPostsUi)
                        newPostsUi
                    } else {
                        cachedPosts.addAll(newPostsUi)
                        cachedPosts.toList()
                    }

                    _posts.value = updatedPosts
                    currentCursor = response.nextCursor
                    hasMore = response.hasMore
                    lastLoadTime = System.currentTimeMillis()
                } else {
                    _error.value = "Błąd pobierania postów"
                }
            } catch (e: Exception) {
                Log.e("PostsViewModel", "Error fetching posts", e)
                _error.value = e.message ?: "Błąd pobierania postów"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchUserLikes() {
        val result = repository.getUserLikes()
        result.onSuccess { likesResponse ->
            likedPosts = likesResponse.posts
        }
    }


    fun searchByHashtags(hashtags: List<String>) {
        searchJob?.cancel()
        loadMoreJob?.cancel()

        currentHashtags = hashtags.takeIf { it.isNotEmpty() }
        fetchPosts(refresh = true)
    }

    fun clearSearch() {
        searchJob?.cancel()
        loadMoreJob?.cancel()
        currentHashtags = null
        fetchPosts(refresh = true)
    }

    fun loadMore() {
        loadMoreJob?.cancel()

        loadMoreJob = viewModelScope.launch {
            if (!_isLoading.value && hasMore && _posts.value.isNotEmpty()) {
                fetchPosts()
            }
        }
    }

    fun refreshPosts() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                fetchPosts(refresh = true)
                fetchUserLikes()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun Post.toUiModel(): PostUi {
        return PostUi(
            id = _id,
            username = author.username,
            userAvatar = author.profileDetails?.profilePicture.orEmpty(),
            timeAgo = createdAt.formatToRelativeTime(),
            content = content,
            mediaUrl = video ?: image,
            likes = likes ?: 0,
            isLiked = likedPosts.contains(_id),
            hashtags = hashtags
        )
    }

    private fun String.formatToRelativeTime(): String {
        return try {
            val instant = Instant.parse(this)
            val now = Instant.now()
            val minutesAgo = ChronoUnit.MINUTES.between(instant, now)
            val hoursAgo = ChronoUnit.HOURS.between(instant, now)
            val daysAgo = ChronoUnit.DAYS.between(instant, now)

            when {
                minutesAgo < 1 -> "przed chwilą"
                minutesAgo < 60 -> "${minutesAgo}min"
                hoursAgo < 24 -> "${hoursAgo}h"
                daysAgo < 7 -> "${daysAgo}d"
                daysAgo < 30 -> "${daysAgo / 7}tyg"
                else -> DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    .withZone(ZoneId.systemDefault())
                    .format(instant)
            }
        } catch (e: Exception) {
            Log.e("PostsViewModel", "Error formatting date: $this", e)
            this
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        loadMoreJob?.cancel()
        cachedPosts.clear()
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            val currentPosts = _posts.value.toMutableList()
            val idx = currentPosts.indexOfFirst { it.id == postId }
            if (idx == -1) return@launch

            val post = currentPosts[idx]
            val result = repository.toggleLike(postId)
            result.onSuccess { response ->
                val isLiked = response.message == "Dodano polubienie"
                val updatedPost = post.copy(
                    likes = response.likes,
                    isLiked = isLiked
                )
                currentPosts[idx] = updatedPost
                _posts.value = currentPosts
            }.onFailure {
                _error.value = it.message
            }
        }
    }
}
