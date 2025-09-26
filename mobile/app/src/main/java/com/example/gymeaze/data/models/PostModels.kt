package com.example.gymeaze.data.models

data class AddPostResponse(
    val message: String
)

data class GetPostsResponse(
    val items: List<Post>,
    val hasMore: Boolean,
    val nextCursor: String?
)

data class Post(
    val _id: String,
    val content: String,
    val createdAt: String,
    val hashtags: List<String>,
    val author: Author,
    val image: String?,
    val video: String?,
    val likes: Int?,
    val updatedAt: String?,
)

data class Author(
    val _id: String,
    val username: String,
    val profileDetails: ProfileDetails?
)

data class ProfileDetails(
    val profilePicture: String?
)

data class PostUi(
    val id: String,
    val username: String,
    val userAvatar: String,
    val timeAgo: String,
    val content: String,
    val mediaUrl: String?,
    val likes: Int,
    val isLiked: Boolean,
    val hashtags: List<String> = emptyList(),
    val isOwner: Boolean = false
)

data class ToggleLikeResponse(
    val message: String,
    val likes: Int
)

data class LikesResponse(
    val posts: List<String>,
    val exercises: List<String>,
    val trainingPlans: List<String>
)

data class PostResponse(
    val items: List<Post>,
    val hasMore: Boolean,
    val nextCursor: String?
)