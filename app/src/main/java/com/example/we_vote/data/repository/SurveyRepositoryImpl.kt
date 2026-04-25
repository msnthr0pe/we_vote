package com.example.we_vote.data.repository

import com.example.we_vote.data.mapper.toDomain
import com.example.we_vote.data.mapper.toDto
import com.example.we_vote.data.remote.api.AuthApi
import com.example.we_vote.data.remote.dto.SurveyIdDto
import com.example.we_vote.data.remote.dto.TitleDto
import com.example.we_vote.data.remote.dto.UsersSurveysDto
import com.example.we_vote.domain.model.Survey
import com.example.we_vote.domain.model.SurveyVotes
import com.example.we_vote.domain.repository.SurveyRepository

class SurveyRepositoryImpl(private val api: AuthApi) : SurveyRepository {

    override suspend fun getSurveys(): List<Survey> =
        api.getSurveys().map { it.toDomain() }

    override suspend fun getArchivedSurveys(): List<Survey> =
        api.getArchivedSurveys().map { it.toDomain() }

    override suspend fun getSurveyVotes(surveyId: Int): SurveyVotes? =
        runCatching { api.getSurveyVotes(SurveyIdDto(surveyId)).toDomain() }.getOrNull()

    override suspend fun vote(userEmail: String, surveyId: Int, voteOption: Int) {
        api.addUserSurvey(UsersSurveysDto(userEmail, surveyId, voteOption))
    }

    override suspend fun archiveSurvey(title: String) {
        val response = api.archiveSurvey(TitleDto(title))
        if (!response.isSuccessful) error("Archive failed: ${response.code()}")
    }

    override suspend fun addSurvey(survey: Survey) {
        val response = api.addSurvey(survey.toDto())
        if (!response.isSuccessful) error("Add survey failed: ${response.code()}")
    }

    override suspend fun deleteSurvey(surveyId: Int) {
        val response = api.deleteSurvey(SurveyIdDto(surveyId))
        if (!response.isSuccessful) error("Delete survey failed: ${response.code()}")
    }
}
