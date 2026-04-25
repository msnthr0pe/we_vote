package com.example.we_vote.domain.usecase.survey

import com.example.we_vote.domain.model.SurveyVotes
import com.example.we_vote.domain.repository.SurveyRepository

class GetSurveyVotesUseCase(private val repository: SurveyRepository) {
    suspend operator fun invoke(surveyId: Int): SurveyVotes? = repository.getSurveyVotes(surveyId)
}
