package com.example.we_vote.domain.usecase.application

import com.example.we_vote.domain.model.SurveyApplication
import com.example.we_vote.domain.repository.ApplicationRepository

class GetApplicationsUseCase(private val repository: ApplicationRepository) {
    suspend operator fun invoke(): List<SurveyApplication> = repository.getApplications()
}
