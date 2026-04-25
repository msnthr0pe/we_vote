package com.example.we_vote.domain.usecase.survey

import com.example.we_vote.domain.repository.SurveyRepository

class ArchiveSurveyUseCase(private val repository: SurveyRepository) {
    suspend operator fun invoke(title: String) = repository.archiveSurvey(title)
}
