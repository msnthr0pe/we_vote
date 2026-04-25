package com.example.we_vote.domain.usecase.user

import com.example.we_vote.domain.model.User
import com.example.we_vote.domain.repository.UserRepository

class GetUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(email: String, password: String): User =
        repository.getUser(email, password)
}
