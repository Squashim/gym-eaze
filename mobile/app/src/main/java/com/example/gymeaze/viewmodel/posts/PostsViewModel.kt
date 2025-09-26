package com.example.gymeaze.viewmodel.posts

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.Post
import com.example.gymeaze.data.models.PostUi
import com.example.gymeaze.data.models.User
import com.example.gymeaze.data.repository.AuthRepository
import com.example.gymeaze.data.repository.PostsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import com.example.gymeaze.viewmodel.posts.toUiModel

data class PostUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    val content: String = "",
    val imageFile: Uri? = null,
    val videoFile: Uri? = null,
    val hashtags: List<String> = emptyList(),
    val imageRemoved: Boolean = false,
    val videoRemoved: Boolean = false
)

class PostsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostsRepository(application)

    private val _uiState = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    // Rozdzielone cache dla różnych trybów
    private var allPostsCache = mutableListOf<PostUi>()
    private var followedPostsCache = mutableListOf<PostUi>()
    private var userPostsCache = mutableListOf<PostUi>()
    private var allPostsCursor: String? = null
    private var followedPostsCursor: String? = null
    private var userPostsCursor: String? = null

    private var allPostsHasMore = true
    private var followedPostsHasMore = true
    private var userPostsHasMore = true

    private var allPostsHashtags: List<String>? = null
    private var followedPostsHashtags: List<String>? = null
    private var userPostsHashtags: List<String>? = null

    private var allPostsLastLoadTime = 0L
    private var followedPostsLastLoadTime = 0L
    private var userPostsLastLoadTime = 0L

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    private val _imageFile = MutableStateFlow<Uri?>(null)
    val imageFile: StateFlow<Uri?> = _imageFile.asStateFlow()

    private val _videoFile = MutableStateFlow<Uri?>(null)
    val videoFile: StateFlow<Uri?> = _videoFile.asStateFlow()

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
            _uiState.value = _uiState.value.copy(imageFile = null, imageRemoved = true)
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
                _uiState.value = _uiState.value.copy(imageFile = uri, videoFile = null, imageRemoved = false)
            }
        }
    }

    fun setVideo(uri: Uri?) {
        if (uri == null) {
            _videoFile.value = null
            _uiState.value = _uiState.value.copy(videoFile = null, videoRemoved = true)
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
                _uiState.value = _uiState.value.copy(videoFile = uri, imageFile = null, videoRemoved = false)
            }
        }
    }

    fun removeImage() {
        _imageFile.value = null
        _uiState.value = _uiState.value.copy(imageFile = null, imageRemoved = true)
    }

    fun removeVideo() {
        _videoFile.value = null
        _uiState.value = _uiState.value.copy(videoFile = null, videoRemoved = true)
    }

    fun setHashtags(tags: List<String>) {
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
        _uiState.value = PostUiState()
    }

    fun fetchPosts(refresh: Boolean = false) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                if (refresh) {
                    allPostsCursor = null
                    allPostsHasMore = true
                    allPostsCache.clear()
                    _posts.value = emptyList()
                }

                if (!allPostsHasMore && !refresh) return@launch
                if (_isLoading.value && !refresh) return@launch

                _isLoading.value = true
                _error.value = null

                if (!refresh && System.currentTimeMillis() - allPostsLastLoadTime < 1000) {
                    delay(1000 - (System.currentTimeMillis() - allPostsLastLoadTime))
                }

                // Najpierw pobierz polubienia
                fetchUserLikes()

                val response = repository.getPosts(
                    hashtags = allPostsHashtags,
                    followedOnly = false,
                    cursor = allPostsCursor,
                    limit = 15
                )

                if (response != null) {
                    val newPostsUi = response.items.map {
                        it.toUiModel(isLiked = likedPosts.contains(it._id))
                    }

                    val updatedPosts = if (refresh || allPostsCursor == null) {
                        allPostsCache.clear()
                        allPostsCache.addAll(newPostsUi)
                        newPostsUi
                    } else {
                        allPostsCache.addAll(newPostsUi)
                        allPostsCache.toList()
                    }

                    _posts.value = updatedPosts
                    allPostsCursor = response.nextCursor
                    allPostsHasMore = response.hasMore
                    allPostsLastLoadTime = System.currentTimeMillis()
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

    fun fetchFollowedUsersPosts(refresh: Boolean = false) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                if (refresh) {
                    followedPostsCursor = null
                    followedPostsHasMore = true
                    followedPostsCache.clear()
                    _posts.value = emptyList()
                }

                if (!followedPostsHasMore && !refresh) return@launch
                if (_isLoading.value && !refresh) return@launch

                _isLoading.value = true
                _error.value = null

                if (!refresh && System.currentTimeMillis() - followedPostsLastLoadTime < 1000) {
                    delay(1000 - (System.currentTimeMillis() - followedPostsLastLoadTime))
                }

                // Najpierw pobierz polubienia
                fetchUserLikes()

                val response = repository.getPosts(
                    hashtags = followedPostsHashtags,
                    followedOnly = true,
                    cursor = followedPostsCursor,
                    limit = 15
                )

                if (response != null) {
                    val newPostsUi = response.items.map {
                        it.toUiModel(isLiked = likedPosts.contains(it._id))
                    }

                    val updatedPosts = if (refresh || followedPostsCursor == null) {
                        followedPostsCache.clear()
                        followedPostsCache.addAll(newPostsUi)
                        newPostsUi
                    } else {
                        followedPostsCache.addAll(newPostsUi)
                        followedPostsCache.toList()
                    }

                    _posts.value = updatedPosts
                    followedPostsCursor = response.nextCursor
                    followedPostsHasMore = response.hasMore
                    followedPostsLastLoadTime = System.currentTimeMillis()

                    // WAŻNE: Jeśli nie ma postów, wyczyść błąd
                    if (updatedPosts.isEmpty() && followedPostsHashtags == null) {
                        // To jest normalne - użytkownik nie obserwuje nikogo lub nikt nie ma postów
                    } else if (updatedPosts.isEmpty() && followedPostsHashtags != null) {
                        // To może być komunikat o braku wyników wyszukiwania
                        // Możesz ustawić specjalny stan dla "brak wyników wyszukiwania"
                    }
                } else {
                    _error.value = "Błąd pobierania postów"
                }
            } catch (e: Exception) {
                Log.e("PostsViewModel", "Error fetching followed posts", e)
                _error.value = e.message ?: "Błąd pobierania postów"
            } finally {
                _isLoading.value = false
            }
        }
    }
// W fetchPosts() użyj allPostsCache
// W fetchFollowedUsersPosts() użyj followedPostsCache

    fun loadMoreFollowed() {
        loadMoreJob?.cancel()
        loadMoreJob = viewModelScope.launch {
            if (!_isLoading.value && followedPostsHasMore && _posts.value.isNotEmpty()) {
                fetchFollowedUsersPosts()
            }
        }
    }

    fun searchFollowedByHashtags(hashtags: List<String>) {
        searchJob?.cancel()
        loadMoreJob?.cancel()

        followedPostsHashtags = hashtags.takeIf { it.isNotEmpty() }

        // WAŻNE: Wyczyść cache i stan przed nowym wyszukiwaniem
        followedPostsCache.clear()
        followedPostsCursor = null
        followedPostsHasMore = true
        _posts.value = emptyList()

        fetchFollowedUsersPosts(refresh = true)
    }

    fun fetchCurrentUserPosts(refresh: Boolean = false) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                val userResult = authRepository.getCurrentUser()
                userResult.onSuccess { user ->
                    val userId = user._id

                    if (refresh) {
                        userPostsCursor = null
                        userPostsHasMore = true
                        userPostsCache.clear()
                        _posts.value = emptyList()
                    }

                    if (!userPostsHasMore && !refresh) return@launch
                    if (_isLoading.value && !refresh) return@launch

                    _isLoading.value = true
                    _error.value = null

                    val responseResult = repository.getUserPosts(
                        userId = userId,
                        cursor = userPostsCursor,
                        limit = 15
                    )

                    responseResult.onSuccess { response ->
                        val newPostsUi = response.items.map {
                            it.toUiModel(isLiked = likedPosts.contains(it._id))
                        }

                        val updatedPosts = if (refresh || userPostsCursor == null) {
                            userPostsCache.clear()
                            userPostsCache.addAll(newPostsUi)
                            newPostsUi
                        } else {
                            userPostsCache.addAll(newPostsUi)
                            userPostsCache.toList()
                        }

                        _posts.value = updatedPosts
                        userPostsCursor = response.nextCursor
                        userPostsHasMore = response.hasMore
                        allPostsLastLoadTime = System.currentTimeMillis()
                    }.onFailure {
                        _error.value = it.message ?: "Błąd pobierania postów"
                    }
                }.onFailure {
                    _error.value = "Nie udało się pobrać użytkownika"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Błąd pobierania postów"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun clearFollowedSearch() {
        searchJob?.cancel()
        loadMoreJob?.cancel()

        followedPostsHashtags = null

        // WAŻNE: Wyczyść cache i przywróć domyślny stan
        followedPostsCache.clear()
        followedPostsCursor = null
        followedPostsHasMore = true
        _posts.value = emptyList()

        fetchFollowedUsersPosts(refresh = true)
    }




    suspend fun fetchUserLikes() {
        val result = repository.getUserLikes()
        result.onSuccess { likesResponse ->
            likedPosts = likesResponse.posts
        }
    }

    fun searchByHashtags(hashtags: List<String>) {
        searchJob?.cancel()
        loadMoreJob?.cancel()

        allPostsHashtags = hashtags.takeIf { it.isNotEmpty() }

        // Wyczyść cache przed nowym wyszukiwaniem
        allPostsCache.clear()
        allPostsCursor = null
        allPostsHasMore = true
        _posts.value = emptyList()

        fetchPosts(refresh = true)
    }

    fun clearSearch() {
        searchJob?.cancel()
        loadMoreJob?.cancel()

        allPostsHashtags = null

        // WAŻNE: Wyczyść cache i przywróć domyślny stan
        allPostsCache.clear()
        allPostsCursor = null
        allPostsHasMore = true
        _posts.value = emptyList()

        fetchPosts(refresh = true)
    }

    fun loadMore() {
        loadMoreJob?.cancel()
        loadMoreJob = viewModelScope.launch {
            if (!_isLoading.value && allPostsHasMore && _posts.value.isNotEmpty()) {
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


    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        loadMoreJob?.cancel()

        // Wyczyść wszystkie cache
        allPostsCache.clear()
        followedPostsCache.clear()
        userPostsCache.clear()
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

    fun updatePost(
        postId: String,
        content: String,
        hashtags: List<String>,
        imageUri: Uri?,
        videoUri: Uri?
    ) {
        val state = _uiState.value
        _uiState.value = state.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val imageToSend = if (state.imageRemoved) null else imageUri
            val videoToSend = if (state.videoRemoved) null else videoUri

            val result = repository.updatePost(
                postId = postId,
                content = content,
                hashtags = hashtags,
                imageUri = imageToSend,
                videoUri = videoToSend,
                imageRemoved = state.imageRemoved,   // 🔥 dodajemy
                videoRemoved = state.videoRemoved    // 🔥 dodajemy
            )

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, success = true)
                fetchPosts(refresh = true)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
            }
        }
    }


    private val authRepository = AuthRepository(application.applicationContext)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun fetchCurrentUser() {
        viewModelScope.launch {
            val result = authRepository.getCurrentUser()
            result.onSuccess { user ->
                _currentUser.value = user
            }.onFailure {
            }
        }
    }
    fun searchByHashtagsForCurrentUser(hashtags: List<String>) {
        searchJob?.cancel()
        loadMoreJob?.cancel()

        userPostsHashtags = hashtags.takeIf { it.isNotEmpty() }

        // Wyczyść cache przed nowym wyszukiwaniem
        userPostsCache.clear()
        userPostsCursor = null
        userPostsHasMore = true
        _posts.value = emptyList()

        fetchCurrentUserPosts(refresh = true)
    }

    fun refreshFollowedPosts() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                fetchFollowedUsersPosts(refresh = true)
                fetchUserLikes()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun clearCurrentUserSearch() {
        searchJob?.cancel()
        loadMoreJob?.cancel()

        userPostsHashtags = null

        // WAŻNE: Wyczyść cache i przywróć domyślny stan
        userPostsCache.clear()
        userPostsCursor = null
        userPostsHasMore = true
        _posts.value = emptyList()

        fetchCurrentUserPosts(refresh = true)
    }


    fun refreshCurrentUserPosts() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                fetchCurrentUserPosts(refresh = true)
                fetchUserLikes()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun loadMoreCurrentUser() {
        loadMoreJob?.cancel()
        loadMoreJob = viewModelScope.launch {
            if (!_isLoading.value && userPostsHasMore && _posts.value.isNotEmpty()) {
                fetchCurrentUserPosts()
            }
        }
    }


    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = repository.deletePost(postId)
            result.onSuccess { message ->
                // Usuń post z lokalnej listy
                val currentPosts = _posts.value.toMutableList()
                currentPosts.removeAll { it.id == postId }
                _posts.value = currentPosts

                // Usuń ze wszystkich cache
                allPostsCache.removeAll { it.id == postId }
                followedPostsCache.removeAll { it.id == postId }
                userPostsCache.removeAll { it.id == postId }

                _isLoading.value = false
            }.onFailure { error ->
                _error.value = error.message ?: "Błąd podczas usuwania posta"
                _isLoading.value = false
            }
        }
    }

}


