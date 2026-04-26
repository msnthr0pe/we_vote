package com.example.we_vote.data.mapper

import com.example.we_vote.data.remote.dto.ApplicationDto
import com.example.we_vote.domain.model.ApplicationStatus
import com.example.we_vote.domain.model.SurveyApplication

fun ApplicationDto.toDomain() = SurveyApplication(
    id = id,
    title = title,
    firstChoice = firstChoice,
    secondChoice = secondChoice,
    thirdChoice = thirdChoice,
    status = runCatching { ApplicationStatus.valueOf(status) }.getOrDefault(ApplicationStatus.PENDING),
    userEmail = userEmail,
    userCity = userCity,
)

fun SurveyApplication.toDto() = ApplicationDto(
    id = id,
    title = title,
    firstChoice = firstChoice,
    secondChoice = secondChoice,
    thirdChoice = thirdChoice,
    status = status.name,
    userEmail = userEmail,
    userCity = userCity,
)
