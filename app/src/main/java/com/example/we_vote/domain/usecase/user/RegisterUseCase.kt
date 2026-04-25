package com.example.we_vote.domain.usecase.user

import com.example.we_vote.domain.model.User
import com.example.we_vote.domain.repository.UserRepository

class RegisterUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(user: User) = repository.register(user)
}
