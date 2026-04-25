package com.example.we_vote.domain.usecase.survey

import com.example.we_vote.domain.model.Survey
import com.example.we_vote.domain.repository.SurveyRepository

class AddSurveyUseCase(private val repository: SurveyRepository) {
    suspend operator fun invoke(survey: Survey) = repository.addSurvey(survey)
}
