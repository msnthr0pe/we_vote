package com.example.we_vote.domain.usecase.survey

import com.example.we_vote.domain.model.Survey
import com.example.we_vote.domain.repository.SurveyRepository

class GetArchivedSurveysUseCase(private val repository: SurveyRepository) {
    suspend operator fun invoke(city: String): List<Survey> = repository.getArchivedSurveys(city)
}
