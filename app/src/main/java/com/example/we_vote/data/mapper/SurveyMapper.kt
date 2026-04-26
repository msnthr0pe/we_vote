package com.example.we_vote.data.mapper

import com.example.we_vote.data.remote.dto.SurveyDto
import com.example.we_vote.data.remote.dto.SurveyVotesDto
import com.example.we_vote.domain.model.Survey
import com.example.we_vote.domain.model.SurveyVotes

fun SurveyDto.toDomain() = Survey(
    id = id,
    title = title,
    firstChoice = firstChoice,
    secondChoice = secondChoice,
    thirdChoice = thirdChoice,
    city = city,
)

fun Survey.toDto() = SurveyDto(
    id = id,
    title = title,
    firstChoice = firstChoice,
    secondChoice = secondChoice,
    thirdChoice = thirdChoice,
    city = city,
)

fun SurveyVotesDto.toDomain() = SurveyVotes(
    votes = votes,
    votesPercentage = votesPercentage,
)
