package com.example.we_vote.data.repository

import com.example.we_vote.data.mapper.toDomain
import com.example.we_vote.data.mapper.toDto
import com.example.we_vote.data.remote.api.AuthApi
import com.example.we_vote.data.remote.dto.CredentialsDto
import com.example.we_vote.domain.model.User
import com.example.we_vote.domain.repository.UserRepository

class UserRepositoryImpl(private val api: AuthApi) : UserRepository {

    override suspend fun login(email: String, password: String): Boolean =
        api.login(CredentialsDto(email, password)).isSuccessful

    override suspend fun getUser(email: String, password: String): User =
        api.getUser(CredentialsDto(email, password)).toDomain()

    override suspend fun register(user: User) {
        val response = api.register(user.toDto())
        if (!response.isSuccessful) error("Registration failed: ${response.code()}")
    }

    override suspend fun updateUser(user: User) {
        val response = api.updateUser(user.toDto())
        if (!response.isSuccessful) error("Update user failed: ${response.code()}")
    }
}
