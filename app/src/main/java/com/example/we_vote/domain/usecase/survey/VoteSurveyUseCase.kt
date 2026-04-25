package com.example.we_vote.domain.usecase.survey

import com.example.we_vote.domain.repository.SurveyRepository

class VoteSurveyUseCase(private val repository: SurveyRepository) {
    suspend operator fun invoke(userEmail: String, surveyId: Int, voteOption: Int) =
        repository.vote(userEmail, surveyId, voteOption)
}
