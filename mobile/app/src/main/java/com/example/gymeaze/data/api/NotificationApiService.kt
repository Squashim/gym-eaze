package com.example.gymeaze.data.api

import com.example.gymeaze.data.models.DeleteNotificationsResponse
import com.example.gymeaze.data.models.NotificationsResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NotificationApiService {
    @GET("/api/notifications")
    suspend fun getNotifications(
        @Query("limit") limit: Int = 10,
        @Query("cursor") cursor: String? = null
    ): Response<NotificationsResponse>

    @DELETE("/api/notifications")
    suspend fun deleteAllNotifications(): Response<DeleteNotificationsResponse>
}