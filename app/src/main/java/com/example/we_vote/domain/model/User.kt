package com.example.we_vote.domain.model

data class User(
    val name: String,
    val email: String,
    val dob: String,
    val city: String,
    val password: String,
    val access: String,
)
