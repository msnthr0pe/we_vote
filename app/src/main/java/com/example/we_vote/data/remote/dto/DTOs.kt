package com.example.we_vote.data.remote.dto

data class CredentialsDto(
    val email: String,
    val password: String,
)

data class UserDto(
    val name: String,
    val email: String,
    val dob: String,
    val city: String,
    val password: String,
    val access: String,
)

data class CityDto(val name: String)

data class SurveyDto(
    val id: Int,
    val title: String,
    val firstChoice: String,
    val secondChoice: String,
    val thirdChoice: String,
    val city: String,
)

data class TitleDto(val title: String)

data class UsersSurveysDto(
    val userEmail: String,
    val surveyId: Int,
    val vote: Int,
)

data class SurveyVotesDto(
    val votes: Map<Int, Int>,
    val votesPercentage: Map<Int, Int>,
)

data class SurveyIdDto(val id: Int)

data class ApplicationDto(
    val id: Int,
    val title: String,
    val firstChoice: String,
    val secondChoice: String,
    val thirdChoice: String,
    val status: String,
    val userEmail: String,
    val userCity: String = "",
)

data class EmailDto(val email: String)

data class ApplicationStatusUpdateDto(
    val id: Int,
    val status: String,
)

data class ApplicationIdDto(val id: Int)
