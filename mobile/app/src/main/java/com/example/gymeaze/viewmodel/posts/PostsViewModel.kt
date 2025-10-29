package com.example.gymeaze.viewmodel.posts

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymeaze.data.models.Comment
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
    val videoRemoved: Boolean = false,

)

class PostsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostsRepository(application)

    private val _uiState = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()
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

    private var userPostsByUsernameCursor: String? = null
    private var userPostsByUsernameHasMore = true
    private val userPostsByUsernameCache = mutableListOf<PostUi>()
    private var userPostsByUsernameLastLoadTime = 0L

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
    private val authRepository = AuthRepository(application.applicationContext)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // W PostsViewModel.kt - dodaj te zmienne
    private val _userPostsByUsername = MutableStateFlow<List<PostUi>>(emptyList())
    val userPostsByUsername: StateFlow<List<PostUi>> = _userPostsByUsername.asStateFlow()

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
            size != null && size > 5 * 1024 * 1024 -> {
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

                    if (updatedPosts.isEmpty() && followedPostsHashtags == null) {
                    } else if (updatedPosts.isEmpty() && followedPostsHashtags != null) {
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
        allPostsCache.clear()
        followedPostsCache.clear()
        userPostsCache.clear()
        _userPostsByUsername.value = emptyList()
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            Log.d("PostsViewModel", "Toggling like for post: $postId")

            val result = repository.toggleLike(postId)
            result.onSuccess { response ->
                val isLiked = response.message == "Dodano polubienie"

                Log.d("PostsViewModel", "Like toggled successfully: likes=${response.likes}, isLiked=$isLiked")

                // Zaktualizuj wszystkie listy, które mogą zawierać ten post
                updatePostInAllLists(postId, response.likes, isLiked)

            }.onFailure { error ->
                Log.e("PostsViewModel", "Failed to toggle like: ${error.message}")
                _error.value = error.message
            }
        }
    }

    private fun updatePostInAllLists(postId: String, newLikes: Int, isLiked: Boolean) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                post.copy(likes = newLikes, isLiked = isLiked)
            } else post
        }

        _userPostsByUsername.value = _userPostsByUsername.value.map { post ->
            if (post.id == postId) {
                post.copy(likes = newLikes, isLiked = isLiked)
            } else post
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
                imageRemoved = state.imageRemoved,
                videoRemoved = state.videoRemoved
            )

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, success = true)
                fetchPosts(refresh = true)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
            }
        }
    }

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
                val currentPosts = _posts.value.toMutableList()
                currentPosts.removeAll { it.id == postId }
                _posts.value = currentPosts

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

    //---------------------------------------------------KOMENTARZE W POSTACH---------------------
    private val _isLoadingComments = MutableStateFlow(false)
    val isLoadingComments: StateFlow<Boolean> = _isLoadingComments

    private val _repliesMap = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())

    private val _likedComments = MutableStateFlow<Set<String>>(emptySet())
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    private val _hasMoreComment = MutableStateFlow(false)
    val hasMoreComment: StateFlow<Boolean> = _hasMoreComment

    private val _nextCursorComment = MutableStateFlow<String?>(null)
    val nextCursorComment: StateFlow<String?> = _nextCursorComment

    fun loadComments(postId: String, cursor: String? = null, limit: Int = 10, refresh: Boolean = false) {
        viewModelScope.launch {
            val effectiveCursor = if (refresh) null else cursor
            val response = repository.getCommentsForPost(postId, effectiveCursor, limit)
            _comments.value = response?.items ?: emptyList()
            _hasMoreComment.value = response?.hasMore ?: false
            _nextCursorComment.value = response?.nextCursor
        }
    }

    fun loadMoreComments(postId: String, limit: Int = 10) {
        val cursor = _nextCursorComment.value
        if (_hasMoreComment.value && cursor != null && !_isLoadingComments.value) {
            viewModelScope.launch {
                _isLoadingComments.value = true
                val response = repository.getCommentsForPost(postId, cursor, limit)
                val current = _comments.value
                val newComments = response?.items ?: emptyList()
                _comments.value = current + newComments
                _hasMoreComment.value = response?.hasMore ?: false
                _nextCursorComment.value = response?.nextCursor
                //_commentsCount.value = _comments.value.size
                _isLoadingComments.value = false
            }
        }
    }

    fun loadReplies(commentId: String, cursor: String? = null, limit: Int = 10) {
        viewModelScope.launch {
            val response = repository.getRepliesForComment(commentId, cursor, limit)
            if (response != null) {
                val currentMap = _repliesMap.value.toMutableMap()
                currentMap[commentId] = response.items
                _repliesMap.value = currentMap
            }
        }
    }

    fun getRepliesForComment(commentId: String): StateFlow<List<Comment>> {
        return _repliesMap.map { it[commentId] ?: emptyList() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
    }

    fun addComment(postId: String, content: String) {
        viewModelScope.launch {
            val response = repository.createComment(
                content = content,
                parentType = "Post",
                parentId = postId
            )
            if (response != null) {
                loadComments(postId)
            }
        }
    }

    fun addReply(commentId: String, content: String) {
        viewModelScope.launch {
            val response = repository.createComment(
                content = content,
                parentType = "Komentarz",
                parentId = commentId
            )
            if (response != null) {
                loadReplies(commentId)

                val updatedComments = _comments.value.map { comment ->
                    if (comment._id == commentId) {
                        comment.copy(repliesCount = (comment.repliesCount ?: 0) + 1)
                    } else {
                        comment
                    }
                }
                _comments.value = updatedComments
            }
        }
    }

    fun toggleCommentLike(commentId: String) {
        viewModelScope.launch {
            val response = repository.toggleCommentLike(commentId)
            if (response != null) {
                val isLiked = response.message.contains("Dodano")

                _likedComments.value = if (isLiked) {
                    _likedComments.value + commentId
                } else {
                    _likedComments.value - commentId
                }

                updateCommentLikesWithServerValue(commentId, response.likes)
            }
        }
    }

    private fun updateCommentLikesWithServerValue(commentId: String, likes: Int) {
        _comments.value = _comments.value.map { comment ->
            if (comment._id == commentId) {
                comment.copy(likes = likes)
            } else {
                comment
            }
        }

        _repliesMap.value = _repliesMap.value.mapValues { (_, replies) ->
            replies.map { reply ->
                if (reply._id == commentId) {
                    reply.copy(likes = likes)
                } else {
                    reply
                }
            }
        }
    }

    fun isCommentLiked(commentId: String): StateFlow<Boolean> {
        return _likedComments.map { it.contains(commentId) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                false
            )
    }

    fun editComment(comment: Comment, newContent: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.editComment(comment._id, newContent)
            onResult(result.isSuccess, result.getOrElse { it.message ?: "Błąd edycji" })
            if (result.isSuccess) {
                loadComments(comment.parentId ?: "")
            }
        }
    }

    fun deleteComment(comment: Comment, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteComment(comment._id)
            onResult(result.isSuccess, result.getOrElse { it.message ?: "Błąd usuwania" })
            if (result.isSuccess) {
                loadComments(comment.parentId ?: "")
            }
        }
    }

    fun fetchUserCommentLikes() {
        viewModelScope.launch {
            val response = repository.getUserLikes()
            response.onSuccess { likesResponse ->
                _likedComments.value = likesResponse.comments.map { it.toString() }.toSet()
            }
        }
    }

    fun clearPosts() {
        likedPosts = emptyList()
        _likedComments.value = emptySet()
        allPostsCache.clear()
        followedPostsCache.clear()
        userPostsCache.clear()
        _posts.value = emptyList()
    }

    // W PostsViewModel.kt - poprawiona funkcja
    fun fetchUserPostsByUsername(username: String, refresh: Boolean = false) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                if (refresh) {
                    userPostsByUsernameCursor = null
                    userPostsByUsernameHasMore = true
                    userPostsByUsernameCache.clear()
                    _userPostsByUsername.value = emptyList() // UŻYJ nowego Flow
                }

                if (!userPostsByUsernameHasMore && !refresh) return@launch
                if (_isLoading.value && !refresh) return@launch

                _isLoading.value = true
                _error.value = null

                // Sprawdź cache czasowy
                if (!refresh && System.currentTimeMillis() - userPostsByUsernameLastLoadTime < 1000) {
                    delay(1000 - (System.currentTimeMillis() - userPostsByUsernameLastLoadTime))
                }

                // Pobierz polubienia użytkownika
                fetchUserLikes()

                // UŻYJ nowej funkcji z repository
                val responseResult = repository.getUserPostsByUsername(
                    username = username,
                    cursor = userPostsByUsernameCursor,
                    limit = 15
                )

                responseResult.onSuccess { response ->
                    val newPostsUi = response.items.map {
                        it.toUiModel(isLiked = likedPosts.contains(it._id))
                    }

                    val updatedPosts = if (refresh || userPostsByUsernameCursor == null) {
                        userPostsByUsernameCache.clear()
                        userPostsByUsernameCache.addAll(newPostsUi)
                        newPostsUi
                    } else {
                        userPostsByUsernameCache.addAll(newPostsUi)
                        userPostsByUsernameCache.toList()
                    }

                    _userPostsByUsername.value = updatedPosts // UŻYJ nowego Flow
                    userPostsByUsernameCursor = response.nextCursor
                    userPostsByUsernameHasMore = response.hasMore
                    userPostsByUsernameLastLoadTime = System.currentTimeMillis()

                    Log.d("PostsViewModel", "Loaded ${newPostsUi.size} posts for user $username, hasMore: $userPostsByUsernameHasMore")
                }.onFailure { error ->
                    _error.value = error.message ?: "Błąd pobierania postów użytkownika $username"
                    Log.e("PostsViewModel", "Error fetching user posts: ${error.message}")
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Błąd pobierania postów"
                Log.e("PostsViewModel", "Exception in fetchUserPostsByUsername: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearUserPostsByUsername() {
        userPostsByUsernameCursor = null
        userPostsByUsernameHasMore = true
        userPostsByUsernameCache.clear()
        _userPostsByUsername.value = emptyList()
    }
}


