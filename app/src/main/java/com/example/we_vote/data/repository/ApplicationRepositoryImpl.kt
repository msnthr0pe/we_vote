package com.example.we_vote.data.repository

import com.example.we_vote.data.mapper.toDomain
import com.example.we_vote.data.mapper.toDto
import com.example.we_vote.data.remote.api.AuthApi
import com.example.we_vote.data.remote.dto.ApplicationIdDto
import com.example.we_vote.data.remote.dto.ApplicationStatusUpdateDto
import com.example.we_vote.data.remote.dto.EmailDto
import com.example.we_vote.domain.model.ApplicationStatus
import com.example.we_vote.domain.model.SurveyApplication
import com.example.we_vote.domain.repository.ApplicationRepository

class ApplicationRepositoryImpl(private val api: AuthApi) : ApplicationRepository {

    override suspend fun getApplications(): List<SurveyApplication> =
        api.getApplications().map { it.toDomain() }

    override suspend fun getUserApplications(email: String): List<SurveyApplication> =
        api.getUserApplications(EmailDto(email)).map { it.toDomain() }

    override suspend fun addApplication(application: SurveyApplication) {
        val response = api.addApplication(application.toDto())
        if (!response.isSuccessful) error("Add application failed: ${response.code()}")
    }

    override suspend fun updateApplicationStatus(id: Int, status: ApplicationStatus) {
        val response = api.updateApplication(ApplicationStatusUpdateDto(id, status.name))
        if (!response.isSuccessful) error("Update application failed: ${response.code()}")
    }

    override suspend fun deleteApplication(id: Int) {
        val response = api.deleteApplication(ApplicationIdDto(id))
        if (!response.isSuccessful) error("Delete application failed: ${response.code()}")
    }
}
