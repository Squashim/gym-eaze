package com.example.gymeaze.data.models

import com.example.gymeaze.util.formatToRelativeTime

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
    val commentsCount: Int?
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
    val isOwner: Boolean = false,
    val commentsCount: Int,
)

data class ToggleLikeResponse(
    val message: String,
    val likes: Int
)

data class LikesResponse(
    val posts: List<String>,
    val exercises: List<String>,
    val trainingPlans: List<String>,
    val comments: List<String>,
)

data class PostResponse(
    val items: List<Post>,
    val hasMore: Boolean,
    val nextCursor: String?
)

data class Comment(
    val _id: String,
    val content: String,
    val createdAt: String,
    val author: Author,
    val likes: Int?,
    val repliesCount: Int?,
    val updatedAt: String?,
    val isEdited: Boolean?,
    val parentId: String?,
    val parentType: String?
)

data class GetCommentsResponse(
    val items: List<Comment>,
    val hasMore: Boolean,
    val nextCursor: String?
)

data class CreateCommentRequest(
    val content: String,
    val parentType: String,
    val parentId: String
)

data class MessageCommentResponse(
    val message: String
)

data class ToggleLikeCommentResponse(
    val message: String,
    val likes: Int
)

fun Comment.timeAgo(): String {
    return createdAt.formatToRelativeTime()
}

data class UpdateCommentRequest(
    val content: String
)