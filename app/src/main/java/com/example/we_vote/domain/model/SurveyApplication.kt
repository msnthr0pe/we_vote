package com.example.we_vote.domain.model

data class SurveyApplication(
    val id: Int,
    val title: String,
    val firstChoice: String,
    val secondChoice: String,
    val thirdChoice: String,
    val status: ApplicationStatus,
    val userEmail: String,
)
