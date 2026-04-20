package com.example.we_vote.ktor

import kotlinx.serialization.Serializable

class DTOs {
    @Serializable
    data class CredentialsDTO(
        val email: String,
        val password: String
    )

    @Serializable
    data class UserDTO (
        val name: String,
        val email: String,
        val dob: String,
        val city: String,
        val password: String,
        val access: String,
    )

    @Serializable
    data class SurveyDTO (
        val id: Int,
        val title: String,
        val firstChoice: String,
        val secondChoice: String,
        val thirdChoice: String,
    )

    @Serializable
    data class TitleDTO (
        val title: String
    )

    @Serializable
    data class UsersSurveysDTO (
        val userEmail: String,
        val surveyId: Int,
        val vote: Int,
    )

    @Serializable
    data class SurveyVotesDTO (
        val votes: Map<Int, Int>,
        val votesPercentage: Map<Int, Int>
    )

    @Serializable
    data class SurveyIdRequest(
        val id: Int
    )

    data class ApplicationDTO(
        val id: Int,
        val title: String,
        val firstChoice: String,
        val secondChoice: String,
        val thirdChoice: String,
        val status: ApplicationStatus,
        val userEmail: String
    )

    data class EmailDTO(
        val email: String
    )

    data class ApplicationStatusUpdateDTO(
        val id: Int,
        val status: String
    )
}

enum class ApplicationStatus(val displayName: String) {
    PENDING("Опрос подан на рассмотрение"),
    ACCEPTED("Опрос принят"),
    REJECTED("Опрос отклонён")
}