package com.example.we_vote.domain.repository

import com.example.we_vote.domain.model.Survey
import com.example.we_vote.domain.model.SurveyVotes

interface SurveyRepository {
    suspend fun getSurveys(): List<Survey>
    suspend fun getArchivedSurveys(): List<Survey>
    suspend fun getSurveyVotes(surveyId: Int): SurveyVotes?
    suspend fun vote(userEmail: String, surveyId: Int, voteOption: Int)
    suspend fun archiveSurvey(title: String)
    suspend fun addSurvey(survey: Survey)
    suspend fun deleteSurvey(surveyId: Int)
}
