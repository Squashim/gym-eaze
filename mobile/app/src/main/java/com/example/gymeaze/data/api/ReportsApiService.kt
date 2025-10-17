package com.example.gymeaze.data.api

import com.example.gymeaze.data.models.CreateReportRequest
import com.example.gymeaze.data.models.ReportResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

interface ReportsApiService {

    @POST("/api/reports")
    suspend fun createReport(
        @Body body: CreateReportRequest
    ): Response<ReportResponse>
}
