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
    data class UsersSurveysDTO (
        val userEmail: String,
        val surveyId: Int,
    )
}