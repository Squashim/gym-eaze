package com.example.gymeaze.data.models

data class Notification(
    val _id: String,
    val userId: String,
    val type: String,
    val message: String,
    val relatedId: String? = null,
    val isRead: Boolean = false,
    val createdAt: String
)

data class NotificationsResponse(
    val items: List<Notification>,
    val hasMore: Boolean,
    val nextCursor: String?,
    val total: Int
)

data class DeleteNotificationsResponse(
    val message: String
)