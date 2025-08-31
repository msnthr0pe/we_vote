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
}