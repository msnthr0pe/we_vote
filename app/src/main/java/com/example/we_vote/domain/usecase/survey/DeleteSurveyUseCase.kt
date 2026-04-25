package com.example.we_vote.domain.usecase.survey

import com.example.we_vote.domain.repository.SurveyRepository

class DeleteSurveyUseCase(private val repository: SurveyRepository) {
    suspend operator fun invoke(surveyId: Int) = repository.deleteSurvey(surveyId)
}
