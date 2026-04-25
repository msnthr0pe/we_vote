package com.example.we_vote.domain.usecase.application

import com.example.we_vote.domain.model.SurveyApplication
import com.example.we_vote.domain.repository.ApplicationRepository

class AddApplicationUseCase(private val repository: ApplicationRepository) {
    suspend operator fun invoke(application: SurveyApplication) =
        repository.addApplication(application)
}
