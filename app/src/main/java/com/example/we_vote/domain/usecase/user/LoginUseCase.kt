package com.example.we_vote.domain.usecase.user

import com.example.we_vote.domain.repository.UserRepository

class LoginUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(email: String, password: String): Boolean =
        repository.login(email, password)
}
