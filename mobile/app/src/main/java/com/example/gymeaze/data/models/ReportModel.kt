package com.example.gymeaze.data.models

data class CreateReportRequest(
    val reportedItemId: String,
    val reportedItemType: String,
    val reportType: String,
    val reason: String? = null
)

data class ReportResponse(
    val _id: String,
    val reporterId: String,
    val reportedItemId: String,
    val reportedItemType: String,
    val reportType: String,
    val reason: String?,
    val status: String,
    val createdAt: String
)

