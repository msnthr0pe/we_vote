package com.example.we_vote.domain.usecase.survey

import com.example.we_vote.domain.model.Survey
import com.example.we_vote.domain.repository.SurveyRepository

class GetSurveysUseCase(private val repository: SurveyRepository) {
    suspend operator fun invoke(city: String): List<Survey> = repository.getSurveys(city)
}
