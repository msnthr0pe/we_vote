package com.example.we_vote.domain.repository

import com.example.we_vote.domain.model.User

interface UserRepository {
    suspend fun login(email: String, password: String): Boolean
    suspend fun getUser(email: String, password: String): User
    suspend fun register(user: User)
    suspend fun updateUser(user: User)
}
