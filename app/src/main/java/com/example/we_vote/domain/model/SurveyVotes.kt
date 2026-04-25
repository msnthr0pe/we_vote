package com.example.we_vote.domain.model

data class SurveyVotes(
    val votes: Map<Int, Int>,
    val votesPercentage: Map<Int, Int>,
)
