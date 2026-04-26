package com.example.we_vote.domain.repository

import com.example.we_vote.domain.model.ApplicationStatus
import com.example.we_vote.domain.model.SurveyApplication

interface ApplicationRepository {
    suspend fun getApplications(email: String): List<SurveyApplication>
    suspend fun getUserApplications(email: String): List<SurveyApplication>
    suspend fun addApplication(application: SurveyApplication)
    suspend fun updateApplicationStatus(id: Int, status: ApplicationStatus)
    suspend fun deleteApplication(id: Int)
}
